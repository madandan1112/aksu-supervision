USE aksu_supervision;

-- 更新用户与组织/职务/岗位的关联
-- 基于用户名和角色映射到新创建的职务和岗位

-- 1. admin 超级管理员 - 不需要关联
-- 2. region_director 王地区 -> 书记 (org_id=1, job_title_id=24, position_id=23)
UPDATE sys_user SET org_id=1, org_name=(SELECT name FROM org_structure WHERE id=1), job_title_id=24, job_title_name='书记', position_id=23, position_name='书记岗' WHERE username='region_director';

-- 3. region_deputy 张副局 -> 副局长 (org_id=1, job_title_id=26, position_id=25)
UPDATE sys_user SET org_id=1, org_name=(SELECT name FROM org_structure WHERE id=1), job_title_id=26, job_title_name='副局长', position_id=25, position_name='副局长岗' WHERE username='region_deputy';

-- 4. city_director 李局长 -> 局长 (org_id=11阿克苏市局, job_title_id=25, position_id=24)
UPDATE sys_user SET org_id=11, org_name=(SELECT name FROM org_structure WHERE id=11), job_title_id=25, job_title_name='局长', position_id=24, position_name='局长岗' WHERE username='city_director';

-- 5. county_ws_director 赵局长 -> 局长 (org_id=21温宿县局, job_title_id=25, position_id=24)
UPDATE sys_user SET org_id=21, org_name=(SELECT name FROM org_structure WHERE id=21), job_title_id=25, job_title_name='局长', position_id=24, position_name='局长岗' WHERE username='county_ws_director';

-- 6. county_kc_director 钱局长 -> 局长 (org_id=31库车县局, job_title_id=25, position_id=24)
UPDATE sys_user SET org_id=31, org_name=(SELECT name FROM org_structure WHERE id=31), job_title_id=25, job_title_name='局长', position_id=24, position_name='局长岗' WHERE username='county_kc_director';

-- 7. dept_chief_ws_food 孙科长 -> 科长 (org_id=3食品监管科, job_title_id=27, position_id=26)
UPDATE sys_user SET org_id=3, org_name=(SELECT name FROM org_structure WHERE id=3), job_title_id=27, job_title_name='科长', position_id=26, position_name='科长岗' WHERE username='dept_chief_ws_food';

-- 8. region_dept_food 周科长 -> 科长 (org_id=3食品监管科, job_title_id=27, position_id=26)
UPDATE sys_user SET org_id=3, org_name=(SELECT name FROM org_structure WHERE id=3), job_title_id=27, job_title_name='科长', position_id=26, position_name='科长岗' WHERE username='region_dept_food';

-- 9. enforcer_ws_001 吴执法 -> 一般执法人员 (org_id=3食品监管科, job_title_id=29, position_id=28)
UPDATE sys_user SET org_id=3, org_name=(SELECT name FROM org_structure WHERE id=3), job_title_id=29, job_title_name='一般执法人员', position_id=28, position_name='执法人员岗' WHERE username='enforcer_ws_001';

-- 10. enforcer_kc_001 郑执法 -> 一般执法人员 (org_id=33库车食品监管科, job_title_id=29, position_id=28)
UPDATE sys_user SET org_id=33, org_name=(SELECT name FROM org_structure WHERE id=33), job_title_id=29, job_title_name='一般执法人员', position_id=28, position_name='执法人员岗' WHERE username='enforcer_kc_001';

-- 查看更新结果
SELECT id, username, real_name, org_id, org_name, job_title_id, job_title_name, position_id, position_name, role_id FROM sys_user WHERE org_id IS NOT NULL;
