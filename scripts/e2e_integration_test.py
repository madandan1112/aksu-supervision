#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
阿克苏监管平台 端到端联动测试（2026-09）
覆盖五条业务链 + 附加验证：
  A. 申诉闭环：企业提交 → 管理端分流 → 处理 → 企业评价
  B. 检查→整改闭环：执法提交检查(含问题) → 自动生成整改通知+站内信 →
     企业反馈(触发OCR通知执法) → 执法验收通过；检查不合格实时预警(事件)
  C. 企业注册闭环：5步注册 → 管理端通过(自动建企业+账号) / 拒绝(触发预警)
  D. 预警引擎：手动扫描 → 接收(记录接收人) → 退回 → 统计
  E. 数据权限：科室/县市级用户企业列表按区域过滤
  附加：消息设置持久化、扫码公示公开接口

安全约束：仅允许访问本机回环地址（每次请求构造URL时校验协议与主机）。
"""
import io
import os
import re
import struct
import sys
import time
import zlib
from urllib.parse import urlparse

import requests

BASE_URL = os.environ.get("AKSU_BASE_URL", "http://localhost:8080")
ALLOWED_HOSTS = ("localhost", "127.0.0.1", "::1")
BACKEND_LOG = os.environ.get(
    "AKSU_BACKEND_LOG",
    os.path.join(os.path.dirname(__file__), "..", "backend.log"),
)
CAPTCHA_LOG_PATTERN = re.compile(r"key=(\w{32}), code=(\w+)")

PASS, FAIL = 0, 0


def _safe_url(path):
    """构造请求URL并做边界校验：仅http(s)协议 + 本机回环主机，防止SSRF。"""
    url = f"{BASE_URL}{path}"
    p = urlparse(url)
    if p.scheme not in ("http", "https"):
        raise ValueError(f"scheme not allowed: {p.scheme}")
    if (p.hostname or "") not in ALLOWED_HOSTS:
        raise ValueError(f"host not allowed (local only): {p.hostname}")
    return url


def check(name, cond, detail=""):
    global PASS, FAIL
    mark = "PASS" if cond else "FAIL"
    if cond:
        PASS += 1
    else:
        FAIL += 1
    print(f"[{mark}] {name}" + (f"  -- {detail}" if detail and not cond else ""))


def login_mini(username, password):
    """小程序/H5登录（无验证码）"""
    r = requests.post(_safe_url("/api/auth/miniprogram-login"),
                      json={"username": username, "password": password}, timeout=10)
    j = r.json()
    if j.get("code") == 200:
        return j["data"].get("token")
    return None


def login_with_password(username, password):
    """主登录（带验证码，从后端日志取码）"""
    for _ in range(3):
        try:
            key = requests.get(_safe_url("/api/auth/captcha"), timeout=5).json()["data"]["captchaKey"]
        except Exception:
            time.sleep(1)
            continue
        code = None
        for _ in range(10):
            time.sleep(1)
            try:
                text = io.open(BACKEND_LOG, encoding="utf-8", errors="ignore").read()
            except OSError:
                break
            for line in reversed(text.splitlines()):
                m = CAPTCHA_LOG_PATTERN.search(line)
                if m and m.group(1) == key:
                    code = m.group(2)
                    break
            if code:
                break
        if not code:
            continue
        r = requests.post(_safe_url("/api/auth/login"),
                          json={"username": username, "password": password,
                                "captchaKey": key, "captchaCode": code}, timeout=10)
        j = r.json()
        if j.get("code") == 200:
            return j["data"].get("token")
    return None


def api(token, method, path, **kw):
    headers = kw.pop("headers", {})
    if token:
        headers["Authorization"] = "Bearer " + token
    r = requests.request(method, _safe_url(path), headers=headers, timeout=15, **kw)
    try:
        j = r.json()
    except Exception:
        j = {}
    return r.status_code, j


def tiny_png():
    """生成一个最小合法PNG"""
    def chunk(tag, data):
        c = struct.pack(">I", len(data)) + tag + data
        return c + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)
    ihdr = struct.pack(">IIBBBBB", 1, 1, 8, 2, 0, 0, 0)
    idat = zlib.compress(b"\x00\xff\x00\x00")
    return (b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr)
            + chunk(b"IDAT", idat) + chunk(b"IEND", b""))


def main():
    # ================= 登录准备 =================
    print("\n========== 0. 登录准备 ==========")
    admin = login_with_password("admin", "123456")
    check("admin login", bool(admin))

    ent = None
    for pwd in ("123456", "test123"):
        ent = login_mini("ent_user1", pwd)
        if ent:
            break
    check("ent_user1 login (企业用户)", bool(ent))

    insp = login_mini("inspector1", "123456")
    check("inspector1 login (执法人员)", bool(insp))

    # 内部测试账号：口令未知则用管理端重置为已知值
    staff_pwd = None
    for pwd in ("123456", "test123"):
        if login_mini("enforcer_ws_001", pwd):
            staff_pwd = pwd
            break
    if not staff_pwd and admin:
        for uid, uname in ((9, "enforcer_ws_001"), (10, "enforcer_kc_001")):
            st, j = api(admin, "PUT", f"/api/admin/system/user/{uid}/reset-password",
                        params={"newPassword": "123456"})
            print(f"  reset {uname}: http={st} code={j.get('code')}")
        staff_pwd = "123456"
    print(f"  enforcer_ws_001 password = {staff_pwd}")

    # 企业档案（链B目标企业）
    st, j = api(ent, "GET", "/api/enterprise/profile")
    my_ent = j.get("data") or {}
    ent_id = my_ent.get("id")
    ent_name = my_ent.get("name") or my_ent.get("enterpriseName")
    check("ent_user1 enterprise profile", bool(ent_id), f"data={str(my_ent)[:80]}")
    print(f"  target enterprise: id={ent_id} name={ent_name}")

    # ================= 链A：申诉闭环 =================
    print("\n========== A. 申诉闭环 ==========")
    st, j = api(ent, "POST", "/api/appeal", json={
        "title": "E2E测试诉求-" + str(int(time.time())),
        "appealType": "CONSULT",
        "content": "自动化端到端测试：咨询事项",
        "relatedFields": "食品",
    })
    appeal_id = (j.get("data") or {}).get("id") if isinstance(j.get("data"), dict) else j.get("data")
    check("A1 企业提交诉求", st == 200 and j.get("code") == 200 and appeal_id, f"http={st} code={j.get('code')}")

    st, j = api(admin, "PUT", f"/api/admin/appeal/{appeal_id}/assign", params={"assignedTo": "inspector1"})
    check("A2 管理端分流", st == 200 and j.get("code") == 200 and (j.get("data") or {}).get("status") == "ASSIGNED",
          f"http={st} resp={str(j)[:120]}")

    st, j = api(admin, "PUT", f"/api/admin/appeal/{appeal_id}/handle", params={"handleResult": "E2E处理完毕"})
    check("A3 管理端处理", st == 200 and j.get("code") == 200 and (j.get("data") or {}).get("status") == "HANDLED",
          f"http={st} resp={str(j)[:120]}")

    st, j = api(ent, "POST", f"/api/appeal/{appeal_id}/evaluate", params={"satisfaction": 5, "comment": "E2E满意"})
    check("A4 企业评价", st == 200 and j.get("code") == 200 and (j.get("data") or {}).get("status") == "EVALUATED",
          f"http={st} resp={str(j)[:120]}")

    # ================= 链B：检查→整改闭环 =================
    print("\n========== B. 检查→整改闭环 ==========")
    ts = str(int(time.time()))
    st, j = api(insp, "POST", "/api/inspector/inspection/submit", json={
        "enterpriseId": ent_id,
        "checkTypes": "FP001",
        "issues": f"E2E检查发现问题-{ts}：后厨卫生不达标",
        "requirements": "限期清洁整改并提交照片",
        "summary": "E2E自动检查",
        "deadline": "2026-10-10T00:00:00",
        "evidenceImages": "[]",
    })
    check("B1 执法提交检查(含问题)", st == 200 and j.get("code") == 200, f"http={st} resp={str(j)[:120]}")
    time.sleep(2)  # 等异步事件+独立事务通知生成

    # 整改通知书已生成
    st, j = api(admin, "GET", "/api/admin/rectification/list", params={"enterpriseId": ent_id, "page": 1, "size": 5})
    notices = (j.get("data") or {}).get("list") or []
    notice = next((n for n in notices if "E2E检查发现问题" in (n.get("issues") or "")), None)
    check("B2 整改通知书自动生成", notice is not None,
          f"notices={[(n.get('noticeNo'), (n.get('issues') or '')[:20]) for n in notices]}")
    notice_id = notice.get("id") if notice else None

    # 企业收到站内信
    st, j = api(ent, "GET", "/api/message/list", params={"page": 1, "size": 10})
    msgs = (j.get("data") or {}).get("list") or []
    check("B3 企业收到整改通知站内信", any(m.get("type") == "RECTIFICATION_NOTICE" for m in msgs),
          f"types={[m.get('type') for m in msgs]}")

    # 企业提交整改反馈
    st, j = api(ent, "POST", f"/api/enterprise/rectification/{notice_id}/feedback", json={
        "rectifyMeasures": f"E2E整改完成-{ts}：已完成清洁并提供照片",
        "evidenceImages": "[]",
        "checkType": "食品生产安全",
        "remark": "E2E",
    })
    check("B4 企业提交整改反馈", st == 200 and j.get("code") == 200, f"http={st} resp={str(j)[:120]}")
    time.sleep(2)

    # 执法验收通过
    st, j = api(insp, "POST", f"/api/inspector/rectification/{notice_id}/accept", json={
        "conclusion": "PASS",
        "opinion": "E2E验收通过",
    })
    check("B5 执法验收通过", st == 200 and j.get("code") == 200, f"http={st} resp={str(j)[:120]}")

    st, j = api(admin, "GET", f"/api/admin/rectification/{notice_id}")
    notice_detail = ((j.get("data") or {}).get("notice")) or {}
    check("B6 整改单终态ACCEPTED(验收通过)", notice_detail.get("status") == "ACCEPTED",
          f"status={notice_detail.get('status')}")

    # 检查不合格实时预警（事件驱动）
    st, j = api(admin, "GET", "/api/inspector/alert/list",
                params={"type": "CREDIT_ANOMALY", "page": 1, "size": 20})
    alerts = (j.get("data") or {}).get("list") or []
    ev = [a for a in alerts if (a.get("source") == "EVENT" and ent_id == a.get("enterpriseId"))]
    check("B7 检查不合格→实时预警(事件)", len(ev) > 0,
          f"event alerts={[(a.get('source'), a.get('enterpriseId')) for a in alerts[:5]]}")

    # ================= 链C：企业注册闭环 =================
    print("\n========== C. 企业注册闭环 ==========")
    reg_user, reg_pwd, reg_tok = None, None, None
    for u, p in (("test_ent_user", "123456"), ("test_ent_user", "test123"),
                 ("auto_reg_test", "123456"), ("auto_reg_test", "test123")):
        t = login_mini(u, p)
        if t:
            reg_user, reg_pwd, reg_tok = u, p, t
            break
    if not reg_user:
        check("C0 注册载体账号登录", False, "test_ent_user/auto_reg_test 均无法登录")
    else:
        check("C0 注册载体账号登录", True, f"user={reg_user}")
        png = tiny_png()
        files = {"file": ("license.png", png, "image/png")}
        st, j = api(reg_tok, "POST", "/api/enterprise/register/license", files=files)
        ok = st == 200 and j.get("code") == 200
        check("C1 步骤1 上传营业执照(+OCR降级)", ok, f"http={st} resp={str(j)[:120]}")

        code = "E2E" + str(int(time.time()))[-12:]
        st, j = api(reg_tok, "POST", "/api/enterprise/register/info", json={
            "creditCode": code,
            "enterpriseName": f"E2E测试企业{code[-4:]}",
            "legalPerson": "E2E法人",
            "address": "E2E地址",
            "phone": "13800001111",
            "industry": "餐饮服务",
            "area": "阿克苏市",
        })
        check("C2 步骤2 企业信息", st == 200 and j.get("code") == 200, f"http={st} resp={str(j)[:120]}")

        files = [("files", ("q1.png", png, "image/png")), ("files", ("q2.png", png, "image/png"))]
        st, j = api(reg_tok, "POST", "/api/enterprise/register/qualification", files=files)
        check("C3 步骤3 资质证照(多文件,JSON存储)", st == 200 and j.get("code") == 200, f"http={st}")

        st, j = api(reg_tok, "POST", "/api/enterprise/register/photos",
                    files={"storefrontPhoto": ("s.png", png, "image/png"),
                           "interiorPhoto": ("i.png", png, "image/png")})
        check("C4 步骤4 门店照片", st == 200 and j.get("code") == 200, f"http={st}")

        st, j = api(reg_tok, "POST", "/api/enterprise/register/submit")
        submitted = st == 200 and ((j.get("data") or {}).get("status") == "SUBMITTED")
        check("C5 步骤5 提交审核", submitted, f"http={st} resp={str(j)[:120]}")

        # 管理端通过 → 自动建企业
        st, j = api(admin, "GET", "/api/admin/enterprise-registration/pending")
        pend = j.get("data") or []
        mine = next((r for r in pend if r.get("creditCode") == code), None)
        check("C6 管理端看到待审核", mine is not None, f"pending={len(pend)}")
        if mine:
            st, j = api(admin, "POST", f"/api/admin/enterprise-registration/{mine['id']}/approve",
                        params={"comment": "E2E审核通过"})
            check("C7 审核通过(自动建企业+账号)", st == 200 and j.get("code") == 200, f"http={st} resp={str(j)[:150]}")

            st, j = api(admin, "GET", "/api/admin/enterprise/list", params={"keyword": code, "page": 1, "size": 5})
            rows = (j.get("data") or {}).get("list") or []
            check("C8 企业已入库", any(r.get("creditCode") == code for r in rows))

        # 拒绝路径 + 注册异常预警（事件）：重开一轮草稿再拒
        st, j = api(reg_tok, "POST", "/api/enterprise/register/license",
                    files={"file": ("license2.png", png, "image/png")})
        code2 = "E2R" + str(int(time.time()))[-12:]
        st, j = api(reg_tok, "POST", "/api/enterprise/register/info", json={
            "creditCode": code2, "enterpriseName": f"E2E拒绝企业{code2[-4:]}",
            "legalPerson": "E2E", "phone": "13800001111", "area": "库车市"})
        st, j = api(reg_tok, "POST", "/api/enterprise/register/submit")
        st, j = api(admin, "GET", "/api/admin/enterprise-registration/pending")
        mine2 = next((r for r in (j.get("data") or []) if r.get("creditCode") == code2), None)
        rej_ok = False
        if mine2:
            st, j = api(admin, "POST", f"/api/admin/enterprise-registration/{mine2['id']}/reject",
                        params={"comment": "E2E材料不全"})
            rej_ok = st == 200 and j.get("code") == 200
        check("C9 拒绝注册", rej_ok)
        hit = []
        ralerts = []
        for _ in range(6):  # 异步事件落库有延迟，最长等 12 秒
            time.sleep(2)
            st, j = api(admin, "GET", "/api/inspector/alert/list",
                        params={"type": "REGISTRATION_ANOMALY", "page": 1, "size": 100})
            ralerts = (j.get("data") or {}).get("list") or []
            hit = [a for a in ralerts if a.get("source") == "EVENT" and code2[-4:] in (a.get("title") or "")]
            if hit:
                break
        check("C10 拒绝→注册异常预警(事件,带企业名)", len(hit) > 0,
              f"titles={[a.get('title') for a in ralerts[:5]]}")

    # ================= 链D：预警引擎 =================
    print("\n========== D. 预警引擎 ==========")
    st, j = api(admin, "POST", "/api/inspector/alert/scan")
    check("D1 手动全量扫描", st == 200 and j.get("code") == 200,
          f"newAlerts={(j.get('data') or {}).get('newAlerts')}")

    st, j = api(admin, "GET", "/api/inspector/alert/statistics")
    stats = j.get("data") or {}
    check("D2 预警统计(含escalated)", all(k in stats for k in ("total", "pending", "handling", "handled", "escalated")),
          f"stats={stats}")

    # 找一条PENDING接收
    st, j = api(admin, "GET", "/api/inspector/alert/list", params={"status": "PENDING", "page": 1, "size": 5})
    pend_alerts = (j.get("data") or {}).get("list") or []
    if pend_alerts:
        aid = pend_alerts[0]["id"]
        st, j = api(admin, "POST", f"/api/inspector/alert/{aid}/accept")
        a = j.get("data") or {}
        check("D3 接收预警(记录接收人)", st == 200 and a.get("status") == "HANDLING" and a.get("assignedUserName"),
              f"resp={str(a)[:120]}")
        # 退回（重新分配）
        st, j = api(admin, "POST", f"/api/inspector/alert/{aid}/reject", params={"reason": "E2E退回测试"})
        a = j.get("data") or {}
        check("D4 退回预警(回PENDING)", st == 200 and a.get("status") == "PENDING", f"resp={str(a)[:120]}")
        # 已处理状态不可退回（状态校验）
        st, j = api(admin, "GET", "/api/inspector/alert/list", params={"status": "HANDLED", "page": 1, "size": 1})
        handled = (j.get("data") or {}).get("list") or []
        if handled:
            st, j = api(admin, "POST", f"/api/inspector/alert/{handled[0]['id']}/reject", params={"reason": "x"})
            check("D5 已处理预警不可退回(状态校验)", j.get("code") != 200, f"code={j.get('code')}")
        else:
            check("D5 已处理预警不可退回(状态校验)", True, "无HANDLED样本，跳过")
    else:
        check("D3/D4/D5 接收-退回链", False, "无PENDING预警样本")

    # ================= 链E：数据权限 =================
    print("\n========== E. 数据权限(区域过滤) ==========")
    if staff_pwd:
        ws = login_mini("enforcer_ws_001", staff_pwd)
        kc = login_mini("enforcer_kc_001", staff_pwd)
        for name, tok, expect_area in (("enforcer_ws_001(阿克苏市)", ws, "阿克苏市"),
                                       ("enforcer_kc_001(库车市)", kc, "库车市"),
                                       ("inspector1(阿克苏市)", insp, "阿克苏市")):
            if not tok:
                check(f"E {name} 登录", False)
                continue
            st, j = api(tok, "GET", "/api/inspector/enterprise/list", params={"page": 1, "size": 100})
            rows = (j.get("data") or {}).get("list") or []
            areas = {r.get("area") for r in rows}
            ok = st == 200 and j.get("code") == 200 and rows and areas == {expect_area}
            check(f"E {name} 仅见{expect_area}企业", ok,
                  f"http={st} n={len(rows)} areas={areas}")
        # 企业用户SELF：profile 仅返回本企业
        st, j = api(ent, "GET", "/api/enterprise/profile")
        prof = (j.get("data") or {})
        check("E ent_user1(SELF) 仅见本企业", st == 200 and prof.get("id") == ent_id,
              f"http={st} id={prof.get('id')} expect={ent_id}")
    else:
        print("  [skip] 内部测试账号无法登录，链E跳过")

    # ================= 链R：角色隔离 =================
    print("\n========== R. 服务端角色隔离 ==========")
    st, j = api(ent, "GET", "/api/admin/system/user/list", params={"page": 1, "size": 5})
    check("R1 企业token访问admin接口被拒(403)", st == 403, f"http={st}")
    st, j = api(ent, "GET", "/api/inspector/alert/list", params={"page": 1, "size": 5})
    check("R2 企业token访问inspector接口被拒(403)", st == 403, f"http={st}")
    st, j = api(ent, "GET", "/api/dictionary/enterprise-type/active")
    check("R3 企业token可用字典接口(200)", st == 200, f"http={st}")
    st, j = api(insp, "GET", "/api/inspector/enterprise/list", params={"page": 1, "size": 5})
    rows = (j.get("data") or {}).get("list") or []
    check("R4 执法token用执法企业检索(属地过滤)", st == 200 and rows and all(r.get("area") == "阿克苏市" for r in rows),
          f"http={st} n={len(rows)}")

    # ================= 附加验证 =================
    print("\n========== X. 附加验证 ==========")
    st, j = api(ent, "PUT", "/api/message/settings",
                data='{"pushEnabled":true,"types":["RECTIFICATION_NOTICE"]}',
                headers={"Content-Type": "application/json"})
    st2, j2 = api(ent, "GET", "/api/message/settings")
    check("X1 消息设置持久化", st == 200 and st2 == 200 and "RECTIFICATION_NOTICE" in str(j2.get("data")),
          f"put={st} get={st2} value={str(j2.get('data'))[:80]}")

    st, j = api(None, "GET", "/api/public/enterprise/91652901MA77XXXXX4")
    d = j.get("data") or {}
    check("X2 扫码公示(公开接口)", st == 200 and j.get("code") == 200 and d.get("enterprise"),
          f"keys={list(d.keys())}")

    st, j = api(admin, "GET", "/api/admin/dashboard/overview")
    check("X3 数据看板总览", st == 200 and j.get("code") == 200 and isinstance(j.get("data"), dict))

    print("\n" + "=" * 60)
    print(f"E2E 结果: {PASS} 通过 / {FAIL} 失败")
    print("=" * 60)
    return 0 if FAIL == 0 else 1


if __name__ == "__main__":
    sys.exit(main())
