USE aksu_supervision;

-- 查看组织架构中level=5的科室列表（供关联用）
SELECT id, name, level, parent_id FROM org_structure WHERE level = 5 ORDER BY id LIMIT 20;
