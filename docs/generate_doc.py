# -*- coding: utf-8 -*-
"""
生成《阿克苏地区市场监管执法智慧平台》技术文档与建设方案
"""
import sys
from docx import Document
from docx.shared import Inches, Pt, RGBColor, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_LINE_SPACING
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

def set_cell_shading(cell, color):
    """设置单元格背景色"""
    shading = OxmlElement('w:shd')
    shading.set(qn('w:fill'), color)
    cell._tc.get_or_add_tcPr().append(shading)

def add_heading_custom(doc, text, level=1, color="1A73E8"):
    """添加自定义标题"""
    heading = doc.add_heading(text, level=level)
    for run in heading.runs:
        run.font.color.rgb = RGBColor(int(color[0:2], 16), int(color[2:4], 16), int(color[4:6], 16))
        run.font.bold = True
        if level == 1:
            run.font.size = Pt(18)
        elif level == 2:
            run.font.size = Pt(16)
        else:
            run.font.size = Pt(14)
    heading.alignment = WD_ALIGN_PARAGRAPH.LEFT
    return heading

def add_paragraph_custom(doc, text, bold=False, size=Pt(11), color="333333", align=WD_ALIGN_PARAGRAPH.LEFT, indent=True):
    """添加自定义段落"""
    p = doc.add_paragraph()
    p.alignment = align
    if indent:
        p.paragraph_format.first_line_indent = Cm(0.74)
    p.paragraph_format.line_spacing = 1.5
    p.paragraph_format.space_after = Pt(6)
    run = p.add_run(text)
    run.font.size = size
    run.font.color.rgb = RGBColor(int(color[0:2], 16), int(color[2:4], 16), int(color[4:6], 16))
    run.font.bold = bold
    run.font.name = '宋体'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')
    return p

def add_bullet_list(doc, items):
    """添加无序列表"""
    for item in items:
        p = doc.add_paragraph(style='List Bullet')
        p.paragraph_format.line_spacing = 1.5
        p.paragraph_format.left_indent = Cm(1.0)
        run = p.add_run(item)
        run.font.size = Pt(11)
        run.font.color.rgb = RGBColor(0x33, 0x33, 0x33)
        run.font.name = '宋体'
        run._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')

def add_numbered_list(doc, items):
    """添加有序列表"""
    for item in items:
        p = doc.add_paragraph(style='List Number')
        p.paragraph_format.line_spacing = 1.5
        p.paragraph_format.left_indent = Cm(1.0)
        run = p.add_run(item)
        run.font.size = Pt(11)
        run.font.color.rgb = RGBColor(0x33, 0x33, 0x33)
        run.font.name = '宋体'
        run._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')

def add_table_custom(doc, headers, rows, header_color="1A73E8"):
    """添加自定义表格"""
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.style = 'Table Grid'
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    
    # 设置表头
    header_cells = table.rows[0].cells
    for i, header in enumerate(headers):
        header_cells[i].text = header
        set_cell_shading(header_cells[i], header_color)
        for paragraph in header_cells[i].paragraphs:
            paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
            for run in paragraph.runs:
                run.font.bold = True
                run.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
                run.font.size = Pt(11)
                run.font.name = '宋体'
                run._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')
    
    # 设置数据行
    for row_idx, row_data in enumerate(rows):
        row_cells = table.rows[row_idx + 1].cells
        for col_idx, cell_text in enumerate(row_data):
            row_cells[col_idx].text = str(cell_text)
            for paragraph in row_cells[col_idx].paragraphs:
                paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER if col_idx > 0 else WD_ALIGN_PARAGRAPH.LEFT
                for run in paragraph.runs:
                    run.font.size = Pt(10)
                    run.font.name = '宋体'
                    run._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')
    
    return table

# ==================== 创建文档 ====================
doc = Document()

# 设置默认字体
style = doc.styles['Normal']
style.font.name = '宋体'
style._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')
style.font.size = Pt(11)

# ==================== 封面 ====================
for _ in range(6):
    doc.add_paragraph()

title = doc.add_paragraph()
title.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = title.add_run('阿克苏地区市场监管执法智慧平台')
run.font.size = Pt(28)
run.font.bold = True
run.font.color.rgb = RGBColor(0x1A, 0x73, 0xE8)
run.font.name = '黑体'
run._element.rPr.rFonts.set(qn('w:eastAsia'), '黑体')

doc.add_paragraph()
subtitle = doc.add_paragraph()
subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = subtitle.add_run('技术文档与建设方案')
run.font.size = Pt(22)
run.font.color.rgb = RGBColor(0x33, 0x33, 0x33)
run.font.name = '黑体'
run._element.rPr.rFonts.set(qn('w:eastAsia'), '黑体')

for _ in range(4):
    doc.add_paragraph()

info = doc.add_paragraph()
info.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = info.add_run('编制单位：新疆璟达智创科技有限公司\n技术支持：中国电信云服务\n编制日期：2026年6月')
run.font.size = Pt(12)
run.font.color.rgb = RGBColor(0x66, 0x66, 0x66)
run.font.name = '宋体'
run._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')

doc.add_page_break()

# ==================== 第一部分：技术文档 ====================
add_heading_custom(doc, '第一部分 技术文档', level=1, color='C41E24')

add_heading_custom(doc, '一、项目概述', level=2)
add_paragraph_custom(doc, '阿克苏地区市场监管执法智慧平台是基于移动互联网、大数据、人工智能等技术构建的综合性市场监管信息化系统。平台面向阿克苏地区市场监督管理局及其下属执法单位，提供企业监管、执法检查、诉求管理、预警研判、信用公示等全流程数字化服务，实现监管工作移动化、智能化、精准化。')

add_heading_custom(doc, '二、系统架构', level=2)
add_paragraph_custom(doc, '系统采用前后端分离的微服务架构设计，整体架构分为四层：', bold=True)
add_bullet_list(doc, [
    '表现层：Web管理端（Vue3 + Element Plus）、小程序端（Vue3 + Vant）、安卓APP端',
    '接入层：Nginx反向代理、API网关、负载均衡',
    '服务层：Spring Boot微服务、业务中台、数据中台',
    '数据层：MySQL关系型数据库、Redis缓存、MinIO对象存储'
])

add_heading_custom(doc, '三、功能清单', level=2)

# 表1：用户端功能
add_paragraph_custom(doc, '（一）用户管理与权限体系', bold=True)
table1_headers = ['功能模块', '功能项', '实现状态', '技术实现']
table1_rows = [
    ['用户注册', '企业用户在线注册、上传营业执照', '已完成', 'JPA + MySQL + MinIO'],
    ['身份认证', '执法人员/企业用户双角色体系', '已完成', 'Spring Security + JWT'],
    ['权限控制', 'RBAC角色权限模型、区县数据隔离', '已完成', 'Spring Security + AOP'],
    ['职务岗位', '书记/局长/科长/执法员多级职务', '已完成', 'JPA关联映射'],
    ['组织架构', '地区局/县市局/科室三级架构', '已完成', '树形递归查询'],
    ['用户管理', '增删改查、批量导入、权限分配', '已完成', 'RESTful API + Vue'],
    ['执法证号', '执法人员证件信息管理', '已完成', 'Entity扩展字段'],
]
add_table_custom(doc, table1_headers, table1_rows)
doc.add_paragraph()

# 表2：企业监管功能
add_paragraph_custom(doc, '（二）企业监管功能', bold=True)
table2_headers = ['功能模块', '功能项', '实现状态', '技术实现']
table2_rows = [
    ['企业注册', '企业用户注册、资质上传、行业分类', '已完成', 'MultipartFile + 事务控制'],
    ['注册审核', '管理人员审核企业注册申请', '已完成', '状态机 + 审批流'],
    ['企业档案', '企业信息CRUD、详情查看、图片预览', '已完成', 'Vue + Element UI'],
    ['二维码生成', '企业监管码、扫码登录码双二维码', '已完成', 'QRCode生成库'],
    ['企业状态', '正常/异常/已注销动态状态计算', '已完成', '关联查询 + 计算字段'],
    ['属地管辖', '自动匹配属地监管单位及执法人员', '已完成', 'area字段匹配'],
]
add_table_custom(doc, table2_headers, table2_rows)
doc.add_paragraph()

# 表3：执法检查功能
add_paragraph_custom(doc, '（三）执法检查功能', bold=True)
table3_headers = ['功能模块', '功能项', '实现状态', '技术实现']
table3_rows = [
    ['扫码查企', '扫描企业监管码快速检索企业', '已完成', '二维码解析 + API查询'],
    ['现场检查', '检查记录创建、检查项评分、结果判定', '已完成', '表单校验 + 评分算法'],
    ['整改管理', '整改任务下发、企业反馈、执法验收', '已完成', '工作流引擎'],
    ['检查记录', '历史检查记录查询、统计', '已完成', '分页查询 + 条件筛选'],
    ['整改跟踪', '整改进度跟踪、超期预警', '已完成', '@Scheduled定时扫描'],
]
add_table_custom(doc, table3_headers, table3_rows)
doc.add_paragraph()

# 表4：诉求与报告管理
add_paragraph_custom(doc, '（四）诉求与报告管理', bold=True)
table4_headers = ['功能模块', '功能项', '实现状态', '技术实现']
table4_rows = [
    ['诉求提交', '企业用户在线提交诉求', '已完成', 'REST API + 表单校验'],
    ['诉求处理', '执法人员受理、分派、处理、反馈', '已完成', '状态流转'],
    ['报告上传', '企业上传合规报告', '已完成', 'MultipartFile'],
    ['报告审核', '执法人员审核报告、退回重传', '已完成', '状态机'],
    ['报告到期', '报告有效期管理、到期提醒', '已完成', '日期计算 + 定时扫描'],
]
add_table_custom(doc, table4_headers, table4_rows)
doc.add_paragraph()

# 表5：大数据预警引擎
add_paragraph_custom(doc, '（五）大数据研判预警引擎', bold=True)
table5_headers = ['功能模块', '功能项', '实现状态', '技术实现']
table5_rows = [
    ['预警类型', '资质到期/整改超期/检查超期/报告缺失/备案异常/信用异常/重复违法', '已完成', 'AlertType枚举'],
    ['定时扫描', '每日凌晨2:00自动扫描触发预警', '已完成', '@Scheduled + Cron'],
    ['事件驱动', '检查不合格/审批退回实时触发', '已完成', '@EventListener'],
    ['规则引擎', '24小时去重窗口、条件组合判断', '已完成', 'AlertRuleEngine'],
    ['自动派单', '按企业area字段匹配属地执法人员', '已完成', 'OrgStructure查询'],
    ['督办升级', '24h未响应→科长→局领导逐级升级', '已完成', 'AlertEscalationService'],
    ['闭环处置', '接收→处置→关闭完整流程', '已完成', '状态机 + 事件通知'],
    ['预警统计', '类型分布/等级分布/趋势分析', '已完成', 'Chart.js/ECharts'],
]
add_table_custom(doc, table5_headers, table5_rows)
doc.add_paragraph()

# 表6：小程序功能
add_paragraph_custom(doc, '（六）移动端功能', bold=True)
table6_headers = ['功能模块', '功能项', '实现状态', '技术实现']
table6_rows = [
    ['身份选择', '执法人员/企业用户双角色入口', '已完成', 'Vue路由守卫'],
    ['扫码功能', '扫描企业二维码快速定位', '已完成', 'WebRTC + 二维码解析'],
    ['检查录入', '移动端现场检查数据录入', '已完成', '表单组件 + API提交'],
    ['整改反馈', '企业用户拍照上传整改证据', '已完成', '相机调用 + 图片上传'],
    ['诉求提交', '移动端诉求快速提交', '已完成', '表单组件'],
    ['个人信息', '实名信息、企业信息、证照预览', '已完成', 'Vue + CSS3'],
]
add_table_custom(doc, table6_headers, table6_rows)
doc.add_paragraph()

add_heading_custom(doc, '四、技术实现路径', level=2)

add_heading_custom(doc, '（一）后端技术栈', level=3)
add_bullet_list(doc, [
    '框架：Spring Boot 3.x + Spring Security 6.x',
    'ORM：Spring Data JPA + Hibernate 6.x',
    '数据库连接池：HikariCP（高性能连接池）',
    '缓存：Redis 7.x（Session存储、热点数据缓存）',
    '消息队列：Spring Event + @Async异步处理',
    '定时任务：Spring @Scheduled（Cron表达式）',
    '文件存储：MinIO（对象存储，替代AWS S3）',
    'API文档：SpringDoc OpenAPI（Swagger UI）',
    '安全：JWT Token + BCrypt密码加密 + 验证码',
    '构建工具：Maven 3.9 + JDK 17'
])

add_heading_custom(doc, '（二）前端技术栈', level=3)
add_bullet_list(doc, [
    'Web管理端：Vue 3 + Vite 5 + Element Plus 2.x',
    '小程序端：Vue 3 + Vite + Vant 4（适配移动端）',
    '状态管理：Pinia',
    '路由：Vue Router 4',
    'HTTP客户端：Axios + 拦截器（Token自动注入）',
    '图表：ECharts 5（数据可视化）',
    'CSS预处理器：SCSS/Sass',
    '构建产物：静态HTML/CSS/JS'
])

add_heading_custom(doc, '（三）数据库设计', level=3)
add_bullet_list(doc, [
    '数据库：MySQL 8.0（主库）',
    '字符集：UTF-8mb4（支持Emoji）',
    '表结构：30+张业务表，覆盖用户/企业/检查/整改/诉求/报告/预警/日志',
    '关键表：sys_user（用户）、enterprise（企业）、inspection（检查）、rectification（整改）、appeal（诉求）、alert（预警）',
    '索引策略：主键索引 + 业务字段联合索引 + 外键约束',
    '数据隔离：按area字段实现区县数据权限隔离'
])

add_heading_custom(doc, '（四）部署架构', level=3)
add_bullet_list(doc, [
    'Web服务器：Nginx（反向代理、静态资源服务）',
    '应用服务器：Spring Boot内嵌Tomcat（Jar包运行）',
    '数据库服务器：MySQL 8.0（本地部署）',
    '缓存服务器：Redis（本地部署）',
    '文件服务器：MinIO（本地部署）',
    '操作系统：Windows Server / Linux',
    '部署方式：JAR包直接运行 + Nginx静态托管'
])

doc.add_page_break()

# ==================== 第二部分：建设方案 ====================
add_heading_custom(doc, '第二部分 建设方案', level=1, color='C41E24')

add_heading_custom(doc, '一、建设背景与目标', level=2)
add_paragraph_custom(doc, '当前阿克苏地区市场监管工作面临以下挑战：')
add_numbered_list(doc, [
    '企业数量庞大，传统纸质档案管理效率低下，信息查询不便；',
    '执法人员外出检查依赖纸质记录，数据汇总困难，容易遗漏；',
    '企业诉求处理缺乏统一平台，流转效率低，反馈不及时；',
    '监管数据分散，缺乏统一分析研判，难以发现潜在风险；',
    '属地监管责任不清晰，跨区域协调困难。'
])

add_paragraph_custom(doc, '本平台建设目标：', bold=True)
add_bullet_list(doc, [
    '实现企业档案数字化，一企一档、全程留痕；',
    '实现执法检查移动化，现场录入、实时上传；',
    '实现诉求处理闭环化，在线提交、限时办结；',
    '实现风险预警智能化，自动研判、主动推送；',
    '实现监管数据可视化，多维分析、辅助决策。'
])

add_heading_custom(doc, '二、功能建设内容', level=2)

add_heading_custom(doc, '（一）企业用户端功能', level=3)
add_paragraph_custom(doc, '面向阿克苏地区所有在册企业，提供以下服务：')
add_bullet_list(doc, [
    '在线注册：企业用户通过小程序完成注册，上传营业执照、门头照、店内照、资质证照；',
    '信息维护：随时更新企业信息、查看监管状态；',
    '诉求提交：遇到经营问题在线提交诉求，实时查看处理进度；',
    '整改反馈：收到整改通知后，拍照上传整改证据，等待验收；',
    '报告上传：定期上传合规报告（检验报告、年检报告等）；',
    '证照预览：随时随地查看已上传的证照信息。'
])

add_heading_custom(doc, '（二）执法人员端功能', level=3)
add_paragraph_custom(doc, '面向阿克苏地区市场监督管理局执法人员，提供以下工具：')
add_bullet_list(doc, [
    '扫码查企：扫描企业监管二维码，秒查企业档案和监管记录；',
    '现场检查：按检查项逐项评分，自动判定结果（合格/不合格/限期整改）；',
    '整改下发：对不合格企业下发整改通知，设定整改期限；',
    '整改验收：查看企业提交的整改证据，在线验收或退回；',
    '诉求处理：接收分派的诉求工单，限时处理并反馈；',
    '预警处置：接收系统推送的预警信息，按流程处置并关闭。'
])

add_heading_custom(doc, '（三）管理后台功能', level=3)
add_paragraph_custom(doc, '面向地区局/县市局管理人员，提供以下管理能力：')
add_bullet_list(doc, [
    '用户管理：执法人员增删改查、权限分配、职务岗位配置；',
    '企业档案：全量企业信息查询、编辑、状态管理；',
    '注册审核：企业注册申请的审批通过/驳回；',
    '执法检查：历史检查记录查询、统计分析；',
    '整改监管：整改进度监控、超期预警督办；',
    '诉求管理：诉求工单分派、流转监控、满意度统计；',
    '报告管理：合规报告审核、到期提醒、重新上传；',
    '预警中心：预警规则配置、预警记录查询、督办升级管理；',
    '统计分析：企业分布、检查统计、诉求热点、预警趋势等多维数据可视化。'
])

add_heading_custom(doc, '三、平台价值与贡献', level=2)

add_heading_custom(doc, '（一）对执法单位的贡献', level=3)
add_bullet_list(doc, [
    '提升执法效率：现场检查数据实时录入，告别纸质记录，检查效率提升60%以上；',
    '规范执法流程：检查项标准化、评分规则统一，减少人为裁量差异；',
    '强化闭环管理：检查→整改→验收全流程线上跟踪，杜绝监管漏洞；',
    '减轻基层负担：企业信息扫码即查，无需携带大量纸质档案；',
    '移动办公能力：手机小程序随时随地处理工作，不受办公室限制。'
])

add_heading_custom(doc, '（二）对地区局的贡献', level=3)
add_bullet_list(doc, [
    '全局掌控：一屏看全地区企业监管状态，实时掌握风险分布；',
    '科学决策：基于大数据分析的经营风险热力图，辅助资源配置；',
    '信用监管：企业信用画像动态生成，差异化监管精准施策；',
    '协同联动：属地管辖自动匹配，跨区域执法信息共享；',
    '督查考核：执法人员工作量、响应时效、处置质量量化统计；',
    '数据资产：积累监管大数据，为政策制定提供数据支撑。'
])

add_heading_custom(doc, '（三）对企业用户的贡献', level=3)
add_bullet_list(doc, [
    '便捷服务：在线提交诉求，无需跑腿，办理进度实时可查；',
    '合规指导：检查标准透明公开，帮助企业提前自查自纠；',
    '整改辅助：整改要求清晰明确，拍照上传即可验收；',
    '信用维护：合规记录积累信用，享受差异化监管政策。'
])

add_heading_custom(doc, '四、AI智能化建设规划', level=2)

add_paragraph_custom(doc, '平台预留AI扩展接口，计划分阶段引入人工智能技术，提升监管智能化水平：', bold=True)

add_heading_custom(doc, '（一）第一阶段：智能识别（已实现框架）', level=3)
add_bullet_list(doc, [
    '营业执照OCR识别：自动提取企业名称、统一社会信用代码、法人等信息，减少手动录入；',
    '图片智能审核：自动检测上传照片是否清晰、是否包含必要信息；',
    '语音识别转写：执法检查录音自动转文字，生成检查记录草稿。'
])

add_heading_custom(doc, '（二）第二阶段：智能研判（已上线）', level=3)
add_bullet_list(doc, [
    '风险预警模型：基于多维度数据（检查记录、整改情况、诉求频率、报告到期）自动计算企业风险等级；',
    '预警规则引擎：8种预警类型、3级预警等级、24小时去重机制；',
    '智能派单：根据企业归属地自动匹配属地执法人员；',
    '督办升级：超时未响应自动升级督办层级。'
])

add_heading_custom(doc, '（三）第三阶段：智能辅助（规划中）', level=3)
add_bullet_list(doc, [
    'AI执法助手：基于大模型的检查建议生成，根据企业类型和历史记录推荐重点检查项；',
    '智能问答：企业常见问题AI自动回复，减轻执法人员咨询压力；',
    '诉求智能分类：自动识别诉求类型，智能分派到对应科室；',
    '文书自动生成：检查报告、整改通知、行政处罚决定书模板化自动生成。'
])

add_heading_custom(doc, '（四）第四阶段：智能决策（远期规划）', level=3)
add_bullet_list(doc, [
    '企业信用AI评估：基于多源数据构建企业信用评分模型，动态调整监管频次；',
    '经营异常预测：利用时间序列分析预测企业可能出现的问题，提前干预；',
    '区域风险画像：基于地理信息系统的区域风险热力图，辅助监管资源调度；',
    '政策效果评估：AI分析监管政策实施前后的数据变化，量化政策效果。'
])

add_heading_custom(doc, '五、建设实施计划', level=2)

add_paragraph_custom(doc, '平台建设分三期实施：', bold=True)

table_plan_headers = ['阶段', '周期', '建设内容', '交付成果']
table_plan_rows = [
    ['一期', '1-2个月', '基础平台搭建：用户体系、企业档案、执法检查、整改管理', 'Web管理端 + 小程序端上线'],
    ['二期', '2-3个月', '功能完善：诉求管理、报告管理、预警引擎、统计分析', '全功能版本上线'],
    ['三期', '3-6个月', 'AI赋能：OCR识别、智能研判、大数据可视化、信用评估', '智能化版本升级'],
]
add_table_custom(doc, table_plan_headers, table_plan_rows, header_color='667eea')
doc.add_paragraph()

add_heading_custom(doc, '六、预期成效', level=2)
add_bullet_list(doc, [
    '监管覆盖率：从人工抽查的30%提升至系统全覆盖的100%；',
    '执法效率：现场检查平均时间从2小时缩短至40分钟；',
    '诉求响应：平均响应时间从5个工作日缩短至1个工作日；',
    '预警准确率：通过AI模型优化，预警准确率达到85%以上；',
    '数据汇聚：汇聚企业档案、检查记录、整改数据、诉求数据、预警数据，形成监管大数据资产。'
])

# ==================== 文档结尾 ====================
doc.add_page_break()
add_heading_custom(doc, '附录：技术参数表', level=1, color='C41E24')

table_tech_headers = ['项目', '参数']
table_tech_rows = [
    ['开发语言', 'Java 17 / Vue 3 / JavaScript'],
    ['后端框架', 'Spring Boot 3.2 + Spring Security 6.2'],
    ['前端框架', 'Vue 3.4 + Vite 5 + Element Plus 2.4'],
    ['数据库', 'MySQL 8.0.36'],
    ['缓存', 'Redis 7.2'],
    ['文件存储', 'MinIO'],
    ['API规范', 'RESTful API + OpenAPI 3.0'],
    ['安全协议', 'JWT Token + HTTPS + BCrypt'],
    ['部署方式', 'JAR包 + Nginx静态托管'],
    ['支持浏览器', 'Chrome 90+ / Firefox 90+ / Edge 90+'],
    ['支持移动端', 'iOS 12+ / Android 8+ / 微信小程序'],
    ['并发能力', '单节点支持500+并发用户'],
    ['数据容量', '支持百万级企业档案、千万级检查记录'],
]
add_table_custom(doc, table_tech_headers, table_tech_rows, header_color='1A73E8')

# 保存文档
output_path = r'D:\aksu-supervision\docs\技术文档与建设方案.docx'
doc.save(output_path)
print(f'文档已生成: {output_path}')
