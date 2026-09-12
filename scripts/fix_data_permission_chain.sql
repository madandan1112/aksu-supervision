-- =====================================================================
-- 2026-09-09 数据权限链路修复
-- 1) 角色数据范围语义对齐：内部角色不再全部 ALL（否则数据权限过滤形同虚设）
-- 2) 演示执法人员 inspector1/inspector2 补角色（role 1 执法人员）
-- 3) sys_user_position 种子：建立 科室→县市监局→县市→地区局 完整升级链
-- 4) 企业区域名归一：库车县 → 库车市（2019 撤县设市，与组织架构短名一致）
-- 幂等：可重复执行
-- =====================================================================
USE aksu_supervision;

-- 1) 角色数据范围语义
-- 执法人员：科室视角（属地县市 + 本科室）
UPDATE sys_role SET data_scope='DEPT',  region_scope='COUNTY', dept_scope='DEPT' WHERE id=1  AND NOT (data_scope='DEPT');
-- 市局领导：市级视角
UPDATE sys_role SET data_scope='CITY',  region_scope='CITY',   dept_scope='ALL'  WHERE id=4  AND data_scope='ALL';
-- 县局领导：县级视角
UPDATE sys_role SET data_scope='COUNTY',region_scope='COUNTY', dept_scope='ALL'  WHERE id=5  AND data_scope='ALL';
-- 科室科长/副科长/地区科室科长：科室视角
UPDATE sys_role SET data_scope='DEPT',  region_scope='ALL',    dept_scope='DEPT' WHERE id IN (6,7,8) AND data_scope='ALL';
-- 市局科室科长：市级+科室视角
UPDATE sys_role SET data_scope='CITY',  region_scope='CITY',   dept_scope='DEPT' WHERE id=11 AND data_scope='ALL';
-- 地区级（地区领导/地区副局长/管理员）保持 ALL 不变；企业角色（10/12/13）保持 SELF 不变

-- 2) 演示账号补角色
UPDATE sys_user SET role_id=1 WHERE username IN ('inspector1','inspector2') AND role_id IS NULL;

-- 3) 组织关系种子（升级链：150食品安全监管科 → 124阿克苏市市监局 → 2阿克苏市 → 1地区局）
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 150, 1, 1 FROM sys_user u WHERE u.username='enforcer_ws_001'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=150);
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 150, 1, 1 FROM sys_user u WHERE u.username='dept_chief_ws_food'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=150);
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 167, 1, 1 FROM sys_user u WHERE u.username='enforcer_kc_001'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=167);
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 150, 1, 1 FROM sys_user u WHERE u.username='inspector1'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=150);
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 153, 1, 1 FROM sys_user u WHERE u.username='inspector2'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=153);
-- 局领导挂对应层级组织
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 124, 1, 1 FROM sys_user u WHERE u.username='county_ws_director'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=124);
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 2, 1, 1 FROM sys_user u WHERE u.username='city_director'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=2);
INSERT INTO sys_user_position (user_id, org_id, is_primary, status)
SELECT u.id, 1, 1, 1 FROM sys_user u WHERE u.username='region_director'
  AND NOT EXISTS (SELECT 1 FROM sys_user_position p WHERE p.user_id=u.id AND p.org_id=1);

-- 4) 企业区域名归一
UPDATE enterprise SET area='库车市' WHERE area='库车县';

-- 5) appeal.related_fields 列类型与实体对齐（实体为String、前端传文本，JSON列导致写入500）
ALTER TABLE appeal MODIFY COLUMN related_fields VARCHAR(500) NULL COMMENT '关联领域(文本)';
