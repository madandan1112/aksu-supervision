import { BadgeCheck, Phone, MapPin } from 'lucide-react'
import { PhoneFrame } from '../components/frames'
import { Emblem } from '../components/Emblem'
import { Card, Tag } from '../components/wx'

/* ================= 1 企业信用公示 ================= */
export function PubCredit() {
  return (
    <PhoneFrame>
      <div className="flex-shrink-0 bg-white px-5 pb-3 pt-2">
        <div className="flex items-center gap-2">
          <Emblem size={24} />
          <span className="text-xs text-zinc-400">阿克苏地区市场监管 · 企业信息公示</span>
        </div>
        <h1 className="mt-3 text-[22px] font-bold leading-tight tracking-tight text-zinc-900">阿克苏市光明乳品有限公司</h1>
        <p className="tnum mt-1.5 text-xs text-zinc-400">91652901MA77XXXXX4 · 餐饮服务 · 阿克苏市英巴扎街道</p>
      </div>
      <div className="grid grid-cols-3 gap-2.5 px-4">
        {[['信用正常', 'border-emerald-100 bg-emerald-50/70 text-emerald-600'], ['执照有效', 'border-emerald-100 bg-emerald-50/70 text-emerald-600'], ['许可有效', 'border-emerald-100 bg-emerald-50/70 text-emerald-600']].map(([t, c]) => (
          <div key={t} className={`grid place-items-center gap-1.5 rounded-2xl border py-3.5 ${c}`}><BadgeCheck size={22} strokeWidth={1.8} /><span className="text-xs font-semibold">{t}</span></div>
        ))}
      </div>
      <div className="p-4">
        <Card className="p-4">
          <b className="text-sm text-zinc-900">亮证信息</b>
          <div className="mt-2.5 space-y-0.5">
            {[['营业执照', '91652901MA77XXXXX4'], ['食品经营许可证', 'JY16529010001234 · 2027-03'], ['从业人员健康证', '12 / 12 持证']].map(([t, v]) => (
              <div key={t} className="flex items-center gap-3 border-b border-slate-100 py-2.5 last:border-0">
                <span className="flex-1 text-[14px] text-zinc-700">{t}</span>
                <span className="tnum text-[13px] text-zinc-400">{v}</span>
                <BadgeCheck size={17} className="text-emerald-500" strokeWidth={2.2} />
              </div>
            ))}
          </div>
        </Card>
        <div className="mb-2.5 mt-4 flex items-baseline justify-between px-1"><h3 className="flex items-center gap-2 text-[17px] font-bold tracking-tight text-zinc-900"><span className="h-4 w-1 rounded-full bg-gradient-to-b from-emerald-400 to-emerald-600" />监督检查公示</h3><span className="text-xs text-zinc-400">近12个月 · 8 次</span></div>
        <Card className="p-4">
          <div className="flex items-center justify-between"><b className="text-[15px] text-zinc-900">2026-09-08 日常检查</b><span className="text-xs font-semibold text-amber-600">整改中</span></div>
          <p className="mt-1.5 text-[13px] text-slate-500">发现 2 项问题，已责令限期整改</p>
          <p className="mt-1 text-[11px] text-zinc-400">阿克苏市市监局 · 食品安全监管科</p>
        </Card>
        <Card className="mt-2.5 p-4">
          <div className="flex items-center justify-between"><b className="text-[15px] text-zinc-900">2026-08-02 日常检查</b><span className="text-xs font-semibold text-emerald-600">合格</span></div>
          <p className="mt-1.5 text-[13px] text-slate-500">未发现问题</p>
          <p className="mt-1 text-[11px] text-zinc-400">阿克苏市市监局 · 食品安全监管科</p>
        </Card>
        <p className="mb-4 mt-4 text-center text-[11px] leading-relaxed text-zinc-400">公示信息由阿克苏地区市场监督管理局提供<br />数据更新于 2026-09-10</p>
      </div>
    </PhoneFrame>
  )
}

/* ================= 2 属地监管与举报 ================= */
export function PubReport() {
  return (
    <PhoneFrame>
      <div className="sticky top-0 z-20 flex flex-shrink-0 items-center justify-between bg-white px-4 py-2.5">
        <span className="text-[15px] text-emerald-600">‹ 返回</span><span className="text-[16px] font-semibold text-zinc-900">属地监管</span><span className="w-10" />
      </div>
      <div className="px-5 pb-2 pt-1"><h1 className="text-[24px] font-bold tracking-tight text-zinc-900">监管与举报</h1></div>
      <div className="p-4">
        <Card className="p-4">
          <div className="flex items-center gap-3">
            <div className="grid h-11 w-11 place-items-center rounded-xl border border-[#C8102E]/15 bg-[#C8102E]/[.07] text-[#C8102E]"><MapPin size={22} strokeWidth={1.8} /></div>
            <div className="flex-1"><b className="text-[15px] text-zinc-900">阿克苏市市场监督管理局</b><p className="mt-1 text-xs text-zinc-400">英巴扎街道市场监管所 · 属地监管</p></div>
          </div>
          <div className="mt-3 space-y-1 rounded-xl bg-slate-50 p-3 text-[13px] text-zinc-500">
            <p className="flex items-center gap-2"><MapPin size={14} className="text-zinc-400" />英巴扎街道…幸福路</p>
            <p className="flex items-center gap-2"><Phone size={14} className="text-zinc-400" />0909-2123456（工作日）</p>
          </div>
        </Card>
        <div className="mb-2.5 mt-4 flex items-baseline justify-between px-1"><h3 className="flex items-center gap-2 text-[17px] font-bold tracking-tight text-zinc-900"><span className="h-4 w-1 rounded-full bg-gradient-to-b from-emerald-400 to-emerald-600" />网格执法人员</h3></div>
        <Card>
          {[['吴执法', '网格员 · 执法证 AKSF-0150'], ['孙科长', '科室负责人 · 执法证 AKSF-0121']].map(([n, d], i) => (
            <div key={n} className={`flex items-center gap-3 px-4 py-3 ${i > 0 ? 'border-t border-slate-100' : ''}`}>
              <div className="grid h-10 w-10 place-items-center rounded-full bg-slate-100 text-[15px] font-semibold text-zinc-500">{n[0]}</div>
              <div className="flex-1"><b className="text-[15px] text-zinc-900">{n}</b><p className="tnum mt-0.5 text-xs text-zinc-400">{d}</p></div>
              <div className="grid h-9 w-9 place-items-center rounded-full bg-emerald-50 text-emerald-600"><Phone size={17} strokeWidth={1.9} /></div>
            </div>
          ))}
        </Card>
        <div className="mb-2.5 mt-4 px-1"><h3 className="flex items-center gap-2 text-[17px] font-bold tracking-tight text-zinc-900"><span className="h-4 w-1 rounded-full bg-gradient-to-b from-rose-400 to-rose-500" />发现问题 · 随手举报</h3></div>
        <Card className="p-4">
          <p className="text-[13px] leading-relaxed text-slate-500">食品安全、无证经营、假冒伪劣、价格欺诈……欢迎监督举报，监管部门将依法核查处理。</p>
        </Card>
        <div className="mt-3.5 rounded-2xl bg-gradient-to-b from-rose-500 to-rose-600 py-3.5 text-center text-[16px] font-semibold text-white shadow-lg shadow-rose-600/30">平台一键举报（实名可查进度）</div>
        <div className="mt-2.5 rounded-2xl border border-slate-200 bg-white py-3.5 text-center text-[15px] font-medium text-zinc-700">拨打 12315 热线</div>
        <p className="mb-4 mt-3 text-center text-[11px] leading-relaxed text-zinc-400">举报将转入属地监管所处置流程<br />依据《市场监督管理投诉举报处理暂行办法》办理</p>
        <div className="flex justify-center gap-1.5 pb-4"><Tag tone="green">依法公示</Tag><Tag tone="green">依法监督</Tag><Tag tone="gray">社会共治</Tag></div>
      </div>
    </PhoneFrame>
  )
}
