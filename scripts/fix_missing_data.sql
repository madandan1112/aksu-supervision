-- 补充缺失的测试数据（基于实际表结构）
USE aksu_supervision;

-- ============================================
-- 1. 用户组织岗位关联 (org_user_position)
-- 表结构: id, user_id, org_id, position_id, job_title_id, is_primary, status
-- ============================================
INSERT INTO org_user_position (user_id, org_id, position_id, job_title_id, is_primary, status) VALUES
-- 管理员
(1, 1, 1, 1, true, 1),
-- 地区领导
(2, 1, 1, 1, true, 1),
-- 地区副局长
(3, 1, 2, 2, true, 1),
-- 市局领导
(4, 11, 3, 3, true, 1),
-- 温宿县领导
(5, 21, 4, 4, true, 1),
-- 库车县领导
(6, 31, 4, 4, true, 1),
-- 温宿县食品科科长
(7, 23, 5, 5, true, 1),
-- 地区食品科科长
(8, 3, 7, 7, true, 1),
-- 温宿县执法人员
(9, 21, 8, 8, true, 1),
-- 库车县执法人员
(10, 31, 8, 8, true, 1)
ON DUPLICATE KEY UPDATE user_id = user_id;

-- ============================================
-- 2. 用户数据权限范围 (user_data_scope)
-- 表结构: id, user_id, scope_type, scope_value, scope_name
-- ============================================
INSERT INTO user_data_scope (user_id, scope_type, scope_value, scope_name) VALUES
-- 管理员：全部
(1, 'ALL', '*', '全部'),
-- 地区领导：整个地区
(2, 'REGION', '1', '阿克苏地区'),
-- 地区副局长：整个地区
(3, 'REGION', '1', '阿克苏地区'),
-- 市局领导：阿克苏市
(4, 'CITY', '11', '阿克苏市'),
-- 温宿县领导：温宿县
(5, 'COUNTY', '21', '温宿县'),
-- 库车县领导：库车县
(6, 'COUNTY', '31', '库车县'),
-- 温宿县食品科：食品科
(7, 'DEPT', '23', '温宿县食品监管科'),
-- 地区食品科：全地区食品科
(8, 'REGION_DEPT', '3', '地区食品监管科'),
-- 执法人员：指定区域
(9, 'COUNTY', '21', '温宿县'),
(10, 'COUNTY', '31', '库车县')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- ============================================
-- 3. 企业注册审核记录 (enterprise_registration)
-- 表结构检查：需要匹配现有表结构
-- ============================================
-- 先检查enterprise_registration是否存在
-- 如果不存在则创建（假设已有）

-- ============================================
-- 4. 补充更多企业数据（确保地区多样性）
-- ============================================
INSERT INTO enterprise (credit_code, name, legal_person, phone, email, industry, area, address, business_scope, license_url, status, user_id, registration_status, qualification_urls, industry_type_code) VALUES
-- 阿克苏市新企业
('91652901MA77YYYYY1', '阿克苏市惠民食品厂', '马经理', '13800000010', 'hm@example.com', '食品生产', '阿克苏市', '阿克苏市惠民路20号', '食品加工生产', 'https://example.com/license10.jpg', 1, NULL, 'APPROVED', 'https://example.com/q10.jpg', 'C13'),
-- 温宿县新企业
('91652922MA77YYYYY2', '温宿县兴旺食品厂', '田经理', '13800000011', 'wx@example.com', '食品生产', '温宿县', '温宿县兴旺路21号', '食品加工生产', 'https://example.com/license11.jpg', 1, NULL, 'APPROVED', 'https://example.com/q11.jpg', 'C13'),
-- 库车县新企业
('91652923MA77YYYYY3', '库车县瑞祥食品厂', '韩经理', '13800000012', 'rx@example.com', '食品生产', '库车县', '库车县瑞祥路22号', '食品加工生产', 'https://example.com/license12.jpg', 1, NULL, 'APPROVED', 'https://example.com/q12.jpg', 'C13')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================
-- 5. 数据验证查询
-- ============================================
SELECT '=== 数据分布统计 ===' AS info;
SELECT CONCAT('总用户数: ', COUNT(*)) FROM sys_user;
SELECT CONCAT('总企业数: ', COUNT(*)) FROM enterprise;
SELECT CONCAT('组织架构数: ', COUNT(*)) FROM org_structure;
SELECT CONCAT('用户组织关联: ', COUNT(*)) FROM org_user_position;
SELECT CONCAT('数据权限记录: ', COUNT(*)) FROM user_data_scope;
SELECT CONCAT('检查记录数: ', COUNT(*)) FROM inspection_record;
SELECT CONCAT('整改通知数: ', COUNT(*)) FROM rectification_notice;
SELECT CONCAT('申诉记录数: ', COUNT(*)) FROM appeal;
SELECT CONCAT('角色权限明细: ', COUNT(*)) FROM role_permission_detail;

-- 地区分布
SELECT '=== 企业地区分布 ===' AS info;
SELECT area, COUNT(*) as cnt FROM enterprise GROUP BY area ORDER BY cnt DESC;

-- 行业分布
SELECT '=== 企业行业分布 ===' AS info;
SELECT industry, COUNT(*) as cnt FROM enterprise GROUP BY industry ORDER BY cnt DESC;

-- 用户角色分布
SELECT '=== 用户角色分布 ===' AS info;
SELECT r.name, COUNT(*) as cnt FROM sys_user u JOIN sys_role r ON u.role_id = r.id GROUP BY r.name ORDER BY cnt DESC;

-- 数据权限范围
SELECT '=== 数据权限范围分布 ===' AS info;
SELECT scope_type, COUNT(*) as cnt FROM user_data_scope GROUP BY scope_type ORDER BY cnt DESC;
