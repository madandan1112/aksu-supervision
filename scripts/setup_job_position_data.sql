-- 清理并重建职务和岗位数据，实现权限分离
-- 用户要求的6个职务层级：书记、局长、副局长、科长、副科长、一般执法人员

USE aksu_supervision;

-- 先清空旧数据
DELETE FROM sys_job_title;
DELETE FROM sys_position;

-- ==============================
-- 重新插入职务（job_title），每个职务关联角色
-- ==============================

-- 1. 书记 → 地区领导角色 (role_id=2, REGION_DIRECTOR)
INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order, description, role_id)
VALUES ('TITLE-SECRETARY', '书记', 'MANAGEMENT', 1, 1, 1, 1, '地区局党委书记，最高领导，拥有全局数据权限', 2);

-- 2. 局长 → 地区局副局长角色 (role_id=3, REGION_DEPUTY) 改用地区局领导
INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order, description, role_id)
VALUES ('TITLE-DIRECTOR', '局长', 'MANAGEMENT', 1, 2, 1, 2, '地区/市/县局局长，拥有所辖区域全部数据权限', 2);

-- 3. 副局长 → 市局领导角色 (role_id=4, CITY_DIRECTOR)
INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order, description, role_id)
VALUES ('TITLE-DEPUTY-DIRECTOR', '副局长', 'MANAGEMENT', 1, 3, 1, 3, '地区/市/县局副局长，协助局长管理，权限低于局长', 4);

-- 4. 科长 → 科室科长角色 (role_id=6, DEPT_CHIEF)
INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order, description, role_id)
VALUES ('TITLE-DEPT-CHIEF', '科长', 'MANAGEMENT', 1, 4, 1, 4, '科室负责人，拥有本科室全部数据权限', 6);

-- 5. 副科长 → 科室副科长角色 (role_id=7, DEPT_DEPUTY_CHIEF)
INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order, description, role_id)
VALUES ('TITLE-DEPT-DEPUTY', '副科长', 'MANAGEMENT', 1, 5, 1, 5, '科室副职，协助科长管理，拥有本科室数据权限', 7);

-- 6. 一般执法人员 → 普通执法人员角色 (role_id=1, ADMIN-执法人员)
INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order, description, role_id)
VALUES ('TITLE-ENFORCER', '一般执法人员', 'TECHNICAL', 0, 6, 1, 6, '一线执法人员，只能查看和操作自己负责的数据', 1);

-- ==============================
-- 重新插入岗位（position），每个岗位关联角色
-- ==============================

-- 1. 书记岗 → 地区领导
INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id)
VALUES ('POS-SECRETARY', '书记岗', 'LEADERSHIP', NULL, 1, 1, 1, '地区局党委书记岗位，全局最高领导', 2);

-- 2. 局长岗 → 地区局领导
INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id)
VALUES ('POS-DIRECTOR', '局长岗', 'LEADERSHIP', NULL, 2, 1, 2, '地区/市/县局局长岗位', 2);

-- 3. 副局长岗 → 市局领导
INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id)
VALUES ('POS-DEPUTY-DIRECTOR', '副局长岗', 'LEADERSHIP', NULL, 3, 1, 3, '地区/市/县局副局长岗位', 4);

-- 4. 科长岗 → 科室科长
INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id)
VALUES ('POS-DEPT-CHIEF', '科长岗', 'MANAGEMENT', NULL, 4, 1, 4, '科室负责人岗位', 6);

-- 5. 副科长岗 → 科室副科长
INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id)
VALUES ('POS-DEPT-DEPUTY', '副科长岗', 'MANAGEMENT', NULL, 5, 1, 5, '科室副职岗位', 7);

-- 6. 执法人员岗 → 一般执法人员
INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id)
VALUES ('POS-ENFORCER', '执法人员岗', 'TECHNICAL', NULL, 6, 1, 6, '一线执法岗位，负责日常检查', 1);

-- 7. 内勤岗 → 一般科员
INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id)
VALUES ('POS-CLERK', '内勤岗', 'ADMIN', NULL, 7, 1, 7, '内勤人员岗位，负责文书和档案', 1);

-- 验证结果
SELECT '--- 职务列表 ---' AS info;
SELECT id, title_code, title_name, is_leadership, level, role_id FROM sys_job_title ORDER BY level;

SELECT '--- 岗位列表 ---' AS info;
SELECT id, position_code, position_name, category, level, role_id FROM sys_position ORDER BY level;

SELECT '--- 职务-角色映射 ---' AS info;
SELECT jt.id, jt.title_name, jt.role_id, r.role_name, r.data_scope, r.region_scope
FROM sys_job_title jt LEFT JOIN sys_role r ON jt.role_id = r.id ORDER BY jt.level;
