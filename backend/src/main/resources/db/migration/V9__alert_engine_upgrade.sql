-- V9: 预警引擎表结构升级 + 测试数据
-- 新增字段：source, assigned_user_id, assigned_user_name, escalate_at, escalate_count

-- 1. 修改alert表结构
ALTER TABLE alert 
  MODIFY COLUMN alert_type VARCHAR(30) NOT NULL COMMENT '预警类型',
  MODIFY COLUMN status VARCHAR(20) DEFAULT 'PENDING' COMMENT '预警状态',
  ADD COLUMN source VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '预警来源: SCHEDULED/EVENT',
  ADD COLUMN assigned_user_id BIGINT COMMENT '派单处理人ID',
  ADD COLUMN assigned_user_name VARCHAR(50) COMMENT '派单处理人姓名',
  ADD COLUMN escalate_at DATETIME COMMENT '督办升级截止时间',
  ADD COLUMN escalate_count INT DEFAULT 0 COMMENT '督办升级次数';

-- 2. 清空旧的测试预警数据
DELETE FROM alert;

-- 3. 插入测试数据——模拟规则引擎自动生成的预警

-- 资质到期类（4条）
INSERT INTO alert (alert_type, alert_level, enterprise_id, enterprise_name, title, content, status, source, assigned_user_id, assigned_user_name, escalate_at, escalate_count, created_at, updated_at) VALUES
('LICENSE_EXPIRING', 'LOW', 1, '阿克苏市天山食品有限责任公司', '【60天到期】食品生产许可证 - 阿克苏市天山食品有限责任公司', '企业阿克苏市天山食品有限责任公司的食品生产许可证将于2026-08-27到期（剩余60天），请督促企业及时办理续期手续。', 'PENDING', 'SCHEDULED', 5, '执法员一', DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, NOW(), NOW()),
('LICENSE_EXPIRING', 'MEDIUM', 2, '库车市瑞丰商贸有限公司', '【30天到期】药品经营许可证 - 库车市瑞丰商贸有限公司', '企业库车市瑞丰商贸有限公司的药品经营许可证将于2026-07-28到期（剩余30天），请督促企业及时办理续期手续。', 'PENDING', 'SCHEDULED', 6, '执法员二', DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, NOW(), NOW()),
('LICENSE_EXPIRING', 'HIGH', 3, '阿瓦提县绿洲农业科技有限公司', '【7天到期】特种设备安装许可证 - 阿瓦提县绿洲农业科技有限公司', '企业阿瓦提县绿洲农业科技有限公司的特种设备安装许可证将于2026-07-05到期（剩余7天），请立即督促企业办理续期。', 'HANDLING', 'SCHEDULED', 7, '执法员三', DATE_ADD(NOW(), INTERVAL 12 HOUR), 0, NOW(), NOW()),
('LICENSE_EXPIRED', 'HIGH', 4, '温宿县诚信建材有限公司', '【已过期】安全生产许可证 - 温宿县诚信建材有限公司', '企业温宿县诚信建材有限公司的安全生产许可证已于2026-06-15过期，请立即督促企业办理续期或依法处置。', 'PENDING', 'SCHEDULED', 8, '执法员四', DATE_ADD(NOW(), INTERVAL 6 HOUR), 1, DATE_SUB(NOW(), INTERVAL 30 HOUR), NOW());

-- 监管流程类（4条）
INSERT INTO alert (alert_type, alert_level, enterprise_id, enterprise_name, title, content, status, source, assigned_user_id, assigned_user_name, escalate_at, escalate_count, created_at, updated_at) VALUES
('RECTIFICATION_OVERDUE', 'HIGH', 5, '沙雅县金穗粮油加工厂', '整改超期未反馈 - 沙雅县金穗粮油加工厂', '企业沙雅县金穗粮油加工厂的整改通知（编号ZG20260601001）已超期8天未反馈，要求限期2026-06-20，请立即督办。', 'PENDING', 'SCHEDULED', 5, '执法员一', DATE_ADD(NOW(), INTERVAL 4 HOUR), 1, DATE_SUB(NOW(), INTERVAL 28 HOUR), NOW()),
('INSPECTION_OVERDUE', 'MEDIUM', 6, '拜城县恒泰矿业有限公司', '检查任务超期 - 拜城县恒泰矿业有限公司', '企业拜城县恒泰矿业有限公司的现场检查任务已创建12天仍未完成，请督促执法人员尽快执行。', 'HANDLING', 'SCHEDULED', 6, '执法员二', DATE_ADD(NOW(), INTERVAL 18 HOUR), 0, NOW(), NOW()),
('REPORT_MISSING', 'MEDIUM', 7, '新和县盛达物流有限公司', '合规报告未提交 - 新和县盛达物流有限公司', '企业新和县盛达物流有限公司超过45天未提交合规报告，请督促企业按期上报。', 'PENDING', 'SCHEDULED', 7, '执法员三', DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, NOW(), NOW()),
('REGISTRATION_ANOMALY', 'LOW', 8, '乌什县百味餐饮管理有限公司', '注册备案异常 - 乌什县百味餐饮管理有限公司', '企业乌什县百味餐饮管理有限公司的注册申请被退回已超过30天未重新提交，可能存在经营异常。', 'PENDING', 'SCHEDULED', 8, '执法员四', DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, NOW(), NOW());

-- 信用风险类（2条）
INSERT INTO alert (alert_type, alert_level, enterprise_id, enterprise_name, title, content, status, source, assigned_user_id, assigned_user_name, escalate_at, escalate_count, created_at, updated_at) VALUES
('CREDIT_ANOMALY', 'HIGH', 9, '柯坪县惠民超市', '企业状态异常 - 柯坪县惠民超市', '企业柯坪县惠民超市当前状态为已注销，请关注是否存在异常经营行为。', 'PENDING', 'SCHEDULED', 5, '执法员一', DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, NOW(), NOW()),
('REPEAT_VIOLATION', 'HIGH', 1, '阿克苏市天山食品有限责任公司', '屡次违规预警 - 阿克苏市天山食品有限责任公司', '企业阿克苏市天山食品有限责任公司在12个月内累计4次违规/整改记录，属于高风险企业，建议加大监管力度。', 'PENDING', 'EVENT', 5, '执法员一', DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, NOW(), NOW());

-- 4. 已处理的预警（2条，用于统计展示）
INSERT INTO alert (alert_type, alert_level, enterprise_id, enterprise_name, title, content, status, source, assigned_user_id, assigned_user_name, handle_remark, handled_by, handled_at, escalate_at, escalate_count, created_at, updated_at) VALUES
('LICENSE_EXPIRING', 'LOW', 2, '库车市瑞丰商贸有限公司', '【60天到期】营业执照 - 库车市瑞丰商贸有限公司', '企业库车市瑞丰商贸有限公司的营业执照将于2026-08-25到期（剩余60天），请督促企业及时办理续期手续。', 'HANDLED', 'SCHEDULED', 6, '执法员二', '已电话通知企业及时办理续期', 'admin', NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, DATE_SUB(NOW(), INTERVAL 48 HOUR), NOW()),
('RECTIFICATION_OVERDUE', 'HIGH', 3, '阿瓦提县绿洲农业科技有限公司', '整改超期未反馈 - 阿瓦提县绿洲农业科技有限公司', '企业阿瓦提县绿洲农业科技有限公司的整改通知已超期5天未反馈，要求限期2026-06-15，请立即督办。', 'HANDLED', 'SCHEDULED', 7, '执法员三', '已现场核查并督促企业完成整改', 'admin', NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR), 0, DATE_SUB(NOW(), INTERVAL 72 HOUR), NOW());

-- 5. 为合规报告表插入即将到期和已过期的测试数据
-- compliance_report 表中 expireDate 映射到 expiry_date 列

-- 插入即将到期的合规报告
INSERT INTO compliance_report (enterprise_id, title, report_type, file_url, file_name, expiry_date, status, uploaded_by, created_at, updated_at) VALUES
(1, '食品生产许可证', 'LICENSE', '/uploads/license1.pdf', '食品生产许可证.pdf', DATE_ADD(NOW(), INTERVAL 55 DAY), 'APPROVED', 'admin', NOW(), NOW()),
(2, '药品经营许可证', 'LICENSE', '/uploads/license2.pdf', '药品经营许可证.pdf', DATE_ADD(NOW(), INTERVAL 25 DAY), 'APPROVED', 'admin', NOW(), NOW()),
(3, '特种设备安装许可证', 'LICENSE', '/uploads/license3.pdf', '特种设备安装许可证.pdf', DATE_ADD(NOW(), INTERVAL 5 DAY), 'APPROVED', 'admin', NOW(), NOW()),
(4, '安全生产许可证', 'LICENSE', '/uploads/license4.pdf', '安全生产许可证.pdf', DATE_SUB(NOW(), INTERVAL 13 DAY), 'APPROVED', 'admin', NOW(), NOW());

-- 6. 插入一条已超期的整改通知（用于整改超期规则测试）
INSERT INTO rectification_notice (inspection_record_id, enterprise_id, inspector_id, notice_no, issues, requirements, deadline, status, created_at, updated_at) VALUES
(1, 5, 5, 'ZG20260601001', '生产车间卫生不达标', '限期整改并提交书面反馈', DATE_SUB(NOW(), INTERVAL 8 DAY), 'ISSUED', DATE_SUB(NOW(), INTERVAL 20 DAY), NOW());
