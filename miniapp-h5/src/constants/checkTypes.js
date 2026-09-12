export const deptCheckTypes = {
  // 1. 食品生产安全监督管理科
  foodProduction: [
    { code: 'FP001', name: '食品生产许可核查', desc: '食品生产企业许可条件、变更、延续核查' },
    { code: 'FP002', name: '生产环境条件检查', desc: '厂区环境、车间布局、清洁消毒设施' },
    { code: 'FP003', name: '生产设备管理', desc: '设备维护、清洗消毒、校验记录' },
    { code: 'FP004', name: '原料进货查验', desc: '原料索证索票、进货台账、检验报告' },
    { code: 'FP005', name: '生产过程控制', desc: '关键控制点监控、生产工艺执行' },
    { code: 'FP006', name: '食品添加剂使用', desc: '添加剂使用范围、用量、台账记录' },
    { code: 'FP007', name: '出厂检验管理', desc: '产品出厂检验报告、留样管理' },
    { code: 'FP008', name: '食品标签标识', desc: '产品标签、说明书合规性检查' },
    { code: 'FP009', name: '追溯体系建设', desc: '原料来源、产品流向追溯记录' },
    { code: 'FP010', name: '不合格品处置', desc: '不合格品召回、销毁、处置记录' },
    { code: 'FP011', name: '人员健康管理', desc: '从业人员健康证明、培训记录' },
    { code: 'FP012', name: '食品安全自查', desc: '自查制度、自查报告、整改记录' },
  ],

  // 2. 食品流通安全监督管理科（含餐饮服务）
  foodCirculation: [
    { code: 'FC001', name: '食品经营许可核查', desc: '食品经营许可条件、变更、延续核查' },
    { code: 'FC002', name: '进货查验记录', desc: '食品进货台账、检验合格证明' },
    { code: 'FC003', name: '储存条件检查', desc: '冷藏冷冻设备温度、通风防潮、分类存放' },
    { code: 'FC004', name: '过期食品清理', desc: '临期食品管理、过期食品处置' },
    { code: 'FC005', name: '散装食品管理', desc: '散装食品标签、防尘防蝇设施、容器消毒' },
    { code: 'FC006', name: '餐饮具清洗消毒', desc: '餐饮具清洗消毒设备、消毒液浓度、记录' },
    { code: 'FC007', name: '网络食品经营', desc: '线上商家资质、信息公示、配送容器合规' },
    { code: 'FC008', name: '学校食堂检查', desc: '学校食堂卫生、留样、从业人员健康、明厨亮灶' },
    { code: 'FC009', name: '农村集体聚餐', desc: '农村聚餐备案、人员健康、加工场所卫生' },
    { code: 'FC010', name: '小餐饮小食杂', desc: '小餐饮备案、经营条件、食品安全管理' },
    { code: 'FC011', name: '食品摊贩管理', desc: '食品摊贩登记、经营区域、卫生条件' },
  ],

  // 3. 药品医疗器械监督管理科
  drugMedical: [
    { code: 'DM001', name: '药品经营许可核查', desc: '药品零售企业许可条件、变更核查' },
    { code: 'DM002', name: '处方药销售管理', desc: '处方药凭处方销售、执业药师在岗、处方留存' },
    { code: 'DM003', name: '特殊药品管理', desc: '麻醉药品、精神药品、毒性药品、放射性药品管理' },
    { code: 'DM004', name: '药品储存条件', desc: '温湿度监控、阴凉/冷藏药品分区、冷链管理' },
    { code: 'DM005', name: '医疗器械经营备案', desc: '医疗器械经营备案/许可、仓库条件' },
    { code: 'DM006', name: '医疗器械使用监管', desc: '使用单位采购验收、储存养护、使用记录' },
    { code: 'DM007', name: '化妆品标签检查', desc: '化妆品标签标识、功效宣称、注册备案合规性' },
    { code: 'DM008', name: '不良反应监测', desc: '药品/医疗器械不良反应报告制度、报告记录' },
    { code: 'DM009', name: '中药材中药饮片', desc: '中药材采购渠道、中药饮片炮制、储存养护' },
    { code: 'DM010', name: '网络售药监管', desc: '互联网药品信息服务、网络销售资质' },
  ],

  // 4. 特种设备安全监察科
  specialEquipment: [
    { code: 'SE001', name: '特种设备使用登记', desc: '锅炉、压力容器、电梯、起重机械使用登记' },
    { code: 'SE002', name: '定期检验合格', desc: '特种设备检验报告、下次检验日期、检验标志' },
    { code: 'SE003', name: '安全附件校验', desc: '安全阀、压力表、爆破片校验记录' },
    { code: 'SE004', name: '作业人员资质', desc: '特种设备作业人员持证上岗、证书有效期' },
    { code: 'SE005', name: '应急预案演练', desc: '应急预案、演练记录、救援装备配置' },
    { code: 'SE006', name: '电梯维保记录', desc: '电梯维保合同、半月/季度维保记录、故障记录' },
    { code: 'SE007', name: '起重机械检查', desc: '起重机械限位装置、钢丝绳磨损、防脱钩装置' },
    { code: 'SE008', name: '气瓶充装检查', desc: '气瓶充装许可、充装前后检查、自有产权气瓶' },
    { code: 'SE009', name: '压力管道检验', desc: '压力管道使用登记、检验报告、在线监测' },
    { code: 'SE010', name: '场内机动车辆', desc: '叉车等机动车辆登记、检验、操作人员资质' },
  ],

  // 5. 产品质量安全监督管理科
  productQuality: [
    { code: 'PQ001', name: '工业产品许可证', desc: '工业产品生产许可证/CCC认证、标志使用' },
    { code: 'PQ002', name: '产品质量抽检', desc: '产品质量监督抽查、不合格后处理、复查' },
    { code: 'PQ003', name: '计量器具检定', desc: '强检计量器具检定证书、标志、周期' },
    { code: 'PQ004', name: '检验检测机构', desc: '检验检测机构资质认定(CMA)、能力验证' },
    { code: 'PQ005', name: '能效标识检查', desc: '能效/水效标识备案、标注合规、检测报告' },
    { code: 'PQ006', name: '缺陷产品召回', desc: '缺陷产品召回计划、实施记录、召回报告' },
    { code: 'PQ007', name: '纤维制品检查', desc: '纤维制品标识、成分含量、质量证明文件' },
    { code: 'PQ008', name: '机动车安检', desc: '机动车安检机构资质、检验流程、报告合规' },
    { code: 'PQ009', name: '农资产品监管', desc: '化肥、农膜等农资产品质量、标识' },
    { code: 'PQ010', name: '儿童用品检查', desc: '儿童玩具、文具等用品质量、CCC认证、警示标识' },
  ],

  // 6. 计量监督管理科
  measurement: [
    { code: 'MT001', name: '强检计量器具', desc: '用于贸易结算、安全防护、医疗卫生、环境监测的计量器具' },
    { code: 'MT002', name: '计量标准考核', desc: '社会公用计量标准、企事业单位最高计量标准' },
    { code: 'MT003', name: '定量包装商品', desc: '净含量标注、实际含量、计量检验' },
    { code: 'MT004', name: '眼镜制配计量', desc: '验光仪、焦度计、瞳距仪等强检计量器具' },
    { code: 'MT005', name: '加油站计量', desc: '燃油加油机计量检定、铅封管理、防作弊' },
    { code: 'MT006', name: '集贸市场计量', desc: '衡器检定、公平秤配置、短斤缺两检查' },
    { code: 'MT007', name: '能效计量检测', desc: '能效标识检测能力、实验室条件' },
    { code: 'MT008', name: '计量校准机构', desc: '计量校准机构备案、校准能力、质量管理体系' },
  ],

  // 7. 标准化管理科
  standardization: [
    { code: 'ST001', name: '企业标准备案', desc: '企业标准自我声明公开、备案、有效性' },
    { code: 'ST002', name: '标准实施监督', desc: '国家标准、行业标准、地方标准实施情况' },
    { code: 'ST003', name: '团体标准监管', desc: '团体标准制定程序、公开透明、技术要求' },
    { code: 'ST004', name: '标准化示范创建', desc: '标准化示范区、试点项目验收、推广' },
    { code: 'ST005', name: '采用国际标准', desc: '采标标志使用、采标产品认可' },
    { code: 'ST006', name: '标准文本检查', desc: '产品标注执行标准、标准有效性、标准符合性' },
    { code: 'ST007', name: '商品条码管理', desc: '商品条码注册、续展、使用合规' },
  ],

  // 8. 认证认可监督管理科
  certification: [
    { code: 'CF001', name: 'CCC认证产品', desc: '强制性产品认证、认证标志、证书有效性' },
    { code: 'CF002', name: '有机产品认证', desc: '有机产品认证、标志使用、销售证' },
    { code: 'CF003', name: '质量管理体系', desc: 'ISO9001等体系认证、证书有效性、监督审核' },
    { code: 'CF004', name: '实验室资质', desc: '检验检测机构资质认定(CMA)、认可(CNAS)' },
    { code: 'CF005', name: '认证机构监管', desc: '认证机构资质、认证活动合规、认证人员' },
    { code: 'CF006', name: '绿色产品认证', desc: '绿色产品认证、标志使用、证书状态' },
    { code: 'CF007', name: '无公害农产品', desc: '无公害农产品认证、产地认定、标志使用' },
  ],

  // 9. 知识产权保护科（商标专利）
  ipProtection: [
    { code: 'IP001', name: '商标使用规范', desc: '注册商标使用规范、未注册商标管理' },
    { code: 'IP002', name: '专利标识检查', desc: '专利标注合规性、假冒专利查处' },
    { code: 'IP003', name: '地理标志产品', desc: '地理标志专用标志使用、产品质量特色' },
    { code: 'IP004', name: '商标代理监管', desc: '商标代理机构备案、代理行为合规' },
    { code: 'IP005', name: '知识产权维权', desc: '知识产权纠纷调解、维权援助、举报处理' },
    { code: 'IP006', name: '恶意商标注册', desc: '恶意商标注册行为、囤积商标、傍名牌' },
    { code: 'IP007', name: '展会知识产权保护', desc: '展会知识产权投诉处理、快速维权' },
  ],

  // 10. 广告监督管理科
  adSupervision: [
    { code: 'AD001', name: '广告内容审查', desc: '广告发布内容真实性、虚假宣传、极限用语' },
    { code: 'AD002', name: '医疗广告监管', desc: '医疗广告审查证明、发布内容合规' },
    { code: 'AD003', name: '药品广告监管', desc: '药品广告审查、处方药广告发布限制' },
    { code: 'AD004', name: '食品广告监管', desc: '食品广告虚假宣传、保健功能宣称' },
    { code: 'AD005', name: '房地产广告', desc: '房地产广告真实性、面积表示、承诺内容' },
    { code: 'AD006', name: '金融广告监管', desc: '金融广告合规性、风险提示、收益率承诺' },
    { code: 'AD007', name: '互联网广告', desc: '弹窗广告、信息流广告、虚假宣传' },
    { code: 'AD008', name: '户外广告登记', desc: '户外广告登记、发布内容、设置位置' },
  ],

  // 11. 反不正当竞争与价格监督检查科
  antiUnfair: [
    { code: 'AU001', name: '虚假宣传行为', desc: '商品虚假宣传、引人误解的商业宣传' },
    { code: 'AU002', name: '商业贿赂行为', desc: '商业贿赂、回扣、手续费、利益输送' },
    { code: 'AU003', name: '侵犯商业秘密', desc: '商业秘密保护、侵权查处、竞业限制' },
    { code: 'AU004', name: '不正当有奖销售', desc: '有奖销售规则、欺骗性有奖销售、最高奖金额' },
    { code: 'AU005', name: '商业诋毁行为', desc: '捏造散布虚假事实、损害竞争对手商誉' },
    { code: 'AU006', name: '价格欺诈行为', desc: '虚构原价、虚假折扣、价格误导' },
    { code: 'AU007', name: '明码标价检查', desc: '明码标价执行、价格标示规范、投诉处理' },
    { code: 'AU008', name: '价格垄断协议', desc: '横向垄断、纵向垄断、行业协会组织垄断' },
    { code: 'AU009', name: '滥用市场支配', desc: '不公平高价、差别待遇、附加不合理条件' },
    { code: 'AU010', name: '公用事业价格', desc: '水电气暖等公用事业收费、政府定价执行' },
  ],

  // 12. 消费者权益保护科
  consumerProtection: [
    { code: 'CP001', name: '三包规定执行', desc: '商品三包责任、退换货规定、维修记录' },
    { code: 'CP002', name: '消费欺诈查处', desc: '欺诈消费者行为、惩罚性赔偿、调解处理' },
    { code: 'CP003', name: '格式条款检查', desc: '合同格式条款、霸王条款、不公平条款' },
    { code: 'CP004', name: '预付卡消费', desc: '预付卡备案、资金存管、消费者权益保护' },
    { code: 'CP005', name: '网络购物维权', desc: '七天无理由退货、平台责任、消费者信息保护' },
    { code: 'CP006', name: '投诉举报处理', desc: '12315投诉处理、举报核查、调解记录' },
    { code: 'CP007', name: '消费教育引导', desc: '消费警示发布、消费知识宣传' },
    { code: 'CP008', name: '老年消费保护', desc: '老年保健品推销、非法集资、消费陷阱' },
  ],

  // 13. 网络交易监督管理科
  onlineTrading: [
    { code: 'OT001', name: '平台经营者资质', desc: '电商平台经营者资质、信息公示、主体责任' },
    { code: 'OT002', name: '网络食品经营', desc: '网络食品经营者资质、信息公示、配送管理' },
    { code: 'OT003', name: '网络直播带货', desc: '直播带货主体资质、商品信息、虚假宣传' },
    { code: 'OT004', name: '网络促销监管', desc: '网络促销活动规则、价格行为、虚假促销' },
    { code: 'OT005', name: '网络评价管理', desc: '网络交易评价真实性、刷单炒信、好评返现' },
    { code: 'OT006', name: '个人信息保护', desc: '网络经营者收集使用个人信息合规性' },
    { code: 'OT007', name: '跨境电商监管', desc: '跨境电商经营者备案、商品追溯、质量安全' },
    { code: 'OT008', name: '平台算法检查', desc: '平台算法推荐、大数据杀熟、信息茧房' },
  ],

  // 14. 信用监督管理科
  creditSupervision: [
    { code: 'CR001', name: '年报公示检查', desc: '企业年报信息公示、真实性核查、及时性' },
    { code: 'CR002', name: '经营异常名录', desc: '经营异常名录列入、移出管理、公示信息' },
    { code: 'CR003', name: '严重违法失信', desc: '严重违法失信名单管理、联合惩戒措施' },
    { code: 'CR004', name: '双随机抽查', desc: '随机抽查事项清单、抽查计划、结果公示' },
    { code: 'CR005', name: '信息公示检查', desc: '行政许可、行政处罚信息公示、公示及时性' },
    { code: 'CR006', name: '信用修复管理', desc: '信用修复申请、修复条件、修复结果' },
    { code: 'CR007', name: '承诺制监管', desc: '告知承诺制、承诺履行、违诺惩戒' },
  ],

  // 15. 综合执法科（其他）
  comprehensive: [
    { code: 'CO001', name: '无证无照经营', desc: '无证无照经营查处、引导办照、联合执法' },
    { code: 'CO002', name: '假冒伪劣产品', desc: '假冒伪劣产品查处、源头追溯、窝点打击' },
    { code: 'CO003', name: '非法传销活动', desc: '传销行为识别、窝点查处、人员遣返' },
    { code: 'CO004', name: '黑作坊黑窝点', desc: '食品黑作坊、黑窝点排查、取缔关停' },
    { code: 'CO005', name: '农资打假', desc: '农资市场检查、假冒伪劣农资、坑农害农' },
    { code: 'CO006', name: '校园周边整治', desc: '校园周边食品安全、文化市场、治安环境' },
    { code: 'CO007', name: '节令食品检查', desc: '重大节日食品安全专项检查、应急保障' },
    { code: 'CO008', name: '重大活动保障', desc: '重大活动食品安全保障、驻点监管、应急' },
  ],
}

// 检查类型映射表（用于OCR识别结果匹配）
export const checkTypeMap = {
  '食品生产': ['FP001', 'FP002', 'FP003', 'FP004', 'FP005', 'FP006', 'FP007', 'FP008', 'FP009', 'FP010', 'FP011', 'FP012'],
  '食品流通': ['FC001', 'FC002', 'FC003', 'FC004', 'FC005', 'FC006', 'FC007', 'FC008', 'FC009', 'FC010', 'FC011'],
  '药品': ['DM001', 'DM002', 'DM003', 'DM004', 'DM005', 'DM006', 'DM007', 'DM008', 'DM009', 'DM010'],
  '特种': ['SE001', 'SE002', 'SE003', 'SE004', 'SE005', 'SE006', 'SE007', 'SE008', 'SE009', 'SE010'],
  '质量': ['PQ001', 'PQ002', 'PQ003', 'PQ004', 'PQ005', 'PQ006', 'PQ007', 'PQ008', 'PQ009', 'PQ010'],
  '计量': ['MT001', 'MT002', 'MT003', 'MT004', 'MT005', 'MT006', 'MT007', 'MT008'],
  '标准': ['ST001', 'ST002', 'ST003', 'ST004', 'ST005', 'ST006', 'ST007'],
  '认证': ['CF001', 'CF002', 'CF003', 'CF004', 'CF005', 'CF006', 'CF007'],
  '商标': ['IP001', 'IP002', 'IP003', 'IP004', 'IP005', 'IP006', 'IP007'],
  '广告': ['AD001', 'AD002', 'AD003', 'AD004', 'AD005', 'AD006', 'AD007', 'AD008'],
  '不正当竞争': ['AU001', 'AU002', 'AU003', 'AU004', 'AU005', 'AU006', 'AU007', 'AU008', 'AU009', 'AU010'],
  '消费者': ['CP001', 'CP002', 'CP003', 'CP004', 'CP005', 'CP006', 'CP007', 'CP008'],
  '网络': ['OT001', 'OT002', 'OT003', 'OT004', 'OT005', 'OT006', 'OT007', 'OT008'],
  '信用': ['CR001', 'CR002', 'CR003', 'CR004', 'CR005', 'CR006', 'CR007'],
  '综合': ['CO001', 'CO002', 'CO003', 'CO004', 'CO005', 'CO006', 'CO007', 'CO008'],
}

// 科室中文名称映射
export const deptLabels = {
  foodProduction: '食品生产安全',
  foodCirculation: '食品流通餐饮',
  drugMedical: '药品医疗器械',
  specialEquipment: '特种设备安全',
  productQuality: '产品质量安全',
  measurement: '计量监督管理',
  standardization: '标准化管理',
  certification: '认证认可管理',
  ipProtection: '知识产权保护',
  adSupervision: '广告监督管理',
  antiUnfair: '反不正当竞争',
  consumerProtection: '消费者权益保护',
  onlineTrading: '网络交易监管',
  creditSupervision: '信用监督管理',
  comprehensive: '综合执法',
}

// 获取所有检查类型的扁平列表
export function getAllCheckTypes() {
  const list = []
  Object.values(deptCheckTypes).forEach(arr => list.push(...arr))
  return list
}

// 根据code查找检查类型
export function findCheckTypeByCode(code) {
  return getAllCheckTypes().find(t => t.code === code)
}

// 根据OCR识别关键词匹配检查类型
export function matchCheckTypeByOCR(text) {
  if (!text) return null
  for (const [keyword, codes] of Object.entries(checkTypeMap)) {
    if (text.includes(keyword)) {
      return codes.map(code => findCheckTypeByCode(code)).filter(Boolean)
    }
  }
  return null
}
