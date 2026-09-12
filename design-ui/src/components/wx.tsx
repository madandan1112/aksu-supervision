import type { ReactNode } from 'react'
import { ChevronRight } from 'lucide-react'

/* ===== 微信/TDesign 风格基础组件 ===== */

export function Card({ children, className = '', dark = false }: { children: ReactNode; className?: string; dark?: boolean }) {
  return (
    <div className={`${dark ? 'card-float-dark border border-white/[.07] bg-[#1C1C1E]' : 'card-float border border-slate-200/70 bg-white'} rounded-2xl ${className}`}>{children}</div>
  )
}

export function Cell({ icon, title, value, arrow = true, dark = false, danger = false, children }: {
  icon?: ReactNode; title: ReactNode; value?: ReactNode; arrow?: boolean; dark?: boolean; danger?: boolean; children?: ReactNode
}) {
  return (
    <div className={`flex min-h-[52px] items-center gap-3 px-4 ${dark ? 'text-zinc-100' : ''}`}>
      {icon}
      <span className={`flex-1 text-[15px] ${danger ? 'text-rose-500' : dark ? 'text-zinc-100' : 'text-zinc-900'}`}>{title}</span>
      {value}
      {arrow ? <ChevronRight size={16} className={dark ? 'text-zinc-600' : 'text-zinc-300'} strokeWidth={2.2} /> : null}
      {children}
    </div>
  )
}

export function CellDivider({ dark = false }: { dark?: boolean }) {
  return <div className={`ml-14 border-t ${dark ? 'border-white/[.06]' : 'border-slate-100'}`} />
}

export function SectionTitle({ children, more, dark = false, accent = 'blue' }: { children: ReactNode; more?: string; dark?: boolean; accent?: 'blue' | 'red' }) {
  return (
    <div className="mb-2.5 mt-4 flex items-baseline justify-between px-5">
      <h3 className={`flex items-center gap-2 text-[17px] font-bold tracking-tight ${dark ? 'text-white' : 'text-zinc-900'}`}>
        <span className={`h-[16px] w-1 rounded-full bg-gradient-to-b ${accent === 'red' ? 'from-[#E03047] to-[#B00E24]' : 'from-blue-400 to-blue-600'}`} />
        {children}
      </h3>
      {more ? <span className={`text-xs ${dark ? 'text-zinc-500' : 'text-zinc-400'}`}>{more}</span> : null}
    </div>
  )
}

export function Tag({ tone = 'blue', children }: { tone?: 'blue' | 'green' | 'amber' | 'rose' | 'gray' | 'sky' | 'gold'; children: ReactNode }) {
  const tones: Record<string, string> = {
    blue: 'bg-blue-50 text-blue-600 border-blue-100',
    green: 'bg-emerald-50 text-emerald-600 border-emerald-100',
    amber: 'bg-amber-50 text-amber-600 border-amber-100',
    rose: 'bg-rose-50 text-rose-600 border-rose-100',
    gray: 'bg-slate-100 text-slate-500 border-slate-200',
    sky: 'bg-sky-50 text-sky-600 border-sky-100',
    gold: 'bg-amber-50/60 text-amber-700 border-amber-200/60',
  }
  return <span className={`inline-flex items-center gap-1 rounded-md border px-2 py-0.5 text-[11px] font-semibold ${tones[tone]}`}>{children}</span>
}

export function LevelBadge({ level }: { level: '高' | '中' | '低' }) {
  const cls = level === '高' ? 'bg-rose-500' : level === '中' ? 'bg-amber-500' : 'bg-slate-400'
  return <span className={`rounded-md px-2 py-0.5 text-[11px] font-bold text-white ${cls}`}>{level}</span>
}

export function Segmented({ options, active = 0, dark = false }: { options: string[]; active?: number; dark?: boolean }) {
  return (
    <div className={`mx-4 mb-3 flex rounded-[10px] p-0.5 ${dark ? 'bg-white/10' : 'bg-slate-200/60'}`}>
      {options.map((o, i) => (
        <span key={o} className={`flex-1 rounded-lg py-1.5 text-center text-[13px] ${i === active ? `${dark ? 'bg-zinc-800 font-semibold text-white shadow' : 'bg-white font-semibold text-zinc-900 shadow-sm'}` : dark ? 'text-zinc-300' : 'text-zinc-600'}`}>{o}</span>
      ))}
    </div>
  )
}

export function StripedBar({ pct, tone = 'amber', danger = false }: { pct: number; tone?: string; danger?: boolean }) {
  return (
    <div className="h-[5px] overflow-hidden rounded-full bg-slate-200/70">
      <div className={`stripes-fill h-full rounded-full ${danger ? 'bg-rose-500' : ''}`} style={{ width: `${pct}%`, ['--sc' as string]: danger ? '#F43F5E' : tone === 'amber' ? '#F59E0B' : '#3B82F6', ['--sc2' as string]: danger ? '#FB7185' : tone === 'amber' ? '#FCD34D' : '#93C5FD' }} />
    </div>
  )
}

export function CountRing({ pct, center, sub, size = 56 }: { pct: number; center: string; sub?: string; size?: number }) {
  return (
    <div className="relative flex flex-shrink-0 items-center justify-center" style={{ width: size, height: size }}>
      <div className={`ring-dash absolute inset-0 rounded-full border-2 border-dashed ${'border-white/50'}`} />
      <div className="flex items-center justify-center rounded-full" style={{ width: size - 12, height: size - 12, background: `conic-gradient(#fff ${pct}%, rgba(255,255,255,.25) 0)` }}>
        <div className="flex flex-col items-center justify-center rounded-full bg-inherit" style={{ width: size - 26, height: size - 26, background: 'var(--ring-bg,#0B4FCB)' }}>
          <span className="tnum text-[15px] font-bold leading-none text-white">{center}</span>
          {sub ? <span className="mt-0.5 text-[8px] text-white/70">{sub}</span> : null}
        </div>
      </div>
    </div>
  )
}

export function IosSwitch({ on = true }: { on?: boolean }) {
  return (
    <div className="relative h-[27px] w-[46px] flex-shrink-0 rounded-full bg-gradient-to-b from-[#4BC96A] to-[#1E9E4A] shadow-inner">
      <div className="absolute right-[2.5px] top-[2.5px] h-[22px] w-[22px] rounded-full bg-white shadow" />
    </div>
  )
}

export function Stars({ n = 5 }: { n?: number }) {
  return <span className="text-[13px] tracking-[.15em] text-amber-500">{'★'.repeat(n)}</span>
}

export function IconTile({ icon, tone = 'blue', dark = false, size = 'md' }: { icon: ReactNode; tone?: 'blue' | 'amber' | 'green' | 'gray' | 'rose' | 'sky'; dark?: boolean; size?: 'md' | 'sm' }) {
  const light: Record<string, string> = {
    blue: 'bg-blue-50 text-blue-600',
    amber: 'bg-amber-50 text-amber-600',
    green: 'bg-emerald-50 text-emerald-600',
    gray: 'bg-slate-100 text-slate-500',
    rose: 'bg-rose-50 text-rose-500',
    sky: 'bg-sky-50 text-sky-600',
  }
  const darkT: Record<string, string> = {
    blue: 'bg-blue-500/15 text-blue-400',
    amber: 'bg-amber-500/15 text-amber-400',
    green: 'bg-emerald-500/15 text-emerald-400',
    gray: 'bg-white/[.07] text-zinc-300',
    rose: 'bg-rose-500/15 text-rose-400',
    sky: 'bg-sky-500/15 text-sky-400',
  }
  const s = size === 'md' ? 'h-11 w-11 rounded-[14px]' : 'h-[34px] w-[34px] rounded-xl'
  return <div className={`grid flex-shrink-0 place-items-center ${s} ${dark ? darkT[tone] : light[tone]}`}>{icon}</div>
}

export function PrimaryBtn({ children, tone = 'blue', dark = false }: { children: ReactNode; tone?: 'blue' | 'green' | 'rose' | 'gray'; dark?: boolean }) {
  const map: Record<string, string> = {
    blue: dark ? 'from-rose-400 to-rose-600 shadow-rose-500/30' : 'from-blue-500 to-blue-600 shadow-blue-600/25',
    green: 'from-emerald-400 to-emerald-600 shadow-emerald-600/25',
    rose: 'from-rose-500 to-rose-600 shadow-rose-600/30',
    gray: dark ? 'from-zinc-700 to-zinc-800 shadow-none' : 'from-white to-slate-100 text-zinc-800 shadow-black/5',
  }
  return (
    <div className={`mx-4 rounded-2xl bg-gradient-to-b py-3.5 text-center text-[16px] font-semibold text-white shadow-lg ${map[tone]}`}>{children}</div>
  )
}
