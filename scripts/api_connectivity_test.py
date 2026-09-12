#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
API连通性测试脚本（2026-09 更新，对齐当前接口契约）

变更说明:
- 验证码接口: GET /api/auth/captcha（原 /api/public/captcha 已废弃）
- 登录参数: username / password / captchaKey / captchaCode（原 phone / captcha 已废弃）
- 默认账号: admin / 123456（可用环境变量 AKSU_USER / AKSU_PASS 覆盖）
- 接口清单: 按后端 OpenAPI(/v3/api-docs) 147 个路径中选取各模块代表性接口
- 验证码识别: 从后端控制台日志 backend.log 提取（CaptchaService 在 INFO 级
  打印 key/code），日志路径可用环境变量 AKSU_BACKEND_LOG 覆盖

安全约束: 本脚本仅允许访问本机回环地址(localhost/127.0.0.1)的后端服务。
"""
import requests
import json
import os
import re
import sys
import time
from pathlib import Path

BASE_URL = os.environ.get("AKSU_BASE_URL", "http://localhost:8080")
USERNAME = os.environ.get("AKSU_USER", "admin")
PASSWORD = os.environ.get("AKSU_PASS", "123456")
BACKEND_LOG = os.environ.get(
    "AKSU_BACKEND_LOG",
    str(Path(__file__).resolve().parent.parent / "backend.log"),
)

TOKEN = None

CAPTCHA_LOG_PATTERN = re.compile(r"key=(\w{32}), code=(\w+)")


def assert_local_target():
    """仅允许回环地址，避免脚本被改作 SSRF 探针"""
    from urllib.parse import urlparse
    host = urlparse(BASE_URL).hostname or ""
    if host not in ("localhost", "127.0.0.1", "::1"):
        raise SystemExit(f"拒绝访问非本机地址: {host}（本脚本仅用于本地连通性测试）")


def fetch_captcha_code(captcha_key, wait_seconds=10):
    """从后端日志中提取验证码明文（存在 flush 延迟，需轮询）"""
    log_path = Path(BACKEND_LOG)
    if not log_path.exists():
        print(f"  [warn] 后端日志不存在: {log_path}（无法自动识别验证码）")
        return None
    for _ in range(wait_seconds):
        time.sleep(1)
        try:
            text = log_path.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue
        for line in reversed(text.splitlines()):
            m = CAPTCHA_LOG_PATTERN.search(line)
            if m and m.group(1) == captcha_key:
                return m.group(2)
    return None


def test_login():
    """测试登录（含图形验证码闭环）"""
    print("\n" + "=" * 60)
    print("[1] 测试登录接口")
    print("=" * 60)

    captcha_key = None
    try:
        resp = requests.get(f"{BASE_URL}/api/auth/captcha", timeout=5)
        print(f"  验证码接口: HTTP {resp.status_code}")
        if resp.status_code == 200:
            captcha_key = resp.json().get("data", {}).get("captchaKey")
            print(f"  验证码key: {captcha_key}")
    except Exception as e:
        print(f"  验证码获取失败: {e}")

    captcha_code = fetch_captcha_code(captcha_key) if captcha_key else None
    if not captcha_code:
        print("  无法从日志识别验证码，登录测试中止")
        return False

    login_data = {
        "username": USERNAME,
        "password": PASSWORD,
        "captchaKey": captcha_key,
        "captchaCode": captcha_code,
    }
    try:
        resp = requests.post(f"{BASE_URL}/api/auth/login", json=login_data, timeout=10)
        data = resp.json()
        if data.get("code") == 200:
            global TOKEN
            TOKEN = data.get("data", {}).get("token")
            print(f"  登录成功: {USERNAME}，Token已获取")
            return True
        print(f"  登录失败: {data.get('message')}")
        return False
    except Exception as e:
        print(f"  登录异常: {e}")
        return False


def test_api(endpoint, desc=""):
    """测试GET接口"""
    headers = {"Authorization": f"Bearer {TOKEN}"} if TOKEN else {}
    try:
        resp = requests.get(f"{BASE_URL}{endpoint}", headers=headers, timeout=10)
        result = resp.json() if resp.status_code != 404 else {}
        status = "OK" if resp.status_code == 200 and result.get("code") == 200 else "FAIL"
        data = result.get("data")
        extra = ""
        if isinstance(data, list):
            extra = f"-> {len(data)} 条"
        elif isinstance(data, dict):
            for k in ("list", "records", "content"):
                if isinstance(data.get(k), list):
                    extra = f"-> {len(data[k])} 条"
                    break
            else:
                extra = "-> dict" if "total" not in data else f"-> total={data['total']}"
        print(f"  {desc:<24} [{status}] HTTP {resp.status_code} {extra}")
        return status == "OK"
    except Exception as e:
        print(f"  {desc:<24} [ERR] {str(e)[:60]}")
        return False


def main():
    print("=" * 60)
    print("阿克苏监管平台 API 连通性测试")
    print("=" * 60)
    assert_local_target()

    try:
        resp = requests.get(f"{BASE_URL}/v3/api-docs", timeout=5)
        total = len(resp.json().get("paths", {}))
        print(f"\n[0] 后端服务运行中，OpenAPI 共 {total} 个路径 (HTTP {resp.status_code})")
    except Exception as e:
        print(f"\n后端服务未启动: {e}\n请先启动后端服务!")
        return

    login_ok = test_login()

    groups = [
        ("认证", [("/api/auth/userinfo", "当前用户信息")]),
        ("数据看板", [
            ("/api/admin/dashboard/overview", "总览"),
            ("/api/admin/dashboard/area-view", "区域视图"),
            ("/api/admin/dashboard/industry-view", "行业视图"),
            ("/api/admin/dashboard/risk-profile", "风险画像"),
        ]),
        ("企业管理", [
            ("/api/admin/enterprise/list", "企业列表"),
            ("/api/admin/enterprise-registration/pending", "待审核注册"),
            ("/api/admin/enterprise-registration/list", "全部注册记录"),
        ]),
        ("系统管理", [
            ("/api/admin/system/role/list", "角色列表"),
            ("/api/admin/system/permission/list", "权限列表"),
            ("/api/admin/system/user/list", "用户列表"),
            ("/api/admin/system/org/tree", "组织架构树"),
            ("/api/admin/system/position/list", "岗位列表"),
            ("/api/admin/system/job-title/list", "职务列表"),
            ("/api/admin/system/enterprise-type/list", "企业类型"),
            ("/api/admin/system/log/list", "操作日志"),
        ]),
        ("执法检查", [
            ("/api/admin/inspection/list", "检查记录"),
            ("/api/admin/inspection/stats", "检查统计"),
        ]),
        ("整改与申诉", [
            ("/api/admin/rectification/list", "整改通知"),
            ("/api/admin/rectification/stats", "整改统计"),
            ("/api/admin/appeal/list", "申诉列表"),
            ("/api/admin/appeal/statistics", "申诉统计"),
        ]),
        ("报告与任务", [
            ("/api/admin/report/list", "合规报告"),
            ("/api/admin/report/expiring", "临期报告"),
            ("/api/admin/task/list", "任务列表"),
        ]),
        ("消息与预警", [
            ("/api/message/list", "消息列表"),
            ("/api/inspector/alert/list", "预警列表"),
            ("/api/inspector/alert/statistics", "预警统计"),
            ("/api/inspector/alert/types", "预警类型"),
            ("/api/inspector/task/statistics", "执法端任务统计"),
        ]),
    ]

    passed = failed = 0
    for group, eps in groups:
        print("\n" + "-" * 60)
        print(f"[{group}]")
        print("-" * 60)
        for endpoint, desc in eps:
            if test_api(endpoint, desc):
                passed += 1
            else:
                failed += 1

    print("\n" + "=" * 60)
    print(f"连通性测试完成: {passed} 通过 / {failed} 失败"
          + ("（含登录闭环）" if login_ok else "（登录失败，鉴权接口结果不可信）"))
    print("=" * 60)
    return 0 if failed == 0 and login_ok else 1


if __name__ == "__main__":
    sys.exit(main() or 0)
