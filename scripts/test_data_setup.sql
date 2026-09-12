-- ============================================
-- 阿克苏监管平台测试数据录入脚本
-- 排除: OCR/微信登录/消息推送
-- ============================================

USE aksu_supervision;

-- 1. 清理现有测试数据（保留管理员）
-- DELETE FROM enterprise_contact WHERE enterprise_id > 3;
-- DELETE FROM enterprise WHERE id > 3;
-- DELETE FROM sys_user WHERE id > 1;

-- ============================================
-- 2. 组织架构数据（地区→市→县→科室）
-- ============================================
INSERT INTO org_structure (id, name, code, level, parent_id, org_type, sort_order, status) VALUES
-- 地区级
(1, '阿克苏地区市场监督管理局', 'AKS_REGION_001', 1, NULL, 'REGION', 1, 1),
(2, '地区综合办公室', 'AKS_REGION_OFFICE', 4, 1, 'DEPT', 1, 1),
(3, '地区食品监管科', 'AKS_REGION_FOOD', 4, 1, 'DEPT', 2, 1),
(4, '地区特种设备科', 'AKS_REGION_EQUIP', 4, 1, 'DEPT', 3, 1),
-- 市级
(11, '阿克苏市市场监督管理局', 'AKS_CITY_001', 2, 1, 'CITY', 1, 1),
(12, '阿克苏市食品监管科', 'AKS_CITY_FOOD', 4, 11, 'DEPT', 1, 1),
(13, '阿克苏市药品监管科', 'AKS_CITY_DRUG', 4, 11, 'DEPT', 2, 1),
-- 县级
(21, '温宿县市场监督管理局', 'AKS_COUNTY_WS', 3, 11, 'COUNTY', 1, 1),
(22, '温宿县综合科', 'AKS_COUNTY_WS_OFFICE', 4, 21, 'DEPT', 1, 1),
(23, '温宿县食品监管科', 'AKS_COUNTY_WS_FOOD', 4, 21, 'DEPT', 2, 1),
(31, '库车县市场监督管理局', 'AKS_COUNTY_KC', 3, 11, 'COUNTY', 2, 1),
(32, '库车县综合科', 'AKS_COUNTY_KC_OFFICE', 4, 31, 'DEPT', 1, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================
-- 3. 岗位数据
-- ============================================
INSERT INTO sys_position (id, name, code, description, status, role_id) VALUES
(1, '地区局局长', 'REGION_DIRECTOR_POS', '阿克苏地区市场监督管理局局长', 1, 2),
(2, '地区局副局长', 'REGION_DEPUTY_POS', '阿克苏地区市场监督管理局副局长', 1, 3),
(3, '市局局��', 'CITY_DIRECTOR_POS', '阿克苏市市场监督管理局局长', 1, 4),
(4, '县局局长', 'COUNTY_DIRECTOR_POS', '县级市场监督管理局局长', 1, 5),
(5, '科室科长', 'DEPT_CHIEF_POS', '科室负责人', 1, 6),
(6, '科室副科长', 'DEPT_DEPUTY_POS', '科室副职', 1, 7),
(7, '地区科室科长', 'REGION_DEPT_CHIEF_POS', '地区级科室科长（跨区域权限）', 1, 8),
(8, '执法人员', 'ENFORCER_POS', '一线执法人员', 1, 1),
(9, '企业管理员', 'ENTERPRISE_POS', '企业用户', 1, 10)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================
-- 4. 职务数据
-- ============================================
INSERT INTO sys_job_title (id, name, code, level, description, status, role_id) VALUES
(1, '地区局局长', 'REGION_DIRECTOR_JT', 1, '阿克苏地区市场监督管理局局长', 1, 2),
(2, '地区局副局长', 'REGION_DEPUTY_JT', 2, '阿克苏地区市场监督管理局副局长', 1, 3),
(3, '市局局��', 'CITY_DIRECTOR_JT', 3, '阿克苏市市场监督管理局局长', 1, 4),
(4, '县局局长', 'COUNTY_DIRECTOR_JT', 4, '县级市场监督管理局局长', 1, 5),
(5, '科室科长', 'DEPT_CHIEF_JT', 5, '科室负责人', 1, 6),
(6, '科室副科长', 'DEPT_DEPUTY_JT', 6, '科室副职', 1, 7),
(7, '地区科室科长', 'REGION_DEPT_CHIEF_JT', 7, '地区级科室科长', 1, 8),
(8, '执法人员', 'ENFORCER_JT', 8, '一线执法人员', 1, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================
-- 5. 用户数据（不同角色/地区/科室）
-- 密码统一: BCrypt hash of 'test123'
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH  (test123)
-- ============================================
INSERT INTO sys_user (id, username, phone, password, real_name, role_id, status, user_type) VALUES
-- 管理员 (已有 id=1)
-- 地区领导
(2, 'region_director', '13900000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王地区', 2, 1, 'ADMIN'),
(3, 'region_deputy', '13900000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张副局', 3, 1, 'ADMIN'),
-- 市局领导
(4, 'city_director', '13900000003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李局长', 4, 1, 'ADMIN'),
-- 县局领导
(5, 'county_ws_director', '13900000004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵局长', 5, 1, 'ADMIN'),
(6, 'county_kc_director', '13900000005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '钱局长', 5, 1, 'ADMIN'),
-- 科室领导
(7, 'dept_chief_ws_food', '13900000006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙科长', 6, 1, 'ADMIN'),
(8, 'region_dept_food', '13900000007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周科长', 8, 1, 'ADMIN'),
-- 执法人员
(9, 'enforcer_ws_001', '13900000008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '吴执法', 1, 1, 'ADMIN'),
(10, 'enforcer_kc_001', '13900000009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '郑执法', 1, 1, 'ADMIN'),
-- 企业用户
(11, 'enterprise_001', '13800000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张经理', 10, 1, 'ENTERPRISE'),
(12, 'enterprise_002', '13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李经理', 10, 1, 'ENTERPRISE'),
(13, 'enterprise_003', '13800000003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王经理', 10, 1, 'ENTERPRISE')
ON DUPLICATE KEY UPDATE username = VALUES(username), role_id = VALUES(role_id);

-- ============================================
-- 6. 用户-组织-岗位关联（决定数据权限范围）
-- ============================================
INSERT INTO org_user_position (id, user_id, org_id, position_id, job_title_id, is_primary, status) VALUES
-- 地区领导关联地区局
(1, 2, 1, 1, 1, true, 1),
-- 地区副局长
(2, 3, 1, 2, 2, true, 1),
-- 市局领导
(3, 4, 11, 3, 3, true, 1),
-- 温宿县领导
(4, 5, 21, 4, 4, true, 1),
-- 库车县领导
(5, 6, 31, 4, 4, true, 1),
-- 温宿县食品科科长
(6, 7, 23, 5, 5, true, 1),
-- 地区食品科科长（跨区域权限）
(7, 8, 3, 7, 7, true, 1),
-- 温宿县执法人员
(8, 9, 21, 8, 8, true, 1),
-- 库车县执法人员
(9, 10, 31, 8, 8, true, 1)
ON DUPLICATE KEY UPDATE org_id = VALUES(org_id);

-- ============================================
-- 7. 用户数据权限范围
-- ============================================
INSERT INTO user_data_scope (id, user_id, scope_type, scope_value, scope_name) VALUES
-- 地区领导：整个地区
(1, 2, 'REGION', '1', '阿克苏地区'),
-- 地区副局长：整个地区
(2, 3, 'REGION', '1', '阿克苏地区'),
-- 市局领导：阿克苏市
(3, 4, 'CITY', '11', '阿克苏市'),
-- 温宿县领导：温宿县
(4, 5, 'COUNTY', '21', '温宿县'),
-- 库车县领导：库车县
(5, 6, 'COUNTY', '31', '库车县'),
-- 温宿县食品科：食品科
(6, 7, 'DEPT', '23', '温宿县食品监管科'),
-- 地区食品科：全地区食品科
(7, 8, 'REGION_DEPT', '3', '地区食品监管科'),
-- 执法人员：指定区域
(8, 9, 'COUNTY', '21', '温宿县'),
(9, 10, 'COUNTY', '31', '库车县')
ON DUPLICATE KEY UPDATE scope_type = VALUES(scope_type);

-- ============================================
-- 8. 企业数据（不同地区/行业/状态）
-- ============================================
INSERT INTO enterprise (id, credit_code, name, legal_person, phone, email, industry, area, address, business_scope, license_url, status, user_id, registration_status, qualification_urls, industry_type_code) VALUES
-- 温宿县食品企业
(4, '91652922MA77XXXXX1', '温宿县美味食品有限公司', '张经理', '13800000001', 'ws@example.com', '食品生产', '温宿县', '温宿县美食路1号', '食品生产、加工、销售', 'https://example.com/license1.jpg', 1, 11, 'APPROVED', 'https://example.com/q1.jpg,https://example.com/q2.jpg', 'C13'),
-- 温宿县餐饮企业
(5, '91652922MA77XXXXX2', '温宿县绿洲餐饮有限公司', '李经理', '13800000002', 'lc@example.com', '餐饮服务', '温宿县', '温宿县绿洲街2号', '餐饮服务', 'https://example.com/license2.jpg', 1, 12, 'APPROVED', 'https://example.com/q3.jpg', 'H62'),
-- 库车县食品企业
(6, '91652923MA77XXXXX3', '库车县天山食品有限公司', '王经理', '13800000003', 'ts@example.com', '食品生产', '库车县', '库车县天山大道3号', '食品生产、加工', 'https://example.com/license3.jpg', 1, 13, 'APPROVED', 'https://example.com/q4.jpg,https://example.com/q5.jpg', 'C13'),
-- 阿克苏市企业
(7, '91652901MA77XXXXX4', '阿克苏市光明乳品有限公司', '刘经理', '13800000004', 'gm@example.com', '乳制品制造', '阿克苏市', '阿克苏市光明路4号', '乳制品生产、销售', 'https://example.com/license4.jpg', 1, NULL, 'APPROVED', NULL, 'C14'),
-- 待审核企业
(8, '91652922MA77XXXXX5', '温宿县新鑫食品厂', '陈经理', '13800000005', 'xx@example.com', '食品加工', '温宿县', '温宿县新兴路5号', '食品加工', 'https://example.com/license5.jpg', 0, NULL, 'PENDING', NULL, 'C13'),
-- 审核被拒企业
(9, '91652923MA77XXXXX6', '库车县康健食品厂', '赵经理', '13800000006', 'kj@example.com', '保健食品', '库车县', '库车县健康路6号', '保健食品生产', NULL, 0, NULL, 'REJECTED', NULL, 'C14')
ON DUPLICATE KEY UPDATE name = VALUES(name), area = VALUES(area);

-- ============================================
-- 9. 企业联系人
-- ============================================
INSERT INTO enterprise_contact (id, enterprise_id, name, phone, position, is_primary, status) VALUES
(4, 4, '张三', '13800000001', '总经理', true, 1),
(5, 5, '李四', '13800000002', '经理', true, 1),
(6, 6, '王五', '13800000003', '厂长', true, 1),
(7, 7, '赵六', '13800000004', '经理', true, 1),
(8, 8, '钱七', '13800000005', '负责人', true, 1),
(9, 9, '孙八', '13800000006', '负责人', true, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================
-- 10. 注册审核记录
-- ============================================
INSERT INTO enterprise_registration (id, phone, password, credit_code, enterprise_name, legal_person, industry_type_code, industry_name, area, address, license_url, qualification_urls, registration_status, review_comment, reviewed_by, reviewed_at, create_time) VALUES
(1, '13800000007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '91652922MA77XXXXX7', '温宿县新农食品加工厂', '周经理', 'C13', '食品生产', '温宿县', '温宿县新农路10号', 'https://example.com/license7.jpg', 'https://example.com/q6.jpg', 'PENDING', NULL, NULL, NULL, NOW()),
(2, '13800000008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '91652901MA77XXXXX8', '阿克苏市振兴食品有限公司', '吴经理', 'C13', '食品生产', '阿克苏市', '阿克苏市振兴路11号', 'https://example.com/license8.jpg', 'https://example.com/q7.jpg,https://example.com/q8.jpg', 'PENDING', NULL, NULL, NULL, NOW()),
(3, '13800000009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '91652923MA77XXXXX9', '库车县宏达食品有限公司', '郑经理', 'C14', '乳制品制造', '库车县', '库车县宏达路12号', 'https://example.com/license9.jpg', 'https://example.com/q9.jpg', 'PENDING', NULL, NULL, NULL, NOW())
ON DUPLICATE KEY UPDATE registration_status = VALUES(registration_status);

-- ============================================
-- 11. 检查记录
-- ============================================
INSERT INTO inspection_record (id, enterprise_id, inspector_id, inspection_date, inspection_type, result, status, score, notes, create_time) VALUES
(1, 4, 9, DATE_SUB(NOW(), INTERVAL 3 DAY), 'ROUTINE', 'PASS', 1, 95, '食品安全检查合格', NOW()),
(2, 4, 9, DATE_SUB(NOW(), INTERVAL 1 DAY), 'SPECIAL', 'PASS', 1, 92, '专项检查通过', NOW()),
(3, 5, 9, DATE_SUB(NOW(), INTERVAL 2 DAY), 'ROUTINE', 'FAIL', 1, 65, '卫生条件不达标', NOW()),
(4, 6, 10, DATE_SUB(NOW(), INTERVAL 5 DAY), 'ROUTINE', 'PASS', 1, 88, '生产流程规范', NOW()),
(5, 7, 9, DATE_SUB(NOW(), INTERVAL 7 DAY), 'ROUTINE', 'PASS', 1, 90, '检查通过', NOW())
ON DUPLICATE KEY UPDATE enterprise_id = VALUES(enterprise_id);

-- ============================================
-- 12. 整改通知
-- ============================================
INSERT INTO rectification_notice (id, inspection_id, enterprise_id, issue_description, deadline, status, create_time) VALUES
(1, 3, 5, '厨房卫生条件不达标，需要整改', DATE_ADD(NOW(), INTERVAL 7 DAY), 'PENDING', NOW())
ON DUPLICATE KEY UPDATE enterprise_id = VALUES(enterprise_id);

-- ============================================
-- 13. 申诉记录
-- ============================================
INSERT INTO appeal (id, enterprise_id, appeal_type, content, status, create_time) VALUES
(1, 5, 'INSPECTION_RESULT', '对检查结果的异议说明', 'PENDING', NOW())
ON DUPLICATE KEY UPDATE enterprise_id = VALUES(enterprise_id);

COMMIT;

-- ============================================
-- 数据验证查询
-- ============================================
SELECT '=== 数据统计 ===' AS info;
SELECT CONCAT('组织架构: ', COUNT(*)) FROM org_structure;
SELECT CONCAT('用户: ', COUNT(*)) FROM sys_user;
SELECT CONCAT('企业: ', COUNT(*)) FROM enterprise;
SELECT CONCAT('注册审核: ', COUNT(*)) FROM enterprise_registration;
SELECT CONCAT('检查记录: ', COUNT(*)) FROM inspection_record;
SELECT CONCAT('整改通知: ', COUNT(*)) FROM rectification_notice;
SELECT CONCAT('申诉: ', COUNT(*)) FROM appeal;
SELECT CONCAT('用户组织关联: ', COUNT(*)) FROM org_user_position;
SELECT CONCAT('数据权限: ', COUNT(*)) FROM user_data_scope;
