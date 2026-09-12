import { LayoutDashboard, FileText, Clock, AlertTriangle, ClipboardList, Wrench, Building2, ScrollText, BarChart3, Network, UserCog, ShieldCheck, Users, Scroll, Search, ChevronRight } from 'lucide-react'
import { BrowserFrame } from '../components/frames'
import { Emblem } from '../components/Emblem'
import { LevelBadge } from '../components/wx'

const MENU = [
  { icon: LayoutDashboard, label: '数据看板', on: false, group: '' },
  { icon: FileText, label: '诉求管理', badge: 3, group: '监管业务' },
  { icon: Clock, label: '任务调度', group: '' },
  { icon: AlertTriangle, label: '预警督办', badge: 5, group: '' },
  { icon: ClipboardList, label: '现场检查', group: '执法管理' },
  { icon: Wrench, label: '整改管理', group: '' },
  { icon: Building2, label: '企业档案', group: '数据管理' },
  { icon: ScrollText, label: '报告管理', group: '' },
  { icon: BarChart3, label: '统计分析', group: '' },
  { icon: Network, label: '组织架构', group: '系统管理' },
  { icon: UserCog, label: '后台用户', group: '' },
  { icon: ShieldCheck, label: '角色权限', group: '' },
  { icon: Users, label: '用户管理', badge: 8, group: '' },
  { icon: Scroll, label: '操作日志', group: '' },
]

function AdminShell({ title, active, children }: { title: string; active: string; children: React.ReactNode }) {
  let lastGroup = ''
  return (
    <BrowserFrame title={`阿克苏市场监管执法智慧平台 — ${title}`}>
      <div className="flex h-[840px]">
        <aside className="flex w-[220px] flex-shrink-0 flex-col border-r border-zinc-200 bg-[#F6F6F8] p-2.5">
          <div className="mb-2.5 flex items-center gap-2.5 px-1.5">
            <Emblem size={30} />
            <div className="leading-tight"><b className="text-[13px] text-zinc-900">阿克苏市场监管</b><p className="text-[9px] text-zinc-400">执法智慧平台</p></div>
          </div>
          <div className="mb-2.5 flex items-center gap-1.5 rounded-lg border border-zinc-200 bg-white px-2.5 py-1.5 text-xs text-zinc-400"><Search size={12} />搜索</div>
          <div className="flex-1 space-y-0.5 overflow-hidden text-[13px]">
            {MENU.map((m) => {
              const showGroup = m.group && m.group !== lastGroup ? m.group : null
              lastGroup = m.group || lastGroup
              return (
                <div key={m.label}>
                  {showGroup ? <p className="px-2 pb-1 pt-3 text-[10px] font-bold tracking-wide text-zinc-400">{showGroup}</p> : null}
                  <div className={`flex items-center gap-2 rounded-lg px-2.5 py-[7px] ${active === m.label ? 'bg-[#C8102E]/[.08] font-semibold text-[#C8102E]' : 'text-zinc-600'}`}>
                    <m.icon size={16} strokeWidth={1.8} className={active === m.label ? 'text-[#C8102E]' : 'text-zinc-400'} />
                    {m.label}
                    {m.badge ? <span className="ml-auto rounded-full bg-rose-500 px-1.5 py-px text-[10px] font-bold leading-4 text-white">{m.badge}</span> : null}
                  </div>
                </div>
              )
            })}
          </div>
        </aside>
        <main className="flex-1 overflow-hidden bg-white p-6">{children}</main>
      </div>
    </BrowserFrame>
  )
}

/* ================= 1 数据看板 ================= */
export function AdminDashboard() {
  return (
    <AdminShell title="数据看板" active="数据看板">
      <div className="flex items-center justify-between">
        <h2 className="text-[22px] font-bold tracking-tight text-zinc-900">数据看板</h2>
        <div className="flex gap-2 text-[13px]"><span className="rounded-lg border border-zinc-200 px-3.5 py-1.5 text-zinc-600">导出</span><span className="rounded-lg bg-[#C8102E] px-3.5 py-1.5 font-medium text-white">进入大屏</span></div>
      </div>
      <div className="mt-4 flex items-center gap-5 rounded-xl border border-zinc-200 bg-[#FBFBFD] p-4">
        <div className="flex-1"><b className="text-[15px] text-zinc-900">早上好，超级管理员</b><p className="mt-1 text-xs text-zinc-400">今日待办 5 项 · 待审核注册 8 · 待处理预警 5 · 待分流诉求 3 · 临期报告 2</p></div>
        <span className="rounded-lg bg-[#C8102E] px-3.5 py-1.5 text-[13px] font-medium text-white">处理待办</span>
      </div>
      <div className="mt-3.5 grid grid-cols-6 gap-3">
        {[['企业总数', '40', '本月 +3', 'text-emerald-500'], ['诉求总数', '15', '待分流 3', 'text-zinc-400'], ['检查记录', '10', '本周 +2', 'text-blue-600'], ['活跃预警', '22', '高风险 9', 'text-rose-500'], ['整改通知', '23', '整改中 12', 'text-amber-500'], ['合规报告', '11', '临期 2', 'text-amber-500']].map(([l, n, s, c], i) => (
          <div key={l} className={`rounded-xl border border-zinc-200/80 p-3.5 ${i === 3 ? 'border-t-2 border-t-rose-500' : ''}`}>
            <p className="text-[11px] text-zinc-400">{l}</p>
            <b className="tnum block text-[26px] font-bold leading-tight text-zinc-900">{n}</b>
            <p className={`text-[11px] ${c}`}>{s}</p>
          </div>
        ))}
      </div>
      <div className="mt-3.5 grid flex-1 grid-cols-[1.5fr_1fr] gap-3">
        <div className="rounded-xl border border-zinc-200/80 p-4">
          <div className="flex items-baseline justify-between"><b className="text-sm text-zinc-900">九县市企业分布</b><span className="text-[11px] text-zinc-400">共 40 家</span></div>
          <div className="flex h-[150px] items-end gap-4 px-1 pt-4">
            {[['阿克苏市', 92], ['库车市', 74], ['温宿县', 66], ['沙雅县', 30], ['拜城县', 30], ['新和县', 30], ['乌什县', 20], ['阿瓦提', 20], ['柯坪县', 20]].map(([c, h]) => (
              <div key={c as string} className="flex flex-1 flex-col items-center justify-end">
                <div className="w-full rounded-t-md bg-gradient-to-b from-[#E0364C] to-[#C8102E]" style={{ height: h as number, opacity: (h as number) > 60 ? .95 : .6 }} />
                <p className="mt-1.5 truncate text-[10px] text-zinc-400">{c as string}</p>
              </div>
            ))}
          </div>
        </div>
        <div className="rounded-xl border border-zinc-200/80 p-4">
          <b className="text-sm text-zinc-900">预警等级分布</b>
          <div className="space-y-3.5 pt-5">
            {[['高', 64, 'bg-rose-500', '9'], ['中', 50, 'bg-amber-500', '7'], ['低', 43, 'bg-slate-400', '6']].map(([l, w, c, n]) => (
              <div key={l as string} className="flex items-center gap-2.5 text-xs">
                <span className="w-4 text-zinc-400">{l as string}</span>
                <div className="h-1.5 flex-1 rounded-full bg-slate-100"><div className={`h-full rounded-full ${c}`} style={{ width: `${w}%` }} /></div>
                <b className="w-4 text-zinc-700">{n as string}</b>
              </div>
            ))}
          </div>
        </div>
      </div>
    </AdminShell>
  )
}

/* ================= 2 组织架构 ================= */
export function AdminOrg() {
  return (
    <AdminShell title="组织架构" active="组织架构">
      <div className="flex h-full gap-4">
        <div className="w-[330px] flex-shrink-0 rounded-xl border border-zinc-200/80 p-2.5">
          <p className="px-1.5 pb-2 text-xs text-zinc-400">四级体系 · 131 节点</p>
          <div className="space-y-0.5 text-[13px]">
            <div className="flex items-center gap-2 rounded-md bg-[#C8102E]/[.08] px-2 py-1.5 font-semibold text-[#C8102E]"><ChevronRight size={12} className="rotate-90" />阿克苏地区市场监督管理局</div>
            <div className="ml-5 flex items-center gap-2 px-2 py-1 text-zinc-700"><ChevronRight size={12} className="rotate-90 text-zinc-300" />阿克苏市<span className="ml-auto rounded border border-blue-100 bg-blue-50 px-1.5 text-[10px] text-blue-600">县市</span></div>
            <div className="ml-10 flex items-center gap-2 px-2 py-1 text-zinc-700"><ChevronRight size={12} className="text-zinc-300" />阿克苏市市场监督管理局<span className="ml-auto rounded bg-slate-100 px-1.5 text-[10px] text-zinc-500">分局</span></div>
            <div className="ml-14 flex items-center gap-2 rounded-md bg-[#C8102E]/[.08] px-2 py-1.5 font-semibold text-[#C8102E]">食品安全监管科<span className="ml-auto text-[10px] font-normal text-zinc-400">5 人</span></div>
            <div className="ml-14 px-2 py-1 text-zinc-600">药品化妆品监管科<span className="float-right text-[10px] text-zinc-400">3 人</span></div>
            <div className="ml-14 px-2 py-1 text-zinc-600">特种设备安全监察科<span className="float-right text-[10px] text-zinc-400">4 人</span></div>
            <div className="ml-14 px-2 py-1 text-[11px] text-zinc-400">…其余 16 个科室</div>
            <div className="ml-10 flex items-center gap-2 px-2 py-1 text-zinc-600">英巴扎街道市场监管所<span className="ml-auto rounded bg-slate-100 px-1.5 text-[10px] text-zinc-500">乡镇所</span></div>
            <div className="ml-5 px-2 py-1 text-zinc-600">库车市<span className="float-right text-[10px] text-zinc-400">16 乡镇街道</span></div>
            <div className="ml-5 px-2 py-1 text-[11px] text-zinc-400">…其余 6 县市</div>
          </div>
        </div>
        <div className="flex flex-1 flex-col gap-3.5">
          <div className="flex items-center justify-between">
            <div><h2 className="text-[19px] font-bold text-zinc-900">食品安全监管科</h2><p className="tnum mt-1 text-xs text-zinc-400">ORG-12406 · 上级：阿克苏市市场监督管理局 · 层级 4/4</p></div>
            <div className="flex gap-2 text-[13px]"><span className="rounded-lg border border-zinc-200 px-3 py-1.5 text-zinc-600">编辑</span><span className="rounded-lg border border-zinc-200 px-3 py-1.5 text-zinc-600">管理人员</span><span className="rounded-lg bg-[#C8102E] px-3 py-1.5 font-medium text-white">新增下级</span></div>
          </div>
          <div className="grid grid-cols-3 gap-3">
            {[
              ['基本信息', ['负责人：孙科长（科长）', '电话：0909-2123456', '类型：机关 · 正常']],
              ['人员构成 · 5 人', ['科长 1 · 副科长 1 · 执法员 3', '主职 5 · 兼职 0', '持执法证 4 / 5']],
              ['数据权限', ['区域：县市级（阿克苏市）', '可见企业：10 家', '下辖乡镇所：10 个']],
            ].map(([t, rows]) => (
              <div key={t as string} className="rounded-xl border border-zinc-200/80 p-3.5">
                <p className="mb-2 text-[11px] text-zinc-400">{t as string}</p>
                {(rows as string[]).map((r) => <p key={r} className="text-xs leading-6 text-zinc-700">{r}</p>)}
              </div>
            ))}
          </div>
          <div className="flex-1 overflow-hidden rounded-xl border border-zinc-200/80">
            <div className="flex items-center justify-between border-b border-zinc-200 px-4 py-2.5"><b className="text-sm text-zinc-900">科室人员</b><span className="rounded-lg border border-zinc-200 px-3 py-1 text-xs text-zinc-600">分配人员</span></div>
            <table className="w-full text-[13px]">
              <thead><tr className="bg-[#FBFBFD] text-left text-zinc-400">{['姓名', '职务 / 岗位', '执法证号', '主职', '账号', '状态'].map((h) => <th key={h} className="px-4 py-2 font-medium">{h}</th>)}</tr></thead>
              <tbody className="text-zinc-700">
                {[['孙科长', '科长 · 食品安全监管', 'AKSF-0121', '主职', 'dept_chief_ws_food', '在岗', 'green'], ['吴执法', '执法员 · 食品安全监管', 'AKSF-0150', '主职', 'enforcer_ws_001', '在岗', 'green'], ['周执法', '执法员 · 食品安全监管', 'AKSF-0151', '—', 'zhou_zf', '借调', 'gray']].map((r) => (
                  <tr key={r[0] as string} className="border-t border-zinc-100">
                    <td className="px-4 py-2.5 font-semibold text-zinc-900">{r[0]}</td><td className="px-4 py-2.5">{r[1]}</td><td className="tnum px-4 py-2.5">{r[2]}</td>
                    <td className="px-4 py-2.5">{r[3] === '主职' ? <span className="rounded border border-blue-100 bg-blue-50 px-1.5 py-0.5 text-[11px] text-blue-600">主职</span> : '—'}</td>
                    <td className="px-4 py-2.5 text-zinc-400">{r[4]}</td>
                    <td className="px-4 py-2.5"><span className={`rounded px-1.5 py-0.5 text-[11px] ${r[6] === 'green' ? 'border border-emerald-100 bg-emerald-50 text-emerald-600' : 'bg-slate-100 text-zinc-500'}`}>{r[5]}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </AdminShell>
  )
}

/* ================= 3 预警督办 ================= */
export function AdminAlerts() {
  const rows = [
    { t: '食品经营许可证已过期', id: '#14 · 09-10 00:02', type: '许可证过期', tone: 'amber', lv: '高', src: '实时', srcT: 'blue', area: '阿克苏市 · 光明乳品', who: '吴执法', esc: '19:24:11', escN: '超时 → 科长', st: '待处理', stT: 'amber' },
    { t: '整改超期未反馈（8 天）', id: '#9 · 09-09 02:00', type: '整改超期', tone: 'amber', lv: '高', src: '定时', srcT: 'gray', area: '温宿县 · 某餐饮', who: '赵局长（升级改派自 吴执法）', esc: '已督办 2 次', escN: '', st: '已督办', stT: 'gold' },
    { t: '合规报告超 45 天未提交', id: '#11 · 09-09 02:00', type: '报告缺失', tone: 'blue', lv: '中', src: '定时', srcT: 'gray', area: '库车市 · 某商贸', who: '郑执法', esc: '—', escN: '', st: '处理中', stT: 'blue' },
    { t: '现场检查不合格：后厨卫生', id: '#21 · 09-10 09:15', type: '信用异常', tone: 'rose', lv: '高', src: '实时', srcT: 'blue', area: '阿克苏市 · 光明乳品', who: '吴执法', esc: '—', escN: '', st: '已处置', stT: 'green' },
  ]
  return (
    <AdminShell title="预警督办" active="预警督办">
      <div className="flex items-center justify-between">
        <h2 className="text-[22px] font-bold tracking-tight text-zinc-900">预警督办</h2>
        <div className="flex gap-2 text-[13px]"><span className="rounded-lg border border-zinc-200 px-3.5 py-1.5 text-zinc-600">导出</span><span className="rounded-lg bg-[#C8102E] px-3.5 py-1.5 font-medium text-white">手动全量扫描</span></div>
      </div>
      <div className="mt-4 grid grid-cols-6 gap-3">
        {[['22', '全部', ''], ['6', '待处理', 'border-t-2 border-t-amber-400'], ['4', '处理中', 'border-t-2 border-t-blue-400'], ['3', '已督办', ''], ['9', '已处置', 'border-t-2 border-t-emerald-400'], ['9', '高风险', 'border-t-2 border-t-rose-500 text-rose-500']].map(([n, l, ex]) => (
          <div key={l} className={`rounded-xl border border-zinc-200/80 py-2.5 text-center ${ex}`}><b className="tnum text-[22px] font-bold">{n}</b><p className="text-[11px] text-zinc-400">{l}</p></div>
        ))}
      </div>
      <div className="mt-3.5 overflow-hidden rounded-xl border border-zinc-200/80">
        <table className="w-full text-[13px]">
          <thead><tr className="bg-[#FBFBFD] text-left text-zinc-400">{['预警内容', '类型', '等级', '来源', '属地 / 企业', '派单 / 处理', '督办', '状态', '操作'].map((h) => <th key={h} className="px-3.5 py-2.5 font-medium">{h}</th>)}</tr></thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id} className="border-t border-zinc-100 text-zinc-700">
                <td className="px-3.5 py-3"><b className="text-zinc-900">{r.t}</b><br /><span className="tnum text-[11px] text-zinc-400">{r.id}</span></td>
                <td className="px-3.5 py-3"><span className={`rounded border px-1.5 py-0.5 text-[11px] ${r.tone === 'amber' ? 'border-amber-100 bg-amber-50 text-amber-600' : r.tone === 'blue' ? 'border-blue-100 bg-blue-50 text-blue-600' : 'border-rose-100 bg-rose-50 text-rose-600'}`}>{r.type}</span></td>
                <td className="px-3.5 py-3"><LevelBadge level={r.lv as '高' | '中'} /></td>
                <td className="px-3.5 py-3"><span className={`rounded px-1.5 py-0.5 text-[11px] ${r.srcT === 'blue' ? 'bg-blue-50 text-blue-600' : 'bg-slate-100 text-zinc-500'}`}>{r.src}</span></td>
                <td className="px-3.5 py-3">{r.area}</td>
                <td className="px-3.5 py-3">{r.who}</td>
                <td className="px-3.5 py-3">{r.esc !== '—' ? <b className={r.stT === 'gold' ? 'text-amber-700' : 'text-amber-500'}>{r.esc}</b> : '—'}{r.escN ? <><br /><span className="text-[11px] text-zinc-400">{r.escN}</span></> : null}</td>
                <td className="px-3.5 py-3"><span className={`rounded border px-1.5 py-0.5 text-[11px] ${r.stT === 'amber' ? 'border-amber-100 bg-amber-50 text-amber-600' : r.stT === 'gold' ? 'border-amber-200/70 bg-amber-50/60 text-amber-700' : r.stT === 'blue' ? 'border-blue-100 bg-blue-50 text-blue-600' : 'border-emerald-100 bg-emerald-50 text-emerald-600'}`}>{r.st}</span></td>
                <td className="px-3.5 py-3"><span className="rounded-lg bg-[#C8102E] px-2.5 py-1 text-xs text-white">详情</span></td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="flex items-center justify-end gap-1.5 border-t border-zinc-100 px-4 py-3 text-xs text-zinc-400">共 22 条<span className="rounded border border-zinc-200 px-2 py-0.5">‹</span><span className="rounded bg-[#C8102E] px-2 py-0.5 text-white">1</span><span className="rounded border border-zinc-200 px-2 py-0.5">2</span><span className="rounded border border-zinc-200 px-2 py-0.5">›</span></div>
      </div>
    </AdminShell>
  )
}

/* ================= 4 注册审核 ================= */
export function AdminReview() {
  return (
    <AdminShell title="注册审核" active="用户管理">
      <div className="flex h-full gap-4">
        <div className="w-[470px] flex-shrink-0 overflow-hidden rounded-xl border border-zinc-200/80">
          <div className="flex gap-2 border-b border-zinc-200 p-3">
            <span className="rounded-lg border border-[#C8102E]/20 bg-[#C8102E]/[.07] px-3.5 py-1 text-[13px] font-semibold text-[#C8102E]">待审核 8</span>
            <span className="rounded-lg bg-slate-100 px-3.5 py-1 text-[13px] text-zinc-500">已通过 3</span>
            <span className="rounded-lg bg-slate-100 px-3.5 py-1 text-[13px] text-zinc-500">已拒绝 2</span>
          </div>
          {[
            { n: 'E2E 测试企业 7312', s: '91652901E2E…7312 · 阿克苏市 · 09-10 09:32', st: '待审核', on: true },
            { n: '康宁大药房', s: '91652901MA7K…88F2 · 阿克苏市 · 09-09 16:20', st: '待审核', on: false },
            { n: '柯坪县红沙河建材厂', s: '91652926MA7H…1109 · 柯坪县 · 09-09 11:04', st: '待审核', on: false },
            { n: 'E2E 拒绝企业 6218', s: '库车市 · 09-10 09:40', st: '已拒绝', on: false },
          ].map((r) => (
            <div key={r.n} className={`flex items-center gap-3 border-b border-zinc-100 p-3.5 ${r.on ? 'bg-[#C8102E]/[.05]' : ''} ${r.st === '已拒绝' ? 'opacity-55' : ''}`}>
              <div className="grid h-9 w-9 flex-shrink-0 place-items-center rounded-xl bg-slate-100 text-slate-400"><Building2 size={18} strokeWidth={1.7} /></div>
              <div className="flex-1"><b className="text-[13.5px] text-zinc-900">{r.n}</b><p className="tnum mt-1 text-[11px] text-zinc-400">{r.s}</p></div>
              <span className={`rounded border px-1.5 py-0.5 text-[11px] ${r.st === '待审核' ? 'border-amber-100 bg-amber-50 text-amber-600' : 'border-rose-100 bg-rose-50 text-rose-500'}`}>{r.st}</span>
            </div>
          ))}
        </div>
        <div className="flex flex-1 flex-col gap-3.5">
          <div className="flex items-center justify-between">
            <div><h2 className="text-[19px] font-bold text-zinc-900">E2E 测试企业 7312</h2><p className="mt-1 text-xs text-zinc-400">餐饮服务 · 阿克苏市英巴扎街道 · 提交于 09-10 09:32</p></div>
            <span className="rounded border border-blue-100 bg-blue-50 px-2 py-0.5 text-[11px] text-blue-600">步骤 5/5 已提交</span>
          </div>
          <div className="rounded-xl border border-zinc-200/80 p-3.5">
            <p className="mb-2.5 text-[11px] text-zinc-400">证照材料</p>
            <div className="flex gap-3">
              {[['营业执照', 'OCR 一致'], ['食品经营许可', '2028 到期'], ['健康证', '2 张'], ['门头 / 店内照', '已上传']].map(([t, s]) => (
                <div key={t} className="w-[140px]">
                  <div className="grid h-[84px] place-items-center rounded-lg bg-slate-50 text-slate-300"><ScrollText size={24} strokeWidth={1.5} /></div>
                  <p className="mt-1.5 text-[11px] text-emerald-600">✓ {t} · {s}</p>
                </div>
              ))}
            </div>
          </div>
          <div className="flex flex-1 flex-col rounded-xl border border-zinc-200/80 p-4">
            <p className="text-[11px] text-zinc-400">审核意见</p>
            <div className="mt-2 rounded-lg border border-zinc-200 px-3 py-2.5 text-[13px] text-zinc-700">材料齐全，同意注册。</div>
            <div className="mt-auto flex justify-end gap-2.5 pt-4">
              <span className="rounded-lg bg-rose-500 px-4 py-2 text-[13px] font-medium text-white">拒绝（触发预警）</span>
              <span className="rounded-lg bg-emerald-500 px-4 py-2 text-[13px] font-medium text-white">通过并创建企业账号</span>
            </div>
            <p className="mt-2.5 text-[11px] text-zinc-400">通过后自动创建企业档案与账号（初始密码 123456）· 分配属地：英巴扎街道市场监管所</p>
          </div>
        </div>
      </div>
    </AdminShell>
  )
}
