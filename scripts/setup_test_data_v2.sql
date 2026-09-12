-- ============================================
-- 阿克苏监管平台测试数据录入脚本（基于实际表结构）
-- 排除: OCR/微信登录/消息推送
-- ============================================
USE aksu_supervision;

-- 1. 确保管理员存在
INSERT INTO sys_user (id, username, password, real_name, phone, user_type, role_id, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', '13800138000', 'ADMIN', 1, 1)
ON DUPLICATE KEY UPDATE username = VALUES(username), role_id = VALUES(role_id);

-- 2. 检查角色数据
SELECT CONCAT('角色数: ', COUNT(*)) FROM sys_role;

-- 3. 确保角色存在
INSERT INTO sys_role (id, role_code, role_name, description, status, data_scope, region_scope, dept_scope) VALUES
(1, 'ENFORCER', '执法人员', '一线执法人员', 1, 'SELF', 'SELF', 'SELF'),
(2, 'REGION_DIRECTOR', '地区领导', '阿克苏地区市场监督管理局局长', 1, 'ALL', 'ALL', 'ALL'),
(3, 'REGION_DEPUTY', '地区副局长', '阿克苏地区市场监督管理局副局长', 1, 'ALL', 'ALL', 'ALL'),
(4, 'CITY_DIRECTOR', '市局领导', '阿克苏市市场监督管理局局长', 1, 'ALL', 'CITY', 'ALL'),
(5, 'COUNTY_DIRECTOR', '县局领导', '县级市场监督管理局局长', 1, 'ALL', 'COUNTY', 'ALL'),
(6, 'DEPT_CHIEF', '科室科长', '科室负责人', 1, 'ALL', 'ALL', 'DEPT'),
(7, 'DEPT_DEPUTY_CHIEF', '科室副科长', '科室副职', 1, 'ALL', 'ALL', 'DEPT'),
(8, 'REGION_DEPT_CHIEF', '地区科室科长', '地区级科室科长（跨区域权限）', 1, 'ALL', 'ALL', 'DEPT'),
(9, 'ADMIN', '管理员', '系统管理员', 1, 'ALL', 'ALL', 'ALL'),
(10, 'ENTERPRISE_USER', '企业用户', '企业注册用户', 1, 'SELF', 'SELF', 'SELF'),
(11, 'CITY_DEPT_CHIEF', '市局科室科长', '市局科室科长', 1, 'ALL', 'CITY', 'DEPT')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), data_scope = VALUES(data_scope), region_scope = VALUES(region_scope), dept_scope = VALUES(dept_scope);

-- 4. 岗位
INSERT INTO sys_position (id, name, code, description, status, role_id) VALUES
(1, '地区局局长', 'REGION_DIRECTOR_POS', '阿克苏地区市场监督管理局局长', 1, 2),
(2, '地区局副局长', 'REGION_DEPUTY_POS', '阿克苏地区市场监督管理局副局长', 1, 3),
(3, '市局局长', 'CITY_DIRECTOR_POS', '阿克苏市市场监督管理局局长', 1, 4),
(4, '县局局长', 'COUNTY_DIRECTOR_POS', '县级市场监督管理局局长', 1, 5),
(5, '科室科长', 'DEPT_CHIEF_POS', '科室负责人', 1, 6),
(6, '科室副科长', 'DEPT_DEPUTY_POS', '科室副职', 1, 7),
(7, '地区科室科长', 'REGION_DEPT_CHIEF_POS', '地区级科室科长', 1, 8),
(8, '执法人员', 'ENFORCER_POS', '一线执法人员', 1, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), role_id = VALUES(role_id);

-- 5. 职务
INSERT INTO sys_job_title (id, name, code, level, description, status, role_id) VALUES
(1, '地区局局长', 'REGION_DIRECTOR_JT', 1, '阿克苏地区市场监督管理局局长', 1, 2),
(2, '地区局副局长', 'REGION_DEPUTY_JT', 2, '阿克苏地区市场监督管理局副局长', 1, 3),
(3, '市局局长', 'CITY_DIRECTOR_JT', 3, '阿克苏市市场监督管理局局长', 1, 4),
(4, '县局局长', 'COUNTY_DIRECTOR_JT', 4, '县级市场监督管理局局长', 1, 5),
(5, '科室科长', 'DEPT_CHIEF_JT', 5, '科室负责人', 1, 6),
(6, '科室副科长', 'DEPT_DEPUTY_JT', 6, '科室副职', 1, 7),
(7, '地区科室科长', 'REGION_DEPT_CHIEF_JT', 7, '地区级科室科长', 1, 8),
(8, '执法人员', 'ENFORCER_JT', 8, '一线执法人员', 1, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), role_id = VALUES(role_id);

-- 6. 组织架构
INSERT INTO org_structure (id, name, parent_id, level, sort_order, status, org_code, org_type) VALUES
(1, '阿克苏地区市场监督管理局', NULL, 1, 1, 1, 'AKS_REGION_001', 'REGION'),
(2, '地区综合办公室', 1, 4, 1, 1, 'AKS_REGION_OFFICE', 'DEPT'),
(3, '地区食品监管科', 1, 4, 2, 1, 'AKS_REGION_FOOD', 'DEPT'),
(4, '地区特种设备科', 1, 4, 3, 1, 'AKS_REGION_EQUIP', 'DEPT'),
(11, '阿克苏市市场监督管理局', 1, 2, 1, 1, 'AKS_CITY_001', 'CITY'),
(12, '阿克苏市食品监管科', 11, 4, 1, 1, 'AKS_CITY_FOOD', 'DEPT'),
(13, '阿克苏市药品监管科', 11, 4, 2, 1, 'AKS_CITY_DRUG', 'DEPT'),
(21, '温宿县市场监督管理局', 11, 3, 1, 1, 'AKS_COUNTY_WS', 'COUNTY'),
(22, '温宿县综合科', 21, 4, 1, 1, 'AKS_COUNTY_WS_OFFICE', 'DEPT'),
(23, '温宿县食品监管科', 21, 4, 2, 1, 'AKS_COUNTY_WS_FOOD', 'DEPT'),
(31, '库车县市场监督管理局', 11, 3, 2, 1, 'AKS_COUNTY_KC', 'COUNTY'),
(32, '库车县综合科', 31, 4, 1, 1, 'AKS_COUNTY_KC_OFFICE', 'DEPT'),
(33, '库车县食品监管科', 31, 4, 2, 1, 'AKS_COUNTY_KC_FOOD', 'DEPT')
ON DUPLICATE KEY UPDATE name = VALUES(name), level = VALUES(level), org_code = VALUES(org_code);

-- 7. 用户数据（密码统一为test123的BCrypt）
INSERT INTO sys_user (id, username, password, real_name, phone, user_type, role_id, status) VALUES
(2, 'region_director', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王地区', '13900000001', 'ADMIN', 2, 1),
(3, 'region_deputy', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张副局', '13900000002', 'ADMIN', 3, 1),
(4, 'city_director', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李局长', '13900000003', 'ADMIN', 4, 1),
(5, 'county_ws_director', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵局长', '13900000004', 'ADMIN', 5, 1),
(6, 'county_kc_director', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '钱局长', '13900000005', 'ADMIN', 5, 1),
(7, 'dept_chief_ws_food', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙科长', '13900000006', 'ADMIN', 6, 1),
(8, 'region_dept_food', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周科长', '13900000007', 'ADMIN', 8, 1),
(9, 'enforcer_ws_001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '吴执法', '13900000008', 'ADMIN', 1, 1),
(10, 'enforcer_kc_001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '郑执法', '13900000009', 'ADMIN', 1, 1),
(11, 'enterprise_001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张经理', '13800000001', 'ENTERPRISE', 10, 1),
(12, 'enterprise_002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李经理', '13800000002', 'ENTERPRISE', 10, 1),
(13, 'enterprise_003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王经理', '13800000003', 'ENTERPRISE', 10, 1)
ON DUPLICATE KEY UPDATE username = VALUES(username), role_id = VALUES(role_id), user_type = VALUES(user_type);

-- 8. 用户数据权限（基于实际表结构：user_data_scope）
-- user_data_scope: id, user_id, org_id, org_type, data_scope
DELETE FROM user_data_scope WHERE user_id IN (1,2,3,4,5,6,7,8,9,10);
INSERT INTO user_data_scope (user_id, org_id, org_type, data_scope) VALUES
(1, 1, 'REGION', 'ALL'),
(2, 1, 'REGION', 'ALL'),
(3, 1, 'REGION', 'ALL'),
(4, 11, 'CITY', 'ALL'),
(5, 21, 'COUNTY', 'ALL'),
(6, 31, 'COUNTY', 'ALL'),
(7, 23, 'DEPT', 'DEPT'),
(8, 3, 'DEPT', 'DEPT'),
(9, 21, 'COUNTY', 'DEPT'),
(10, 31, 'COUNTY', 'DEPT');

-- 9. 企业数据（基于实际表结构）
DELETE FROM enterprise WHERE id >= 4 AND id <= 15;
INSERT INTO enterprise (credit_code, enterprise_name, legal_person, address, phone, email, industry, industry_type_code, area, business_scope, license_url, status, user_id, registration_status, qualification_urls) VALUES
('91652922MA77XXXXX1', '温宿县美味食品有限公司', '张经理', '温宿县美食路1号', '13800000001', 'ws@example.com', '食品生产', 'C13', '温宿县', '食品生产、加工、销售', 'https://example.com/license1.jpg', 1, 11, 'APPROVED', '["https://example.com/q1.jpg","https://example.com/q2.jpg"]'),
('91652922MA77XXXXX2', '温宿县绿洲餐饮有限公司', '李经理', '温宿县绿洲街2号', '13800000002', 'lc@example.com', '餐饮服务', 'H62', '温宿县', '餐饮服务', 'https://example.com/license2.jpg', 1, 12, 'APPROVED', '["https://example.com/q3.jpg"]'),
('91652923MA77XXXXX3', '库车县天山食品有限公司', '王经理', '库车县天山大道3号', '13800000003', 'ts@example.com', '食品生产', 'C13', '库车县', '食品生产、加工', 'https://example.com/license3.jpg', 1, 13, 'APPROVED', '["https://example.com/q4.jpg","https://example.com/q5.jpg"]'),
('91652901MA77XXXXX4', '阿克苏市光明乳品有限公司', '刘经理', '阿克苏市光明路4号', '13800000004', 'gm@example.com', '乳制品制造', 'C14', '阿克苏市', '乳制品生产、销售', 'https://example.com/license4.jpg', 1, NULL, 'APPROVED', NULL),
('91652901MA77XXXXX5', '阿克苏市惠民食品厂', '马经理', '阿克苏市惠民路20号', '13800000010', 'hm@example.com', '食品生产', 'C13', '阿克苏市', '食品加工生产', 'https://example.com/license10.jpg', 1, NULL, 'APPROVED', '["https://example.com/q10.jpg"]'),
('91652922MA77XXXXX6', '温宿县兴旺食品厂', '田经理', '温宿县兴旺路21号', '13800000011', 'wx@example.com', '食品生产', 'C13', '温宿县', '食品加工生产', 'https://example.com/license11.jpg', 1, NULL, 'APPROVED', '["https://example.com/q11.jpg"]'),
('91652923MA77XXXXX7', '库车县瑞祥食品厂', '韩经理', '库车县瑞祥路22号', '13800000012', 'rx@example.com', '食品生产', 'C13', '库车县', '食品加工生产', 'https://example.com/license12.jpg', 1, NULL, 'APPROVED', '["https://example.com/q12.jpg"]');

-- 10. 待审核和已拒绝企业
INSERT INTO enterprise (credit_code, enterprise_name, legal_person, address, phone, industry, industry_type_code, area, business_scope, license_url, status, user_id, registration_status, qualification_urls) VALUES
('91652922MA77XXXXX8', '温宿县新鑫食品厂', '陈经理', '温宿县新兴路5号', '13800000005', '食品加工', 'C13', '温宿县', '食品加工', 'https://example.com/license5.jpg', 0, NULL, 'PENDING', NULL),
('91652923MA77XXXXX9', '库车县康健食品厂', '赵经理', '库车县健康路6号', '13800000006', '保健食品', 'C14', '库车县', '保健食品生产', NULL, 0, NULL, 'REJECTED', NULL);

-- 11. 注册审核记录（基于实际表结构：enterprise_registration）
-- enterprise_registration: id, user_id, step, credit_code, enterprise_name, legal_person, address, phone, industry, industry_type_code, area, license_url, qualification_urls, status, review_comment, reviewed_by, reviewed_at
DELETE FROM enterprise_registration WHERE id IN (1,2,3);
INSERT INTO enterprise_registration (user_id, step, credit_code, enterprise_name, legal_person, address, phone, industry, industry_type_code, area, license_url, qualification_urls, status) VALUES
(14, 4, '91652922MA77XXXXX10', '温宿县新农食品加工厂', '周经理', '温宿县新农路10号', '13800000007', '食品生产', 'C13', '温宿县', 'https://example.com/license7.jpg', '["https://example.com/q6.jpg"]', 'PENDING'),
(15, 4, '91652901MA77XXXXX11', '阿克苏市振兴食品有限公司', '吴经理', '阿克苏市振兴路11号', '13800000008', '食品生产', 'C13', '阿克苏市', 'https://example.com/license8.jpg', '["https://example.com/q7.jpg","https://example.com/q8.jpg"]', 'PENDING'),
(16, 4, '91652923MA77XXXXX12', '库车县宏达食品有限公司', '郑经理', '库车县宏达路12号', '13800000009', '乳制品制造', 'C14', '库车县', 'https://example.com/license9.jpg', '["https://example.com/q9.jpg"]', 'PENDING');

-- 12. 检查记录
DELETE FROM inspection_record WHERE id >= 1 AND id <= 5;
INSERT INTO inspection_record (enterprise_id, inspector_id, inspection_date, inspection_type, result, status, score, notes) VALUES
(4, 9, DATE_SUB(NOW(), INTERVAL 3 DAY), 'ROUTINE', 'PASS', 1, 95, '食品安全检查合格'),
(4, 9, DATE_SUB(NOW(), INTERVAL 1 DAY), 'SPECIAL', 'PASS', 1, 92, '专项检查通过'),
(5, 9, DATE_SUB(NOW(), INTERVAL 2 DAY), 'ROUTINE', 'FAIL', 1, 65, '卫生条件不达标'),
(6, 10, DATE_SUB(NOW(), INTERVAL 5 DAY), 'ROUTINE', 'PASS', 1, 88, '生产流程规范'),
(7, 9, DATE_SUB(NOW(), INTERVAL 7 DAY), 'ROUTINE', 'PASS', 1, 90, '检查通过');

-- 13. 整改通知
DELETE FROM rectification_notice WHERE id >= 1 AND id <= 5;
INSERT INTO rectification_notice (inspection_id, enterprise_id, issue_description, deadline, status) VALUES
(3, 5, '厨房卫生条件不达标，需要整改', DATE_ADD(NOW(), INTERVAL 7 DAY), 'PENDING');

-- 14. 申诉记录
DELETE FROM appeal WHERE id >= 1 AND id <= 3;
INSERT INTO appeal (enterprise_id, appeal_type, content, status) VALUES
(5, 'INSPECTION_RESULT', '对检查结果的异议说明', 'PENDING');

-- ============================================
-- 数据验证查询
-- ============================================
SELECT '=== 数据统计 ===' AS info;
SELECT CONCAT('总用户数: ', COUNT(*)) FROM sys_user;
SELECT CONCAT('角色数: ', COUNT(*)) FROM sys_role;
SELECT CONCAT('岗位数: ', COUNT(*)) FROM sys_position;
SELECT CONCAT('职务数: ', COUNT(*)) FROM sys_job_title;
SELECT CONCAT('组织架构数: ', COUNT(*)) FROM org_structure;
SELECT CONCAT('数据权限记录: ', COUNT(*)) FROM user_data_scope;
SELECT CONCAT('企业总数: ', COUNT(*)) FROM enterprise;
SELECT CONCAT('检查记录数: ', COUNT(*)) FROM inspection_record;
SELECT CONCAT('整改通知数: ', COUNT(*)) FROM rectification_notice;
SELECT CONCAT('申诉记录数: ', COUNT(*)) FROM appeal;
SELECT CONCAT('注册审核记录: ', COUNT(*)) FROM enterprise_registration;
SELECT CONCAT('角色权限明细: ', COUNT(*)) FROM role_permission_detail;

SELECT '=== 企业地区分布 ===' AS info;
SELECT area, COUNT(*) as cnt FROM enterprise GROUP BY area ORDER BY cnt DESC;

SELECT '=== 企业状态分布 ===' AS info;
SELECT registration_status, COUNT(*) FROM enterprise WHERE registration_status IS NOT NULL GROUP BY registration_status;

SELECT '=== 用户角色分布 ===' AS info;
SELECT r.role_name, COUNT(*) as cnt FROM sys_user u JOIN sys_role r ON u.role_id = r.id GROUP BY r.role_name ORDER BY cnt DESC;

SELECT '=== 数据权限范围分布 ===' AS info;
SELECT org_type, COUNT(*) FROM user_data_scope GROUP BY org_type ORDER BY COUNT(*) DESC;
