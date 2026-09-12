import type { ReactNode } from 'react'
import { Wifi, BatteryFull, Signal, ScanLine } from 'lucide-react'

/* ===== 手机框架（iPhone） ===== */
export function PhoneFrame({ children, tabbar, dark = false }: { children: ReactNode; tabbar?: ReactNode; dark?: boolean }) {
  return (
    <div className={`relative flex h-[812px] w-[375px] flex-shrink-0 flex-col overflow-hidden rounded-[3.2rem] border-[10px] ${dark ? 'border-zinc-800 bg-[#0A0A0B]' : 'border-zinc-900 bg-[#F6F7F9]'} shadow-2xl`}>
      <div className={`absolute left-1/2 top-2 z-50 h-[30px] w-[110px] -translate-x-1/2 rounded-full ${dark ? 'bg-zinc-800' : 'bg-zinc-900'}`} />
      <div className={`flex h-[48px] items-end justify-between px-7 pb-1 text-[13px] font-semibold ${dark ? 'text-white' : 'text-zinc-900'}`}>
        <span className="tnum">9:41</span>
        <span className="flex items-center gap-1"><Signal size={13} /><Wifi size={13} /><BatteryFull size={16} /></span>
      </div>
      <div className="relative flex min-h-0 flex-1 flex-col overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">{children}</div>
      {tabbar}
      <div className={`absolute bottom-1.5 left-1/2 h-[5px] w-[120px] -translate-x-1/2 rounded-full ${dark ? 'bg-white/80' : 'bg-zinc-900'}`} />
    </div>
  )
}

/* ===== 微信原生风格 TabBar（支持中央凸起扫码主键） ===== */
export type TabItem = { icon: ReactNode; label: string; active?: boolean; badge?: number; scan?: boolean }
export function TabBar({ items, dark = false, accent = 'blue' }: { items: TabItem[]; dark?: boolean; accent?: 'blue' | 'red' }) {
  const activeText = accent === 'red' ? 'text-red-600' : 'text-blue-600'
  return (
    <div className={`relative z-40 flex h-[82px] flex-shrink-0 items-start border-t pt-2.5 pb-4 ${dark ? 'border-white/10 bg-[#121214]/95' : 'border-zinc-200/80 bg-white/95'} backdrop-blur`}>
      {items.map((t, i) => t.scan ? (
        <div key={`scan-${i}`} className="flex flex-1 flex-col items-center gap-1">
          <div className={`-mt-8 grid h-[54px] w-[54px] place-items-center rounded-[18px] border-4 bg-gradient-to-b from-[#D5263D] to-[#B00E24] shadow-xl shadow-red-700/30 ${dark ? 'border-[#121214]' : 'border-white'}`}>
            <ScanLine size={26} className="text-white" strokeWidth={2} />
          </div>
          <span className={`text-[10px] ${dark ? 'text-zinc-500' : 'text-zinc-400'}`}>扫码查企</span>
        </div>
      ) : (
        <div key={t.label} className={`relative flex flex-1 flex-col items-center gap-0.5 text-[10px] ${t.active ? `font-semibold ${activeText}` : dark ? 'text-zinc-500' : 'text-zinc-400'}`}>
          <span className={t.active ? activeText : dark ? 'text-zinc-400' : 'text-zinc-400'}>{t.icon}</span>
          {t.label}
          {t.badge ? <span className="absolute -right-1.5 -top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-rose-500 px-1 text-[9px] font-bold text-white">{t.badge}</span> : null}
        </div>
      ))}
    </div>
  )
}

/* ===== macOS 浏览器窗口框架（后台用） ===== */
export function BrowserFrame({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="w-[1400px] overflow-hidden rounded-xl border border-zinc-300/60 bg-white shadow-2xl">
      <div className="flex h-10 items-center gap-2 border-b border-zinc-200 bg-[#F6F6F8] px-4">
        <span className="h-3 w-3 rounded-full bg-[#FF5F57]" />
        <span className="h-3 w-3 rounded-full bg-[#FEBC2E]" />
        <span className="h-3 w-3 rounded-full bg-[#28C840]" />
        <span className="mx-auto rounded-md border border-zinc-200 bg-white px-16 py-0.5 text-xs text-zinc-500">{title}</span>
      </div>
      {children}
    </div>
  )
}
