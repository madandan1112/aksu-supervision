-- =============================================
-- 阿克苏地区市场监督管理局 数字化监管与服务平台
-- 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS aksu_supervision DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aksu_supervision;

-- =============================================
-- 1. 系统用户与权限
-- =============================================

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '1-启用 0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统角色表';

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource_type VARCHAR(20) NOT NULL COMMENT 'menu/button/api',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '系统权限表';

-- 角色-权限关联表
CREATE TABLE sys_role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
) COMMENT '角色权限关联表';

-- 系统用户表（监管端+管理后台）
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名/工号',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    department VARCHAR(100) COMMENT '所属科室',
    area_code VARCHAR(50) COMMENT '负责区域编码',
    industry_code VARCHAR(50) COMMENT '负责行业编码',
    user_type VARCHAR(20) NOT NULL COMMENT 'admin/inspector/leader',
    status TINYINT DEFAULT 1 COMMENT '1-启用 0-禁用',
    last_login_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统用户表';

-- 用户-角色关联表
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) COMMENT '用户角色关联表';

-- =============================================
-- 2. 企业信息
-- =============================================

-- 企业信息表
CREATE TABLE enterprise (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    credit_code VARCHAR(30) NOT NULL UNIQUE COMMENT '统一社会信用代码',
    enterprise_name VARCHAR(200) NOT NULL COMMENT '企业名称',
    legal_person VARCHAR(50) COMMENT '法人代表',
    address VARCHAR(500) COMMENT '经营地址',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    industry VARCHAR(50) COMMENT '主营行业',
    scale VARCHAR(20) COMMENT '规模(微型/小型/中型/大型)',
    employee_count INT COMMENT '员工数',
    business_status VARCHAR(20) DEFAULT '正常' COMMENT '正常/停业/注销',
    license_info JSON COMMENT '许可证信息(JSON数组)',
    wx_openid VARCHAR(100) COMMENT '微信OpenID',
    wx_unionid VARCHAR(100) COMMENT '微信UnionID',
    status TINYINT DEFAULT 1 COMMENT '1-正常 0-禁用',
    data_source VARCHAR(20) DEFAULT 'manual' COMMENT 'manual/sync 同步来源',
    last_sync_at DATETIME COMMENT '最后同步时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (enterprise_name),
    INDEX idx_industry (industry),
    INDEX idx_area (address)
) COMMENT '企业信息表';

-- 企业联系人表
CREATE TABLE enterprise_contact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL COMMENT '企业ID',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '手机号',
    is_primary TINYINT DEFAULT 0 COMMENT '1-主要联系人',
    verified TINYINT DEFAULT 0 COMMENT '1-已验证',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_enterprise (enterprise_id)
) COMMENT '企业联系人表';

-- =============================================
-- 3. 诉求管理
-- =============================================

-- 诉求表
CREATE TABLE appeal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL COMMENT '企业ID',
    appeal_no VARCHAR(30) NOT NULL UNIQUE COMMENT '诉求编号',
    appeal_type VARCHAR(50) NOT NULL COMMENT '政策咨询/许可办理/检查整改困惑/跨部门协调/服务建议',
    title VARCHAR(200) NOT NULL COMMENT '诉求标题',
    content TEXT NOT NULL COMMENT '诉求描述',
    related_fields JSON COMMENT '涉及领域["食品安全","特种设备"...]',
    attachments JSON COMMENT '附件列表[{name,url,type,size}]',
    status VARCHAR(20) NOT NULL DEFAULT '已接收' COMMENT '已接收/处理中/已办结/已驳回',
    assigned_department VARCHAR(100) COMMENT '分配科室',
    assigned_user_id BIGINT COMMENT '分配处理人',
    assign_remark TEXT COMMENT '分配意见',
    result_content TEXT COMMENT '处理结果',
    reject_reason TEXT COMMENT '驳回原因',
    satisfaction_score TINYINT COMMENT '满意度1-5',
    satisfaction_comment VARCHAR(500) COMMENT '评价内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_status (status),
    INDEX idx_type (appeal_type),
    INDEX idx_assigned (assigned_user_id)
) COMMENT '诉求表';

-- 诉求处理记录
CREATE TABLE appeal_process_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appeal_id BIGINT NOT NULL,
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    content TEXT COMMENT '操作内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_appeal (appeal_id)
) COMMENT '诉求处理记录';

-- =============================================
-- 4. 检查任务
-- =============================================

-- 检查任务表
CREATE TABLE inspection_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_no VARCHAR(30) NOT NULL UNIQUE COMMENT '任务编号',
    task_type VARCHAR(30) NOT NULL COMMENT '计划内/双随机/信访举报',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    inspection_focus TEXT COMMENT '检查重点',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '截止日期',
    status VARCHAR(20) NOT NULL DEFAULT '待分配' COMMENT '待分配/待认领/在办/已完成/逾期/已终止',
    -- 双随机配置
    random_enterprise_ratio DECIMAL(5,2) COMMENT '企业抽取比例',
    random_inspector_ratio DECIMAL(5,2) COMMENT '人员抽取比例',
    exclude_months INT COMMENT '排除近N月已检查企业',
    -- 延期信息在延期表中
    terminated_reason TEXT COMMENT '终止原因',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_type (task_type),
    INDEX idx_dates (start_date, end_date)
) COMMENT '检查任务表';

-- 任务-企业关联表
CREATE TABLE task_enterprise (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    enterprise_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT '待检查' COMMENT '待检查/检查中/已完成',
    UNIQUE KEY uk_task_enterprise (task_id, enterprise_id),
    INDEX idx_task (task_id),
    INDEX idx_enterprise (enterprise_id)
) COMMENT '任务-企业关联表';

-- 任务-检查人员关联表
CREATE TABLE task_inspector (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL COMMENT '检查人员ID',
    claimed TINYINT DEFAULT 0 COMMENT '是否已认领',
    UNIQUE KEY uk_task_inspector (task_id, user_id),
    INDEX idx_task (task_id)
) COMMENT '任务-检查人员关联表';

-- 延期申请表
CREATE TABLE task_extension (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    reason TEXT NOT NULL COMMENT '延期理由',
    extend_days INT NOT NULL COMMENT '申请延期天数',
    status VARCHAR(20) DEFAULT '待审批' COMMENT '待审批/已批准/已驳回',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id)
) COMMENT '延期申请表';

-- =============================================
-- 5. 现场检查记录
-- =============================================

-- 检查记录表
CREATE TABLE inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL COMMENT '任务ID',
    enterprise_id BIGINT NOT NULL COMMENT '企业ID',
    inspector_id BIGINT NOT NULL COMMENT '检查人员ID',
    inspector_name VARCHAR(50) COMMENT '检查人员姓名',
    check_type VARCHAR(50) COMMENT '检查类型',
    check_date DATETIME NOT NULL COMMENT '检查时间',
    -- 标准化检查结果
    form_data JSON COMMENT '检查表单数据(含检查项和结果)',
    -- 问题描述
    issues TEXT COMMENT '问题描述',
    -- 证据材料
    evidence_images JSON COMMENT '证据图片[{url,watermark_info}]',
    evidence_videos JSON COMMENT '证据视频[{url,watermark_info}]',
    -- 水印信息
    watermark_info JSON COMMENT '水印信息{time,location,enterprise,inspector}',
    -- 状态
    status VARCHAR(20) DEFAULT '草稿' COMMENT '草稿/已提交',
    is_offline TINYINT DEFAULT 0 COMMENT '是否离线录入',
    synced_at DATETIME COMMENT '同步时间',
    -- 自动生成的整改通知书ID
    rectification_notice_id BIGINT COMMENT '关联整改通知书ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_task (task_id),
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_inspector (inspector_id)
) COMMENT '现场检查记录表';

-- =============================================
-- 6. 整改管理
-- =============================================

-- 整改通知书表
CREATE TABLE rectification_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_no VARCHAR(30) NOT NULL UNIQUE COMMENT '通知书编号',
    inspection_record_id BIGINT NOT NULL COMMENT '关联检查记录ID',
    enterprise_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    -- 通知书内容
    check_date DATETIME COMMENT '检查时间',
    inspector_name VARCHAR(50) COMMENT '执法人员',
    issues TEXT NOT NULL COMMENT '问题描述',
    requirements TEXT NOT NULL COMMENT '整改要求',
    deadline DATE NOT NULL COMMENT '整改截止日期',
    -- 佐证材料
    evidence_images JSON COMMENT '问题佐证图片(带水印)',
    evidence_videos JSON COMMENT '问题佐证视频(带水印)',
    -- 状态
    status VARCHAR(20) DEFAULT '已下发' COMMENT '已下发/整改中/已提交/验收合格/验收不合格/需现场复核',
    -- 通知状态
    is_read TINYINT DEFAULT 0 COMMENT '企业是否已读',
    read_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_status (status),
    INDEX idx_task (task_id)
) COMMENT '整改通知书表';

-- 整改反馈表
CREATE TABLE rectification_feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_id BIGINT NOT NULL COMMENT '整改通知书ID',
    enterprise_id BIGINT NOT NULL,
    -- 整改内容
    measures TEXT NOT NULL COMMENT '整改措施描述',
    -- 整改后材料
    after_images JSON COMMENT '整改后图片',
    after_videos JSON COMMENT '整改后视频',
    third_party_reports JSON COMMENT '第三方补充报告',
    -- 状态
    status VARCHAR(20) DEFAULT '待提交' COMMENT '待提交/审核中/验收合格/验收不合格',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_notice (notice_id),
    INDEX idx_enterprise (enterprise_id)
) COMMENT '整改反馈表';

-- 验收记录表
CREATE TABLE acceptance_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_id BIGINT NOT NULL COMMENT '整改通知书ID',
    feedback_id BIGINT NOT NULL COMMENT '整改反馈ID',
    inspector_id BIGINT NOT NULL COMMENT '验收人员ID',
    -- 验收结论
    conclusion VARCHAR(20) NOT NULL COMMENT '合格/不合格/需现场复核',
    opinion TEXT COMMENT '验收意见',
    reject_reason TEXT COMMENT '不合格原因',
    -- AI比对结果
    ai_comparison_result JSON COMMENT 'AI视觉比对结果{score,differences,highlights}',
    -- 比对数据
    before_images JSON COMMENT '整改前图片',
    after_images JSON COMMENT '整改后图片',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notice (notice_id),
    INDEX idx_inspector (inspector_id)
) COMMENT '验收记录表';

-- =============================================
-- 7. 合规报告管理
-- =============================================

-- 合规报告表
CREATE TABLE compliance_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    report_no VARCHAR(30) NOT NULL UNIQUE COMMENT '报告编号',
    -- 分类
    industry VARCHAR(50) NOT NULL COMMENT '行业分类',
    report_type VARCHAR(50) NOT NULL COMMENT '出厂检验/第三方型式检验/特种设备定期检验/计量器具检定等',
    -- 文件
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT COMMENT '文件大小(字节)',
    file_format VARCHAR(10) COMMENT '文件格式',
    -- 关联信息
    product_batch_no VARCHAR(100) COMMENT '产品批次号',
    device_reg_code VARCHAR(100) COMMENT '设备注册代码',
    inspection_date DATE COMMENT '检验日期',
    expiry_date DATE COMMENT '有效期',
    -- 审核
    status VARCHAR(20) DEFAULT '已提交' COMMENT '已提交/已审查/待更新/已过期',
    reviewed_by BIGINT COMMENT '审核人ID',
    reviewed_at DATETIME COMMENT '审核时间',
    review_remark TEXT COMMENT '审核意见',
    -- 电子签章验证
    has_electronic_seal TINYINT DEFAULT 0 COMMENT '是否有电子签章',
    seal_verified TINYINT DEFAULT 0 COMMENT '签章是否验证通过',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_status (status),
    INDEX idx_expiry (expiry_date),
    INDEX idx_type (report_type)
) COMMENT '合规报告表';

-- =============================================
-- 8. 特种设备监测
-- =============================================

-- 设备台账表
CREATE TABLE equipment_ledger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    reg_code VARCHAR(100) COMMENT '注册代码',
    model VARCHAR(100) COMMENT '型号',
    use_location VARCHAR(255) COMMENT '使用地点',
    inspection_expiry DATE COMMENT '检验有效期',
    device_type VARCHAR(50) COMMENT '设备类型',
    status VARCHAR(20) DEFAULT '正常' COMMENT '正常/异常/停用',
    data_source VARCHAR(20) DEFAULT 'manual' COMMENT 'manual/sync',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_expiry (inspection_expiry),
    INDEX idx_status (status)
) COMMENT '特种设备台账表';

-- 设备上报记录表
CREATE TABLE equipment_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_id BIGINT NOT NULL COMMENT '设备台账ID',
    enterprise_id BIGINT NOT NULL,
    report_type VARCHAR(30) NOT NULL COMMENT '每日点检/周检/月检/问题上报',
    -- 标准化填报
    check_items JSON COMMENT '检查项数据[{item,result:normal/abnormal,value}]',
    -- 异常上报
    is_abnormal TINYINT DEFAULT 0 COMMENT '是否有异常',
    abnormal_items JSON COMMENT '异常项详情[{item,description,images}]',
    abnormal_images JSON COMMENT '异常图片',
    abnormal_videos JSON COMMENT '异常视频',
    -- 状态
    status VARCHAR(20) DEFAULT '已提交' COMMENT '已提交/已处理',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_equipment (equipment_id),
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_type (report_type)
) COMMENT '设备上报记录表';

-- =============================================
-- 9. 预警管理
-- =============================================

-- 预警表
CREATE TABLE alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    alert_type VARCHAR(30) NOT NULL COMMENT '设备异常/报告到期/高风险诉求/任务逾期',
    alert_level VARCHAR(10) NOT NULL COMMENT '高/中/低',
    source_type VARCHAR(30) COMMENT '来源类型',
    source_id BIGINT COMMENT '来源ID',
    enterprise_id BIGINT COMMENT '关联企业ID',
    title VARCHAR(200) NOT NULL COMMENT '预警标题',
    content TEXT COMMENT '预警内容',
    -- 处理
    status VARCHAR(20) DEFAULT '待处理' COMMENT '待处理/处理中/已处理/已关闭',
    handler_id BIGINT COMMENT '处理人ID',
    handle_type VARCHAR(30) COMMENT '立即派单检查/电话核实/标记关注',
    handle_remark TEXT COMMENT '处理备注',
    handled_at DATETIME,
    -- 派单关联
    created_task_id BIGINT COMMENT '派单创建的任务ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_level (alert_level),
    INDEX idx_enterprise (enterprise_id),
    INDEX idx_handler (handler_id)
) COMMENT '预警表';

-- =============================================
-- 10. 消息通知
-- =============================================

-- 站内消息表
CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_type VARCHAR(20) NOT NULL COMMENT 'enterprise/inspector/admin',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    msg_type VARCHAR(30) NOT NULL COMMENT '诉求通知/整改通知/报告提醒/系统通知/任务提醒/预警提醒',
    title VARCHAR(200) NOT NULL,
    content TEXT,
    related_type VARCHAR(30) COMMENT '关联业务类型',
    related_id BIGINT COMMENT '关联业务ID',
    is_read TINYINT DEFAULT 0,
    read_at DATETIME,
    -- 推送状态
    push_status VARCHAR(20) DEFAULT '待推送' COMMENT '待推送/已推送/推送失败',
    push_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_type, user_id),
    INDEX idx_type (msg_type),
    INDEX idx_read (is_read)
) COMMENT '站内消息表';

-- =============================================
-- 11. 检查表单模板
-- =============================================

-- 检查表单模板表
CREATE TABLE inspection_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    industry VARCHAR(50) NOT NULL COMMENT '适用行业',
    check_type VARCHAR(50) COMMENT '检查类型',
    -- 模板内容(JSON结构化)
    template_content JSON NOT NULL COMMENT '检查项模板[{group,items:[{name,standard,required}]}]',
    status TINYINT DEFAULT 1 COMMENT '1-启用 0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_industry (industry)
) COMMENT '检查表单模板表';

-- 点检模板表(设备)
CREATE TABLE equipment_check_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_type VARCHAR(50) NOT NULL COMMENT '设备类型',
    template_name VARCHAR(100) NOT NULL,
    check_items JSON NOT NULL COMMENT '点检项[{name,description,check_method}]',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '设备点检模板表';

-- =============================================
-- 12. 系统配置
-- =============================================

-- 系统参数配置表
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    config_type VARCHAR(20) DEFAULT 'string' COMMENT 'string/number/json',
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统参数配置表';

-- 操作日志表
CREATE TABLE sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(50),
    operation VARCHAR(50) COMMENT '操作类型',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_time (created_at)
) COMMENT '操作日志表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始角色
INSERT INTO sys_role (role_code, role_name, description) VALUES
('ADMIN', '系统管理员', '拥有所有权限'),
('LEADER', '科室负责人', '科室管理、任务审核'),
('INSPECTOR', '执法人员', '现场检查、整改验收');

-- 初始管理员 (密码: admin123, BCrypt加密)
INSERT INTO sys_user (username, password, real_name, user_type) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin');

-- 初始系统配置
INSERT INTO sys_config (config_key, config_value, config_type, description) VALUES
('system.name', '阿克苏地区市场监督管理局数字化监管与服务平台', 'string', '系统名称'),
('file.max_size', '209715200', 'number', '文件上传最大字节数(200MB)'),
('file.allowed_formats', 'JPG,PNG,MP4,PDF', 'string', '允许上传的文件格式'),
('report.warning_days', '30,15,7', 'string', '报告到期预警天数'),
('rectification.default_days', '30', 'number', '默认整改天数'),
('message.retain_months', '3', 'number', '消息保留月数'),
('log.retain_months', '12', 'number', '日志保留月数');

-- 初始检查模板 - 食品行业
INSERT INTO inspection_template (template_name, industry, check_type, template_content) VALUES
('食品生产日常检查', '食品', '日常检查', '[{"group":"生产环境","items":[{"name":"生产车间卫生状况","standard":"整洁无污染","required":true},{"name":"原料存储条件","standard":"分类存放、温度适宜","required":true}]},{"group":"生产流程","items":[{"name":"生产工艺合规性","standard":"按标准工艺执行","required":true},{"name":"产品检验记录","standard":"批次检验记录完整","required":true}]},{"group":"人员管理","items":[{"name":"健康证有效性","standard":"全员持有效健康证","required":true},{"name":"培训记录","standard":"定期培训有记录","required":false}]}]'),
('特种设备定期检查', '特种设备', '定期检查', '[{"group":"设备本体","items":[{"name":"设备外观完整性","standard":"无变形、无裂纹","required":true},{"name":"安全装置状态","standard":"功能正常、在校验期内","required":true}]},{"group":"运行状态","items":[{"name":"仪表读数","standard":"在正常范围内","required":true},{"name":"运行声音","standard":"无异常声响","required":true},{"name":"安全装置外观","standard":"无损坏、标识清晰","required":true}]},{"group":"档案资料","items":[{"name":"使用登记证","standard":"在有效期内","required":true},{"name":"检验报告","standard":"最新检验合格","required":true}]}]');

-- 初始点检模板
INSERT INTO equipment_check_template (device_type, template_name, check_items) VALUES
('电梯', '电梯日常点检模板', '[{"name":"仪表读数","description":"检查运行参数显示","check_method":"观察记录"},{"name":"运行声音","description":"运行是否有异响","check_method":"听觉判断"},{"name":"安全装置外观","description":"安全钳、限速器等外观","check_method":"目视检查"},{"name":"紧急报警装置","description":"报警按钮和对讲功能","check_method":"功能测试"}]'),
('锅炉', '锅炉日常点检模板', '[{"name":"压力表读数","description":"检查压力表显示值","check_method":"观察记录"},{"name":"水位计","description":"水位是否在正常范围","check_method":"观察比对"},{"name":"安全阀状态","description":"安全阀是否正常","check_method":"外观+手动测试"},{"name":"运行声音","description":"有无异常声响","check_method":"听觉判断"}]');
