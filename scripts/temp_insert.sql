USE aksu_supervision;
DELETE FROM sys_job_title;
DELETE FROM sys_position;

INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order, description, role_id) VALUES
('TITLE-SECRETARY', '书记', 'MANAGEMENT', 1, 1, 1, 1, '地区局党委书记，最高领导，拥有全局数据权限', 2),
('TITLE-DIRECTOR', '局长', 'MANAGEMENT', 1, 2, 1, 2, '地区/市/县局局长，拥有所辖区域全部数据权限', 2),
('TITLE-DEPUTY-DIRECTOR', '副局长', 'MANAGEMENT', 1, 3, 1, 3, '地区/市/县局副局长，协助局长管理', 4),
('TITLE-DEPT-CHIEF', '科长', 'MANAGEMENT', 1, 4, 1, 4, '科室负责人，拥有本科室全部数据权限', 6),
('TITLE-DEPT-DEPUTY', '副科长', 'MANAGEMENT', 1, 5, 1, 5, '科室副职，协助科长管理', 7),
('TITLE-ENFORCER', '一般执法人员', 'TECHNICAL', 0, 6, 1, 6, '一线执法人员，只能查看和操作自己负责的数据', 1);

INSERT INTO sys_position (position_code, position_name, category, org_id, level, status, sort_order, description, role_id) VALUES
('POS-SECRETARY', '书记岗', 'LEADERSHIP', NULL, 1, 1, 1, '地区局党委书记岗位，全局最高领导', 2),
('POS-DIRECTOR', '局长岗', 'LEADERSHIP', NULL, 2, 1, 2, '地区/市/县局局长岗位', 2),
('POS-DEPUTY-DIRECTOR', '副局长岗', 'LEADERSHIP', NULL, 3, 1, 3, '地区/市/县局副局长岗位', 4),
('POS-DEPT-CHIEF', '科长岗', 'MANAGEMENT', NULL, 4, 1, 4, '科室负责人岗位', 6),
('POS-DEPT-DEPUTY', '副科长岗', 'MANAGEMENT', NULL, 5, 1, 5, '科室副职岗位', 7),
('POS-ENFORCER', '执法人员岗', 'TECHNICAL', NULL, 6, 1, 6, '一线执法岗位，负责日常检查', 1),
('POS-CLERK', '内勤岗', 'ADMIN', NULL, 7, 1, 7, '内勤人员岗位，负责文书和档案', 1);

SELECT id, title_code, title_name, level, role_id FROM sys_job_title ORDER BY level;
SELECT id, position_code, position_name, level, role_id FROM sys_position ORDER BY level;
