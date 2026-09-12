import { ShieldCheck, ClipboardList, AlertTriangle, User, Download, MapPin, Phone, Check, Camera, Video, Plus } from 'lucide-react'
import { PhoneFrame, TabBar } from '../components/frames'
import { Emblem } from '../components/Emblem'
import { Card, Cell, CellDivider, SectionTitle, LevelBadge, Segmented, IconTile, StripedBar } from '../components/wx'

/* 市监红主按钮（浅色端） */
function DBtn({ children, tone = 'red' }: { children: string; tone?: 'red' | 'gray' }) {
  return (
    <div className={`flex-1 rounded-xl py-3 text-center text-[15px] font-semibold shadow-lg ${tone === 'red' ? 'bg-gradient-to-b from-[#D5263D] to-[#B00E24] text-white shadow-red-700/25' : 'border border-slate-200 bg-white text-zinc-700 shadow-black/[.04]'}`}>{children}</div>
  )
}

/* ================= 1 登录 ================= */
export function InspLogin() {
  return (
    <PhoneFrame>
      <div className="flex flex-1 flex-col justify-center px-7">
        <div className="relative h-[88px] w-[88px]">
          <div className="grid h-full w-full place-items-center rounded-[24px] border border-red-100 bg-gradient-to-b from-white to-red-50 shadow-xl shadow-red-900/10">
            <Emblem size={62} />
          </div>
          <div className="absolute -bottom-1.5 -right-1.5 grid h-7 w-7 place-items-center rounded-[10px] bg-gradient-to-br from-[#E5C893] to-[#8F6A1E] shadow-lg">
            <ShieldCheck size={14} className="text-zinc-900" strokeWidth={2.2} />
          </div>
        </div>
        <h1 className="mt-6 text-[30px] font-extrabold tracking-tight text-zinc-900">执法工作台</h1>
        <p className="mt-2 text-[15px] text-zinc-500">阿克苏地区市场监督管理</p>
        <div className="mt-3 flex gap-1.5">
          {['扫码查企', '现场检查', '预警督办'].map((t) => <span key={t} className="rounded-full border border-red-200/80 bg-red-50 px-3 py-1 text-[11px] font-medium text-red-700">{t}</span>)}
        </div>
        <Card className="mt-9 p-4">
          <div className="flex items-start justify-between">
            <div>
              <div className="flex items-center gap-2"><b className="text-lg text-zinc-900">吴执法</b><span className="rounded-md border border-[#C9A063]/40 bg-[#C9A063]/15 px-2 py-0.5 text-[11px] font-semibold text-[#8F6A1E]">二级执法员</span></div>
              <p className="tnum mt-2 text-[13px] text-zinc-500">执法证 AKSF-0150</p>
              <p className="mt-1 text-xs text-zinc-400">阿克苏市市监局 · 食品安全监管科</p>
            </div>
            <span className="flex items-center gap-1.5 rounded-xl border border-emerald-200 bg-emerald-50 px-2.5 py-1.5 text-[11px] font-semibold text-emerald-600"><span className="dot-breathe h-1.5 w-1.5 rounded-full bg-emerald-500" />在岗</span>
          </div>
        </Card>
        <div className="mt-5">
          <div className="rounded-2xl bg-gradient-to-b from-[#18D062] to-[#06AD56] py-3.5 text-center text-[16px] font-semibold text-white shadow-xl shadow-emerald-600/25">微信一键登录（工号绑定）</div>
          <div className="mt-3 rounded-2xl border border-slate-200 bg-white py-3.5 text-center text-[15px] font-medium text-zinc-700 shadow-sm">账号密码登录</div>
        </div>
        <p className="mt-7 text-center text-[11px] text-zinc-400">登录行为将记录审计日志</p>
      </div>
    </PhoneFrame>
  )
}

/* ================= 2 工作台 ================= */
export function InspHome() {
  return (
    <PhoneFrame tabbar={<TabBar accent="red" items={[
      { icon: <ShieldCheck size={23} strokeWidth={1.8} />, label: '工作台', active: true },
      { icon: <ClipboardList size={23} strokeWidth={1.8} />, label: '检查', badge: 1 },
      { icon: null, label: '扫码查企', scan: true },
      { icon: <AlertTriangle size={23} strokeWidth={1.8} />, label: '预警', badge: 2 },
      { icon: <User size={23} strokeWidth={1.8} />, label: '我的' },
    ]} />}>
      <div className="flex-shrink-0 bg-gradient-to-b from-[#C8102E] to-[#A50D22] px-4 pb-5 pt-1">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-xs text-red-100/90">9月10日 星期四 · 属地 阿克苏市</p>
            <h3 className="mt-1 text-[23px] font-extrabold tracking-tight text-white">工作台</h3>
          </div>
          <div className="relative grid h-9 w-9 place-items-center rounded-xl bg-white/15">
            <span className="dot-breathe text-white"><AlertTriangle size={17} /></span>
            <span className="absolute -right-1 -top-1 grid h-4 w-4 place-items-center rounded-full bg-white text-[9px] font-bold text-red-700 ring-2 ring-[#C8102E]">3</span>
          </div>
        </div>
        <div className="mt-4 grid grid-cols-4 gap-2">
          {[['2', '待执行任务'], ['1', '待验收'], ['2', '属地预警'], ['6', '本月检查']].map(([n, l]) => (
            <div key={l} className="rounded-xl bg-white/[.14] py-2.5 text-center ring-1 ring-inset ring-white/15">
              <b className="tnum block text-xl font-extrabold text-white">{n}</b>
              <span className="text-[10px] text-red-100/90">{l}</span>
            </div>
          ))}
        </div>
      </div>
      <SectionTitle accent="red" more="全部 22 ›">属地预警</SectionTitle>
      <div className="px-4">
        <Card className="border-red-200 p-4">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <div className="flex items-center gap-1.5"><LevelBadge level="高" /><span className="rounded-md border border-red-200 bg-red-50 px-2 py-0.5 text-[11px] font-semibold text-red-600">实时</span><span className="tnum text-[11px] text-zinc-400">#14 · 00:02</span></div>
              <b className="mt-2.5 block text-base text-zinc-900">食品经营许可证已过期</b>
              <p className="mt-1 text-xs text-zinc-500">光明乳品 · 派单 吴执法（属地自动）</p>
              <div className="mt-2.5"><StripedBar pct={80} /></div>
            </div>
            <div className="ml-2.5 flex flex-col items-center">
              <div className="relative grid h-[54px] w-[54px] place-items-center">
                <div className="ring-dash absolute inset-0 rounded-full border-2 border-dashed border-amber-400" />
                <div className="grid h-[42px] w-[42px] place-items-center rounded-full" style={{ background: 'conic-gradient(#F59E0B 80%, rgba(245,158,11,.18) 0)' }}>
                  <div className="grid h-8 w-8 place-items-center rounded-full bg-white"><span className="tnum text-xs font-bold text-amber-600">19h</span></div>
                </div>
              </div>
              <span className="mt-1 text-[9px] text-zinc-400">督办倒计时</span>
            </div>
          </div>
          <div className="mt-3.5 flex gap-2.5"><DBtn>接收处置</DBtn><DBtn tone="gray">查看企业</DBtn></div>
        </Card>
        <Card className="mt-3 flex items-center gap-3 p-3">
          <LevelBadge level="中" />
          <div className="flex-1"><b className="text-sm text-zinc-900">合规报告超45天未提交</b><p className="mt-1 text-[11px] text-zinc-400">库车某商贸 · 定时规则 · 督办 1 次→科长</p></div>
        </Card>
        <SectionTitle accent="red" more="任务列表 ›">今日任务</SectionTitle>
        <Card>
          <Cell icon={<IconTile tone="rose" size="sm" icon={<ClipboardList size={19} strokeWidth={1.8} />} />} title="双随机 · 食品流通专项" value={<span className="rounded-md border border-red-200 bg-red-50 px-2 py-0.5 text-[11px] font-semibold text-red-600">已领取</span>} />
          <CellDivider />
          <Cell icon={<IconTile tone="sky" size="sm" icon={<Check size={19} strokeWidth={2.2} />} />} title="整改验收 · 光明乳品" value={<span className="rounded-md border border-sky-200 bg-sky-50 px-2 py-0.5 text-[11px] font-semibold text-sky-600">待验收</span>} />
        </Card>
        <div className="h-4" />
      </div>
    </PhoneFrame>
  )
}

/* ================= 3 扫码查企 ================= */
export function InspScan() {
  return (
    <PhoneFrame>
      <div className="sticky top-0 z-20 flex flex-shrink-0 items-center justify-between border-b border-slate-200/70 bg-white/95 px-4 py-2.5 pt-3 text-[15px] backdrop-blur">
        <span className="text-red-600">‹ 返回</span><span className="font-semibold text-zinc-900">扫码查企</span><span className="text-[13px] text-red-600">手输信用代码</span>
      </div>
      <div className="relative mx-14 mt-6 h-[220px] rounded-md bg-zinc-900">
        {[['top-0 left-0', 'border-t-[3px] border-l-[3px] rounded-tl-lg'], ['top-0 right-0', 'border-t-[3px] border-r-[3px] rounded-tr-lg'], ['bottom-0 left-0', 'border-b-[3px] border-l-[3px] rounded-bl-lg'], ['bottom-0 right-0', 'border-b-[3px] border-r-[3px] rounded-br-lg']].map(([pos, br]) => (
          <span key={br} className={`absolute h-8 w-8 border-[#F0607A] ${pos} ${br}`} />
        ))}
        <div className="scan-line absolute left-3 right-3 h-[2.5px] rounded-full bg-gradient-to-r from-transparent via-[#F0607A] to-transparent shadow-[0_0_14px_2px_rgba(200,16,46,.55)]" />
        <div className="absolute -bottom-10 left-0 right-0 text-center">
          <p className="text-xs text-zinc-600">对准企业门头「监管码」或营业执照二维码</p>
          <p className="mt-1.5 text-[11px] text-zinc-400">GPS 已开启 · 检查记录将附位置水印</p>
        </div>
      </div>
      <div className="mt-14 px-4">
        <Card className="p-4">
          <div className="flex items-start justify-between">
            <div>
              <b className="text-base text-zinc-900">阿克苏市光明乳品有限公司</b>
              <p className="tnum mt-1 text-xs text-zinc-500">91652901MA77XXXXX4 · 食品生产 · 阿克苏市</p>
            </div>
            <span className="rounded-md border border-emerald-200 bg-emerald-50 px-2 py-0.5 text-[11px] font-semibold text-emerald-600">正常</span>
          </div>
          <div className="mt-3 grid grid-cols-4 border-t border-slate-100 pt-3 text-center">
            {[['8', 'text-zinc-900', '历史检查'], ['3', 'text-amber-600', '整改记录'], ['2', 'text-red-600', '活跃预警'], ['5', 'text-emerald-600', '合规报告']].map(([n, tc, l]) => (
              <div key={l}><b className={`tnum block text-lg font-extrabold ${tc}`}>{n}</b><span className="text-[9px] text-zinc-400">{l}</span></div>
            ))}
          </div>
        </Card>
        <div className="mt-3.5 flex gap-2.5"><DBtn>发起现场检查</DBtn><DBtn tone="gray">查看档案</DBtn></div>
        <div className="h-4" />
      </div>
    </PhoneFrame>
  )
}

/* ================= 4 现场检查 ================= */
export function InspInspect() {
  return (
    <PhoneFrame tabbar={<TabBar accent="red" items={[
      { icon: <ShieldCheck size={23} strokeWidth={1.8} />, label: '工作台' },
      { icon: <ClipboardList size={23} strokeWidth={1.8} />, label: '检查', active: true },
      { icon: null, label: '扫码查企', scan: true },
      { icon: <AlertTriangle size={23} strokeWidth={1.8} />, label: '预警' },
      { icon: <User size={23} strokeWidth={1.8} />, label: '我的' },
    ]} />}>
      <div className="sticky top-0 z-20 flex flex-shrink-0 items-center justify-between border-b border-slate-200/70 bg-white/95 px-4 py-2.5 pt-3 text-[15px] backdrop-blur">
        <span className="text-red-600">‹ 返回</span><span className="font-semibold text-zinc-900">现场检查</span>
        <span className="flex items-center gap-1 text-[12px] text-emerald-600"><span className="dot-breathe h-1.5 w-1.5 rounded-full bg-emerald-500" />GPS已打卡</span>
      </div>
      <p className="px-5 pb-3 pt-1 text-[13px] text-zinc-500">光明乳品 · 食品生产安全（23 项）· 已选 4</p>
      <Segmented options={['食品生产安全', '食品流通', '药械', '特设']} active={0} />
      <div className="px-4">
        <Card>
          {[
            { t: '生产环境与设施卫生', s: 'FP001 · 车间清洁 / 防虫防鼠 / 通风', ok: true },
            { t: '从业人员健康证明', s: 'FP006 · 有效期核验', ok: false },
            { t: '原辅料进货台账', s: 'FP011 · 索证索票', ok: true },
          ].map((it) => (
            <div key={it.t}>
              <div className="flex items-center gap-3 px-4 py-3">
                <span className="grid h-[23px] w-[23px] flex-shrink-0 place-items-center rounded-lg bg-gradient-to-b from-[#D5263D] to-[#B00E24]"><Check size={13} strokeWidth={3.5} className="text-white" /></span>
                <div className="flex-1"><b className="text-[15px] text-zinc-900">{it.t}</b><p className="tnum mt-0.5 text-[11px] text-zinc-400">{it.s}</p></div>
                <span className={`rounded-md px-2 py-0.5 text-[11px] font-bold ${it.ok ? 'border border-emerald-200 bg-emerald-50 text-emerald-600' : 'bg-gradient-to-b from-[#D5263D] to-[#B00E24] text-white'}`}>{it.ok ? '合格' : '不合格'}</span>
              </div>
              <CellDivider />
            </div>
          ))}
          <div className="flex items-center gap-3 px-4 py-3">
            <span className="h-[23px] w-[23px] flex-shrink-0 rounded-lg border-[1.5px] border-slate-300" />
            <b className="flex-1 text-[15px] text-zinc-900">食品添加剂使用规范</b>
          </div>
          <CellDivider />
          <div className="flex items-center gap-3 px-4 py-3">
            <span className="h-[23px] w-[23px] flex-shrink-0 rounded-lg border-[1.5px] border-slate-300" />
            <b className="flex-1 text-[15px] text-zinc-500">其余 18 项（标签标识 / 贮存运输…）</b>
          </div>
        </Card>
        <SectionTitle accent="red" more="不合格项自动带入">问题登记</SectionTitle>
        <Card className="border-red-200 bg-red-50/40 p-4">
          <p className="text-[15px] leading-relaxed text-zinc-900">后厨排水沟清洁不彻底；1 名员工健康证过期仍在岗。</p>
          <p className="mt-2 text-xs text-zinc-500">整改要求：限期 3 日深度清洁复查 · 补办健康证</p>
          <div className="mt-3 flex gap-2.5">
            <div className="flex h-[58px] w-[58px] flex-col items-center justify-center gap-0.5 rounded-xl border border-red-200 bg-white"><Camera size={19} className="text-red-600" strokeWidth={1.8} /><span className="text-[8.5px] font-bold text-red-600">水印相机</span></div>
            <div className="flex h-[58px] w-[58px] flex-col items-center justify-center gap-0.5 rounded-xl border border-slate-200 bg-white"><Video size={19} className="text-zinc-500" strokeWidth={1.8} /><span className="text-[8.5px] font-bold text-zinc-500">视频</span></div>
            <div className="grid h-[58px] w-[58px] place-items-center rounded-xl border-[1.5px] border-dashed border-red-300 text-red-600"><Plus size={21} strokeWidth={2.2} /></div>
            <p className="self-center text-[10px] leading-snug text-zinc-500">照片自动叠加<br />时间 / 定位 / 工号</p>
          </div>
        </Card>
        <div className="mt-4 rounded-2xl bg-gradient-to-b from-[#D5263D] to-[#B00E24] py-3.5 text-center text-[16px] font-semibold text-white shadow-xl shadow-red-700/25">提交 · 自动生成整改通知书</div>
        <p className="mb-4 mt-2.5 text-center text-[11px] text-zinc-400">弱网自动保存草稿 · 提交即通知企业与预警系统</p>
      </div>
    </PhoneFrame>
  )
}

/* ================= 5 预警 ================= */
export function InspAlerts() {
  return (
    <PhoneFrame tabbar={<TabBar accent="red" items={[
      { icon: <ShieldCheck size={23} strokeWidth={1.8} />, label: '工作台' },
      { icon: <ClipboardList size={23} strokeWidth={1.8} />, label: '检查' },
      { icon: null, label: '扫码查企', scan: true },
      { icon: <AlertTriangle size={23} strokeWidth={1.8} />, label: '预警', active: true, badge: 2 },
      { icon: <User size={23} strokeWidth={1.8} />, label: '我的' },
    ]} />}>
      <div className="px-5 pb-3 pt-2"><h3 className="text-[26px] font-extrabold tracking-tight text-zinc-900">预警</h3></div>
      <Segmented options={['待处理 2', '处理中 1', '已督办 1', '已处置 18']} active={1} />
      <div className="px-4">
        <Card className="border-red-200 p-4">
          <div className="flex items-center gap-1.5"><LevelBadge level="高" /><span className="rounded-md border border-red-200 bg-red-50 px-2 py-0.5 text-[11px] font-semibold text-red-600">实时</span><span className="tnum text-[11px] text-zinc-400">#14 · 09-10 00:02</span></div>
          <b className="mt-2.5 block text-base text-zinc-900">食品经营许可证已过期</b>
          <p className="mt-2 text-[13px] leading-relaxed text-zinc-600">光明乳品的食品生产许可证已于 2026-09-01 过期，请立即督促企业办理续期或依法处置。</p>
          <div className="mt-3 space-y-1 rounded-xl bg-slate-50 p-3 text-xs text-zinc-700 ring-1 ring-inset ring-slate-200/70">
            <p><span className="text-zinc-400">属地　　</span>阿克苏市 · 食品生产</p>
            <p><span className="text-zinc-400">派单　　</span>吴执法（属地自动派单）</p>
            <p><span className="text-zinc-400">督办　　</span><b className="text-amber-600">19:24:11 后升级至科长</b></p>
            <p><span className="text-zinc-400">升级链　</span>科员 → 科长 → 局领导 → 地区局</p>
          </div>
          <div className="mt-3.5 flex gap-2.5"><DBtn>接收</DBtn><DBtn tone="gray">退回</DBtn></div>
        </Card>
        <Card className="mt-3 flex items-center gap-3 p-3">
          <LevelBadge level="中" />
          <div className="flex-1"><b className="text-sm text-zinc-900">合规报告超 45 天未提交</b><p className="mt-1 text-[11px] text-zinc-400">库车某商贸 · 已督办 1 次 → 科长</p></div>
        </Card>
        <Card className="mt-3 flex items-center gap-3 p-3">
          <LevelBadge level="低" />
          <div className="flex-1"><b className="text-sm text-zinc-900">注册申请退回超 30 天未重提</b><p className="mt-1 text-[11px] text-zinc-400">注册备案异常 · 每日定时规则</p></div>
        </Card>
        <p className="mb-4 mt-2 text-center text-[11px] text-zinc-400">每日 02:00 全量扫描 · 每小时督办升级检查</p>
      </div>
    </PhoneFrame>
  )
}

/* ================= 6 我的 ================= */
export function InspProfile() {
  return (
    <PhoneFrame tabbar={<TabBar accent="red" items={[
      { icon: <ShieldCheck size={23} strokeWidth={1.8} />, label: '工作台' },
      { icon: <ClipboardList size={23} strokeWidth={1.8} />, label: '检查' },
      { icon: null, label: '扫码查企', scan: true },
      { icon: <AlertTriangle size={23} strokeWidth={1.8} />, label: '预警' },
      { icon: <User size={23} strokeWidth={1.8} />, label: '我的', active: true },
    ]} />}>
      <div className="flex-shrink-0 bg-gradient-to-b from-[#C8102E] to-[#A50D22] px-4 py-4">
        <div className="flex items-center gap-3.5">
          <div className="grid h-[58px] w-[58px] place-items-center rounded-[17px] border-2 border-[#E5C893]/70 bg-white/95 text-xl font-extrabold text-[#B00E24] shadow-lg">吴</div>
          <div className="flex-1">
            <div className="flex items-center gap-2"><b className="text-lg text-white">吴执法</b><span className="rounded-md border border-[#E5C893]/50 bg-[#E5C893]/20 px-2 py-0.5 text-[11px] font-semibold text-[#F4DCA0]">二级执法员</span></div>
            <p className="tnum mt-1.5 text-xs text-red-100/90">执法证 AKSF-0150 · 0909-2123456</p>
            <p className="mt-0.5 text-xs text-red-100/75">阿克苏市市监局 · 食品安全监管科</p>
          </div>
        </div>
      </div>
      <div className="p-4">
        <Card className="p-4">
          <div className="mb-3 flex items-baseline justify-between"><b className="text-sm font-bold text-zinc-900">本月执法业绩</b><span className="tnum text-[11px] text-zinc-400">2026-09</span></div>
          <div className="flex text-center">
            {[['6', 'text-zinc-900', '现场检查'], ['4', 'text-zinc-900', '整改验收'], ['9', 'text-zinc-900', '预警处置'], ['100%', 'text-emerald-600', '按时率']].map(([n, tc, l], i) => (
              <div key={l} className={`flex-1 ${i > 0 ? 'border-l border-slate-100' : ''}`}><b className={`tnum block text-[22px] font-extrabold ${tc}`}>{n}</b><span className="text-[10px] text-zinc-400">{l}</span></div>
            ))}
          </div>
        </Card>
        <Card className="mt-3">
          <Cell icon={<IconTile tone="gray" size="sm" icon={<ClipboardList size={19} strokeWidth={1.8} />} />} title="检查记录" />
          <CellDivider />
          <Cell icon={<IconTile tone="rose" size="sm" icon={<Download size={19} strokeWidth={1.8} />} />} title="待同步草稿" value={<span className="rounded-md border border-red-200 bg-red-50 px-2 py-0.5 text-[11px] font-semibold text-red-600">1</span>} />
          <CellDivider />
          <Cell icon={<IconTile tone="sky" size="sm" icon={<MapPin size={19} strokeWidth={1.8} />} />} title="辖区企业地图" value={<span className="text-xs text-zinc-400">阿克苏市 10 家</span>} />
          <CellDivider />
          <Cell icon={<IconTile tone="green" size="sm" icon={<Phone size={19} strokeWidth={1.8} />} />} title="科所通讯录" />
        </Card>
        <Card className="mt-3"><Cell title="退出登录" danger arrow={false} /></Card>
        <p className="my-3 text-center text-[11px] text-zinc-400">阿克苏地区市场监督管理局 · 执法端 v1.0.0</p>
      </div>
    </PhoneFrame>
  )
}
