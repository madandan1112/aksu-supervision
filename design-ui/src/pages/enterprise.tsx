import { Bell, FileText, Wrench, BarChart3, Building2, Home, User, Check, Clock, Camera, Video, Plus, Phone } from 'lucide-react'
import { PhoneFrame, TabBar } from '../components/frames'
import { Emblem } from '../components/Emblem'
import { Card, Cell, CellDivider, SectionTitle, Tag, Segmented, StripedBar, CountRing, IosSwitch, Stars, IconTile, PrimaryBtn } from '../components/wx'

/* ================= 1 登录 ================= */
export function EntLogin() {
  return (
    <PhoneFrame>
      <div className="relative flex-1 overflow-hidden bg-gradient-to-b from-blue-600 to-blue-700 pb-14">
        <div className="absolute -right-8 -top-10 h-44 w-44 rounded-full bg-white/[.07]" />
        <div className="absolute -bottom-6 right-8 h-24 w-24 rounded-full bg-white/[.05]" />
        <div className="px-7 pb-16 pt-12">
          <div className="grid h-[96px] w-[96px] place-items-center rounded-[26px] border border-white/50 bg-gradient-to-b from-white to-blue-50 shadow-xl shadow-blue-900/25">
            <Emblem size={68} />
          </div>
          <h1 className="mt-4 text-[26px] font-extrabold tracking-tight text-white">阿克苏市场监管</h1>
          <p className="mt-1.5 text-[13px] text-blue-100/90">企业商户移动服务平台 · 官方渠道</p>
        </div>
        <div className="relative -mt-8 px-4">
          <Card className="px-4 pb-4 pt-5">
            <div className="rounded-2xl bg-gradient-to-b from-[#18D062] to-[#06AD56] py-3.5 text-center text-[16px] font-semibold text-white shadow-lg shadow-emerald-600/25">微信一键登录</div>
            <div className="my-3.5 flex items-center gap-3"><span className="h-px flex-1 bg-slate-200" /><span className="text-[11px] text-slate-400">或使用账号登录</span><span className="h-px flex-1 bg-slate-200" /></div>
            <div className="rounded-xl border border-slate-200/70 bg-slate-50 px-3.5 py-3 text-sm text-slate-400">手机号 / 用户名</div>
            <div className="mt-2 rounded-xl border border-slate-200/70 bg-slate-50 px-3.5 py-3 text-sm text-slate-400">密码</div>
            <div className="mt-3 rounded-2xl border border-slate-200/80 bg-white py-3.5 text-center text-[16px] font-semibold text-zinc-800 shadow-sm">登 录</div>
            <p className="mt-3 text-center text-xs text-slate-400">还没有企业账号？<span className="font-semibold text-blue-600">立即注册企业</span></p>
          </Card>
          <Card className="mt-3 px-4 py-3">
            {['注册备案 · 一次认证全流程通行', '诉求直达属地监管所 · 进度可查', '整改反馈 · 合规报告掌上办理'].map((t) => (
              <div key={t} className="flex items-center gap-2.5 py-1.5">
                <span className="grid h-5 w-5 flex-shrink-0 place-items-center rounded-full bg-gradient-to-b from-[#5BE07E] to-[#1E9E4A] shadow shadow-emerald-500/30"><Check size={11} strokeWidth={3.5} className="text-white" /></span>
                <span className="text-[13px] text-slate-500">{t}</span>
              </div>
            ))}
          </Card>
          <p className="mt-3 pb-4 text-center text-[11px] leading-relaxed text-slate-400">阿克苏地区市场监督管理局<br />登录即同意《用户协议》与《隐私政策》</p>
        </div>
      </div>
    </PhoneFrame>
  )
}

/* ================= 2 首页 ================= */
export function EntHome() {
  return (
    <PhoneFrame tabbar={<TabBar items={[
      { icon: <Home size={23} strokeWidth={1.8} />, label: '首页', active: true },
      { icon: <FileText size={23} strokeWidth={1.8} />, label: '诉求', badge: 2 },
      { icon: <Wrench size={23} strokeWidth={1.8} />, label: '整改' },
      { icon: <User size={23} strokeWidth={1.8} />, label: '我的' },
    ]} />}>
      <div className="relative flex-shrink-0 bg-gradient-to-b from-blue-600 to-blue-700 pb-12">
        <div className="absolute -right-6 -top-6 h-28 w-28 rounded-full bg-white/[.06]" />
        <div className="flex items-center justify-between px-4.5 p-4">
          <div>
            <p className="text-xs text-blue-100/80">上午好</p>
            <h3 className="mt-0.5 text-lg font-bold text-white">王建华</h3>
            <p className="mt-1 text-[11px] text-blue-100/75">阿克苏市光明乳品有限公司</p>
          </div>
          <div className="flex items-center gap-2.5">
            <div className="grid h-[34px] w-[34px] place-items-center rounded-[10px] bg-white/95"><Emblem size={26} /></div>
            <div className="relative grid h-9 w-9 place-items-center rounded-xl bg-white/[.16]">
              <Bell size={17} className="text-white dot-breathe" />
              <span className="absolute -right-1 -top-1 grid h-4 w-4 place-items-center rounded-full bg-white text-[10px] font-bold text-blue-600">2</span>
            </div>
          </div>
        </div>
      </div>
      <div className="relative -mt-8 px-4">
        <Card className="p-4">
          <div className="flex items-start justify-between">
            <div>
              <Tag tone="green"><span className="h-1.5 w-1.5 rounded-full bg-emerald-500" />正常运营</Tag>
              <p className="mt-2.5 text-[13px] text-slate-500">食品生产企业 · 阿克苏市英巴扎街道</p>
              <p className="tnum mt-0.5 text-xs text-slate-400">91652901MA77XXXXX4</p>
            </div>
            <div className="rounded-xl border border-blue-100 bg-blue-50/70 px-4 py-2 text-center">
              <b className="tnum block bg-gradient-to-b from-blue-500 to-blue-700 bg-clip-text text-2xl font-extrabold leading-none text-transparent">2</b>
              <span className="text-[10px] text-slate-400">待办事项</span>
            </div>
          </div>
          <div className="mt-3 flex border-t border-slate-100 pt-2.5 text-center">
            {[['3', '整改记录'], ['5', '合规报告'], ['0', '违规预警'], ['12', '员工']].map(([n, l], i) => (
              <div key={l} className={`flex-1 ${i > 0 ? 'border-l border-slate-100' : ''}`}><b className="tnum block text-[17px] font-extrabold text-zinc-900">{n}</b><span className="text-[10px] text-slate-400">{l}</span></div>
            ))}
          </div>
        </Card>
        <Card className="mt-3 flex items-center gap-3 border-amber-200/70 bg-gradient-to-r from-amber-50 to-[#FFF7E6] p-3">
          <IconTile tone="amber" icon={<Clock size={22} strokeWidth={1.9} />} />
          <p className="flex-1 text-[13px] text-zinc-800"><b className="text-amber-600">2 项整改待反馈</b><br /><span className="text-[11px] text-slate-400">最早截止 09-15 · 点击去处理</span></p>
        </Card>
        <SectionTitle>常用功能</SectionTitle>
        <Card className="grid grid-cols-4 py-4">
          {[
            { icon: <FileText size={24} strokeWidth={1.8} />, label: '提交诉求', tone: 'blue' as const },
            { icon: <Wrench size={24} strokeWidth={1.8} />, label: '整改反馈', tone: 'amber' as const },
            { icon: <BarChart3 size={24} strokeWidth={1.8} />, label: '上传报告', tone: 'green' as const },
            { icon: <Building2 size={24} strokeWidth={1.8} />, label: '企业信息', tone: 'gray' as const },
          ].map((f) => (
            <div key={f.label} className="text-center">
              <IconTile icon={f.icon} tone={f.tone} />
              <p className="mt-2 text-[11.5px] font-medium text-zinc-800">{f.label}</p>
            </div>
          ))}
        </Card>
        <SectionTitle more="全部 3 ›">待整改通知</SectionTitle>
        {[
          { t: '后厨卫生不达标', d: '剩 6 天', no: 'ZG202609083421 · 食品安全监管科 · 吴执法', pct: 60, danger: false },
          { t: '从业人员健康证过期', d: '剩 2 天', no: 'ZG202609012208 · 食品安全监管科 · 吴执法', pct: 87, danger: true },
        ].map((r) => (
          <Card key={r.no} className="relative overflow-hidden p-3.5 pl-4">
            <span className={`absolute inset-y-0 left-0 w-1 ${r.danger ? 'bg-gradient-to-b from-rose-400 to-rose-600' : 'bg-gradient-to-b from-amber-400 to-amber-500'}`} />
            <div className="flex items-center justify-between"><b className="text-[15px] text-zinc-900">{r.t}</b><span className={`text-xs font-bold ${r.danger ? 'text-rose-500' : 'text-amber-600'}`}>{r.d}</span></div>
            <p className="tnum mb-2 mt-1 text-[11px] text-slate-400">{r.no}</p>
            <StripedBar pct={r.pct} danger={r.danger} />
          </Card>
        ))}
        <div className="h-4" />
      </div>
    </PhoneFrame>
  )
}

/* ================= 3 注册向导 ================= */
export function EntRegister() {
  const steps = ['执照', '信息', '资质', '照片', '提交']
  return (
    <PhoneFrame>
      <div className="flex-shrink-0 bg-gradient-to-b from-blue-600 to-blue-700 pb-9">
        <div className="flex items-center justify-between px-4 py-2.5 text-[15px] text-white">
          <span>‹ 返回</span><span className="font-semibold">企业注册认证</span><span className="text-[13px] text-blue-100">保存</span>
        </div>
        <div className="flex items-center gap-2 px-5">
          {steps.map((s, i) => (
            <div key={s} className="flex flex-1 flex-col items-center gap-1.5">
              <span className={`grid h-6 w-6 place-items-center rounded-full text-xs font-bold ${i < 1 ? 'bg-white text-blue-600 shadow' : i === 1 ? 'bg-white text-blue-600 ring-4 ring-white/30' : 'bg-white/25 text-white'}`}>{i < 1 ? <Check size={13} strokeWidth={3.5} /> : i + 1}</span>
              <span className={`text-[10px] ${i <= 1 ? 'font-semibold text-white' : 'text-blue-100/70'}`}>{s}</span>
            </div>
          ))}
        </div>
      </div>
      <div className="relative -mt-6 px-4">
        <Card className="flex items-center gap-3 border-emerald-200/70 p-3">
          <IconTile tone="green" icon={<Check size={22} strokeWidth={2.2} />} />
          <div className="flex-1"><b className="text-sm text-zinc-900">营业执照已识别</b><p className="mt-0.5 text-[11px] text-slate-400">OCR 已自动回填 · 请核对信息</p></div>
          <span className="text-[13px] font-medium text-blue-600">重新识别</span>
        </Card>
        <Card className="mt-2.5">
          {[
            ['企业名称 *', '库车市金桥建材有限公司'],
            ['统一社会信用代码 *', '91652923MA7X2XK29K'],
          ].map(([l, v]) => (
            <div key={l} className="border-b border-slate-100 px-4 py-3">
              <p className="text-[11.5px] text-slate-400">{l}</p>
              <p className="tnum mt-1 text-[15px] font-semibold text-zinc-900">{v}</p>
            </div>
          ))}
          <Cell title="法定代表人" value={<span className="text-[14px] text-slate-500">刘金桥</span>} />
          <CellDivider />
          <Cell title="联系电话" value={<span className="tnum text-[14px] text-slate-500">137****2216</span>} />
          <CellDivider />
          <Cell title="所属区域 *" value={<span className="text-[13px] text-slate-500">库车市 · 16 乡镇 ›</span>} />
        </Card>
        <SectionTitle>行业类型 *</SectionTitle>
        <Card className="p-3.5">
          <div className="flex flex-wrap gap-1.5">
            <span className="rounded-lg bg-gradient-to-b from-blue-500 to-blue-600 px-3 py-1.5 text-xs font-semibold text-white shadow-md shadow-blue-600/25">一般生产加工企业</span>
            {['食品生产', '药品零售', '餐饮服务', '特种设备', '农贸市场', '+19 类'].map((c) => (
              <span key={c} className="rounded-lg bg-slate-100 px-3 py-1.5 text-xs text-slate-500">{c}</span>
            ))}
          </div>
          <div className="mt-3 flex items-start gap-2 rounded-xl border border-amber-200/60 bg-amber-50/70 p-2.5">
            <Clock size={15} className="mt-0.5 flex-shrink-0 text-amber-600" />
            <p className="text-xs leading-relaxed text-amber-700">该行业需上传：营业执照 · 生产许可证 · 排污许可（下一步）</p>
          </div>
        </Card>
        <PrimaryBtn>继续 · 上传资质证照</PrimaryBtn>
        <div className="h-4" />
      </div>
    </PhoneFrame>
  )
}

/* ================= 4 诉求 ================= */
export function EntAppeals() {
  return (
    <PhoneFrame tabbar={<TabBar items={[
      { icon: <Home size={23} strokeWidth={1.8} />, label: '首页' },
      { icon: <FileText size={23} strokeWidth={1.8} />, label: '诉求', active: true },
      { icon: <Wrench size={23} strokeWidth={1.8} />, label: '整改' },
      { icon: <User size={23} strokeWidth={1.8} />, label: '我的' },
    ]} />}>
      <div className="flex-shrink-0 bg-white px-4 py-3 text-center text-[16px] font-semibold text-zinc-900">我的诉求</div>
      <Segmented options={['待处理 2', '进行中 1', '已办结 7', '全部 12']} active={1} />
      <div className="px-4">
        <Card className="p-4">
          <div className="flex items-center justify-between"><b className="text-[15px] text-zinc-900">许可证办理咨询</b><span className="text-xs font-semibold text-blue-600">已分配 · 注册登记科</span></div>
          <p className="mt-2 text-[13px] leading-relaxed text-slate-500">想咨询食品经营许可证变更地址需要什么材料，办理时限多久？</p>
          <div className="mt-3 flex items-center">
            {[['提交', true], ['分流', true], ['处理中', 'cur'], ['评价', false]].map(([l, st], i) => (
              <div key={l as string} className="flex flex-1 items-center">
                <div className="flex flex-col items-center gap-1">
                  <span className={`grid h-[23px] w-[23px] place-items-center rounded-full text-[10px] font-bold ${st === true ? 'bg-gradient-to-b from-emerald-400 to-emerald-600 text-white shadow shadow-emerald-500/30' : st === 'cur' ? 'dot-breathe bg-gradient-to-b from-blue-500 to-blue-700 text-white shadow shadow-blue-600/40' : 'bg-slate-200'}`}>
                    {st === true ? <Check size={12} strokeWidth={3.5} className="text-white" /> : st === 'cur' ? '3' : ''}
                  </span>
                  <span className={`text-[9px] ${st === 'cur' ? 'font-bold text-blue-600' : st === true ? 'text-emerald-600' : 'text-slate-400'}`}>{l as string}</span>
                </div>
                {i < 3 ? <span className={`mb-4 h-[2px] flex-1 ${st === true ? 'bg-emerald-400' : st === 'cur' ? 'bg-blue-500' : 'bg-slate-200'}`} /> : null}
              </div>
            ))}
          </div>
          <p className="tnum mt-2.5 text-[11px] text-slate-400">2026-09-08 10:24 提交 · 已耗时 2 天</p>
        </Card>
        <Card className="p-4">
          <div className="flex items-center justify-between"><b className="text-[15px] text-zinc-900">夜间生产噪音扰民</b><span className="text-xs font-semibold text-emerald-600">已办结</span></div>
          <p className="mt-2 text-[13px] leading-relaxed text-slate-500">处理结果：已现场核查并责令限产整改，夜间 22 点后停止产线。</p>
          <div className="mt-2.5 flex items-center justify-between rounded-xl border border-emerald-100 bg-emerald-50/60 px-3 py-2">
            <span className="text-xs text-slate-400">09-05 办结 · 执法稽查科</span><Stars />
          </div>
        </Card>
        <Card className="p-4">
          <div className="flex items-center justify-between"><b className="text-[15px] text-zinc-900">超市价格标签不规范</b><span className="text-xs font-semibold text-amber-600">待处理</span></div>
          <p className="mt-2 text-[13px] leading-relaxed text-slate-500">货架部分商品标价与结算价不一致，请核查。</p>
          <p className="tnum mt-2 text-[11px] text-slate-400">09-09 16:40 提交 · 待分流</p>
        </Card>
        <div className="h-16" />
      </div>
      <div className="pointer-events-none sticky bottom-3 z-40 mr-5 ml-auto h-14 w-14 rounded-[20px] bg-gradient-to-b from-blue-500 to-blue-600 shadow-xl shadow-blue-600/40 ring-1 ring-white/30 ring-inset">
        <Plus size={26} className="m-auto py-3.5 text-white" strokeWidth={2.4} />
      </div>
    </PhoneFrame>
  )
}

/* ================= 5 整改反馈 ================= */
export function EntRectify() {
  return (
    <PhoneFrame>
      <div className="sticky top-0 z-20 flex flex-shrink-0 items-center justify-between bg-white px-4 py-2.5 text-[15px]">
        <span className="text-blue-600">‹ 返回</span><span className="font-semibold text-zinc-900">整改反馈</span><span className="w-10" />
      </div>
      <div className="relative mx-4 mb-3 mt-1 flex-shrink-0 overflow-hidden rounded-2xl bg-gradient-to-br from-blue-500 to-blue-700 p-4 shadow-xl shadow-blue-600/30">
        <div className="absolute -right-5 -top-5 h-24 w-24 rounded-full bg-white/[.08]" />
        <div className="flex items-center gap-3.5">
          <CountRing pct={78} center="6" sub="天" />
          <div>
            <p className="text-xs text-blue-100/85">距整改截止</p>
            <b className="tnum text-xl font-bold tracking-tight text-white">6 天 14 小时</b>
            <p className="mt-0.5 text-[11px] text-blue-100/60">逾期未反馈将触发督办预警</p>
          </div>
        </div>
      </div>
      <div className="px-4">
        <Card className="p-4">
          <div className="flex items-baseline justify-between"><b className="tnum text-base text-zinc-900">ZG202609083421</b><span className="text-xs font-semibold text-amber-600">待整改</span></div>
          <div className="mt-2.5 space-y-0.5 text-[13px] text-slate-500">
            <p><span className="text-slate-400">检查科室　</span>食品安全监管科 · 吴执法</p>
            <p><span className="text-slate-400">检查类型　</span>食品生产安全 · 3 项</p>
            <p><span className="text-slate-400">存在问题　</span>后厨卫生不达标；健康证过期</p>
            <p><span className="text-slate-400">整改要求　</span>限期清洁、补证并提交照片</p>
          </div>
        </Card>
        <Card className="mt-3 p-3.5">
          <b className="text-[13px] font-bold text-zinc-900">流转记录</b>
          <div className="mt-2.5">
            <div className="flex gap-2.5">
              <div className="flex flex-col items-center"><span className="h-2.5 w-2.5 rounded-full bg-gradient-to-b from-blue-400 to-blue-600 ring-4 ring-blue-100" /><span className="h-5 w-[2px] bg-slate-100" /></div>
              <div className="pb-2.5"><p className="text-[13px] text-zinc-900">检查完成，通知书下发</p><p className="tnum mt-0.5 text-[11px] text-slate-400">09-08 10:24 · 站内信已送达</p></div>
            </div>
            <div className="flex gap-2.5"><span className="h-2.5 w-2.5 flex-shrink-0 rounded-full bg-slate-200" /><p className="text-[13px] text-slate-400">企业提交反馈（待完成）</p></div>
          </div>
        </Card>
        <Card className="mt-3 border-blue-200/70 bg-gradient-to-b from-blue-50/60 to-white p-4">
          <div className="mb-2.5 flex items-center justify-between"><b className="text-sm text-blue-700">提交整改反馈</b><span className="text-[11px] text-slate-400">证据支持 OCR 批量识别</span></div>
          <div className="rounded-xl border border-slate-200/60 bg-slate-50 px-3 py-2.5 text-[13px] leading-relaxed text-zinc-800">已完成后厨深度清洁并消毒；过期健康证已补办（2 张）。</div>
          <div className="mt-3 flex gap-2.5">
            <div className="grid h-[62px] w-[62px] place-items-center gap-0.5 rounded-xl border border-blue-200/60 bg-gradient-to-b from-blue-100/80 to-blue-50">
              <div className="flex flex-col items-center gap-0.5"><Camera size={19} className="text-blue-700" strokeWidth={1.8} /><span className="text-[9px] font-bold text-blue-700">3 张照片</span></div>
            </div>
            <div className="grid h-[62px] w-[62px] place-items-center rounded-xl border border-blue-200/60 bg-gradient-to-b from-blue-100/80 to-blue-50">
              <div className="flex flex-col items-center gap-0.5"><Video size={19} className="text-blue-700" strokeWidth={1.8} /><span className="text-[9px] font-bold text-blue-700">1 段视频</span></div>
            </div>
            <div className="grid h-[62px] w-[62px] place-items-center rounded-xl border-[1.5px] border-dashed border-slate-300 text-2xl text-blue-600">＋</div>
          </div>
          <PrimaryBtn>提交反馈 · 通知执法员核验</PrimaryBtn>
        </Card>
        <div className="h-4" />
      </div>
    </PhoneFrame>
  )
}

/* ================= 6 我的 ================= */
export function EntProfile() {
  return (
    <PhoneFrame tabbar={<TabBar items={[
      { icon: <Home size={23} strokeWidth={1.8} />, label: '首页' },
      { icon: <FileText size={23} strokeWidth={1.8} />, label: '诉求' },
      { icon: <Wrench size={23} strokeWidth={1.8} />, label: '整改' },
      { icon: <User size={23} strokeWidth={1.8} />, label: '我的', active: true },
    ]} />}>
      <div className="relative flex-shrink-0 overflow-hidden bg-gradient-to-b from-blue-600 to-blue-700 px-4 pb-12 pt-2">
        <div className="absolute -bottom-8 -right-7 h-28 w-28 rounded-full bg-white/[.06]" />
        <div className="flex items-center gap-3.5">
          <div className="grid h-[58px] w-[58px] place-items-center rounded-[18px] border-2 border-white/50 bg-white/20 text-xl font-extrabold text-white">王</div>
          <div className="flex-1"><b className="text-lg text-white">王建华</b><p className="mt-1 text-xs text-blue-100/80">138****5678 · 企业负责人权限</p></div>
          <span className="rounded-full bg-white/[.16] px-3 py-1.5 text-xs text-white">切换账号</span>
        </div>
      </div>
      <div className="relative -mt-8 px-4">
        <Card className="p-4">
          <div className="flex items-center justify-between"><b className="text-[15px] text-zinc-900">阿克苏市光明乳品有限公司</b><span className="text-xs font-semibold text-emerald-600">正常运营</span></div>
          <div className="my-2.5 flex flex-wrap gap-2">
            <Tag tone="blue">食品生产企业</Tag><Tag tone="gray">阿克苏市</Tag><Tag tone="gray">2019 年注册</Tag>
          </div>
          <div className="flex border-t border-slate-100 pt-2.5 text-center">
            {[['3', '整改记录'], ['5', '合规报告'], ['0', '违规预警'], ['12', '员工']].map(([n, l], i) => (
              <div key={l} className={`flex-1 ${i > 0 ? 'border-l border-slate-100' : ''}`}><b className="tnum block text-[17px] font-extrabold text-zinc-900">{n}</b><span className="text-[10px] text-slate-400">{l}</span></div>
            ))}
          </div>
        </Card>
        <SectionTitle>企业管理</SectionTitle>
        <Card>
          <Cell icon={<IconTile tone="blue" size="sm" icon={<Building2 size={19} strokeWidth={1.8} />} />} title="企业信息维护" />
          <CellDivider />
          <Cell icon={<IconTile tone="gray" size="sm" icon={<User size={19} strokeWidth={1.8} />} />} title="员工账号管理" value={<span className="text-xs text-slate-400">一般人员只读</span>} />
          <CellDivider />
          <Cell icon={<IconTile tone="green" size="sm" icon={<Bell size={19} strokeWidth={1.8} />} />} title="消息通知" arrow={false}><IosSwitch /></Cell>
          <CellDivider />
          <Cell icon={<IconTile tone="amber" size="sm" icon={<Phone size={19} strokeWidth={1.8} />} />} title="联系属地监管所" value={<span className="text-xs text-slate-400">阿克苏市市监局</span>} />
        </Card>
        <Card className="mt-3"><Cell title="退出登录" danger arrow={false} /></Card>
        <p className="my-3 text-center text-[11px] text-slate-400">阿克苏地区市场监督管理局 · v1.0.0</p>
      </div>
    </PhoneFrame>
  )
}
