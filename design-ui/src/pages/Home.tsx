import { useState } from 'react'
import { Store, ShieldCheck, Monitor, QrCode } from 'lucide-react'
import { EntLogin, EntHome, EntRegister, EntAppeals, EntRectify, EntProfile } from './enterprise'
import { InspLogin, InspHome, InspScan, InspInspect, InspAlerts, InspProfile } from './inspector'
import { AdminDashboard, AdminOrg, AdminAlerts, AdminReview } from './admin'
import { PubCredit, PubReport } from './public'

type Screen = { id: string; label: string; el: React.ReactNode }
type Set = { id: string; name: string; sub: string; icon: React.ReactNode; screens: Screen[] }

const SETS: Set[] = [
  {
    id: 'enterprise', name: '小程序 · 企业商户端', sub: 'iOS 浅色 · 品牌蓝 · 微信原生组件语言', icon: <Store size={16} />,
    screens: [
      { id: 'login', label: '登录', el: <EntLogin /> },
      { id: 'home', label: '首页', el: <EntHome /> },
      { id: 'register', label: '注册向导', el: <EntRegister /> },
      { id: 'appeals', label: '诉求', el: <EntAppeals /> },
      { id: 'rectify', label: '整改反馈', el: <EntRectify /> },
      { id: 'profile', label: '我的', el: <EntProfile /> },
    ],
  },
  {
    id: 'inspector', name: '小程序 · 执法人员端', sub: '高对比浅色 · 市监红 · 中央扫码主键', icon: <ShieldCheck size={16} />,
    screens: [
      { id: 'login', label: '登录', el: <InspLogin /> },
      { id: 'home', label: '工作台', el: <InspHome /> },
      { id: 'scan', label: '扫码查企', el: <InspScan /> },
      { id: 'inspect', label: '现场检查', el: <InspInspect /> },
      { id: 'alerts', label: '预警', el: <InspAlerts /> },
      { id: 'profile', label: '我的', el: <InspProfile /> },
    ],
  },
  {
    id: 'admin', name: 'Web 管理后台', sub: 'macOS 窗口 · 市监红 · shadcn 组件体系', icon: <Monitor size={16} />,
    screens: [
      { id: 'dashboard', label: '数据看板', el: <AdminDashboard /> },
      { id: 'org', label: '组织架构', el: <AdminOrg /> },
      { id: 'alerts', label: '预警督办', el: <AdminAlerts /> },
      { id: 'review', label: '注册审核', el: <AdminReview /> },
    ],
  },
  {
    id: 'public', name: '公众扫码公示 H5', sub: '免登录 · 信用绿 · 社会共治', icon: <QrCode size={16} />,
    screens: [
      { id: 'credit', label: '企业信用公示', el: <PubCredit /> },
      { id: 'report', label: '监管与举报', el: <PubReport /> },
    ],
  },
]

export default function Home() {
  const [cur, setCur] = useState<{ set: number; scr: number }>({ set: 0, scr: 1 })
  const set = SETS[cur.set]
  const scr = set.screens[cur.scr]
  const isAdmin = set.id === 'admin'

  return (
    <div className="flex h-screen overflow-hidden bg-[#0B0B0D] text-zinc-100">
      {/* 左侧导航 */}
      <aside className="flex w-[248px] flex-shrink-0 flex-col border-r border-white/[.06] bg-[#101013] p-3.5">
        <div className="px-1.5 py-2">
          <p className="text-[11px] tracking-wide text-zinc-500">阿克苏地区市场监督管理局</p>
          <h1 className="mt-1 text-[17px] font-bold tracking-tight text-white">UI 设计方案 <span className="text-rose-400">V2</span></h1>
          <p className="mt-1.5 text-[11px] leading-relaxed text-zinc-500">React + Tailwind v4 + shadcn 规范<br />lucide 图标 · 市监官徽 · 四端 18 屏</p>
        </div>
        <div className="mt-3 flex-1 space-y-3 overflow-y-auto [scrollbar-width:thin]">
          {SETS.map((s, si) => (
            <div key={s.id}>
              <button
                onClick={() => setCur({ set: si, scr: 0 })}
                className={`flex w-full items-center gap-2 rounded-lg px-2.5 py-2 text-left text-[13px] font-semibold ${cur.set === si ? 'bg-white/[.08] text-white' : 'text-zinc-400'}`}
              >
                <span className={cur.set === si ? 'text-rose-400' : 'text-zinc-500'}>{s.icon}</span>{s.name}
              </button>
              {cur.set === si ? (
                <div className="mt-1 space-y-px">
                  {s.screens.map((sc, ci) => (
                    <button
                      key={sc.id}
                      onClick={() => setCur({ set: si, scr: ci })}
                      className={`w-full rounded-md px-2.5 py-1.5 text-left text-[12.5px] ${cur.scr === ci ? 'bg-rose-500/15 font-semibold text-rose-300' : 'text-zinc-500'}`}
                    >
                      {String(ci + 1).padStart(2, '0')} · {sc.label}
                    </button>
                  ))}
                </div>
              ) : null}
            </div>
          ))}
        </div>
        <div className="mt-2 border-t border-white/[.06] px-1.5 pt-2.5 text-[10px] leading-relaxed text-zinc-600">
          设计令牌：蓝 2563EB · 红 C8102E · 金 E5C893<br />绿 34C759 · 橙 FF9500 · 灰 zinc 系
        </div>
      </aside>

      {/* 画布 */}
      <main className="flex min-w-0 flex-1 flex-col overflow-hidden">
        <div className="flex flex-shrink-0 items-baseline justify-between border-b border-white/[.06] px-7 py-3.5">
          <div>
            <h2 className="text-[15px] font-semibold text-white">{set.name} · {scr.label}</h2>
            <p className="mt-0.5 text-[11.5px] text-zinc-500">{set.sub}</p>
          </div>
          <p className="tnum text-[11px] text-zinc-600">{cur.set + 1}/{SETS.length} 套 · {cur.scr + 1}/{set.screens.length} 屏</p>
        </div>
        <div className="flex min-h-0 flex-1 items-center justify-center overflow-auto p-6">
          {isAdmin ? <div style={{ zoom: 0.58 }}>{scr.el}</div> : scr.el}
        </div>
      </main>
    </div>
  )
}
