-- ==========================================
-- 组织架构完整建设方案 - 数据库迁移 V3
-- ==========================================

-- 1. 改造 org_structure 表（添加Path、编码、类型等字段）
ALTER TABLE org_structure
    ADD COLUMN org_code VARCHAR(50) NULL COMMENT '组织编码，如 AKS-001',
    ADD COLUMN org_path VARCHAR(500) NULL COMMENT '路径，如 /1/2/5/',
    ADD COLUMN org_type VARCHAR(50) NULL COMMENT '组织类型：企业/机关/事业单位/社会团体',
    ADD COLUMN unified_social_credit_code VARCHAR(18) NULL COMMENT '统一社会信用代码',
    ADD COLUMN leader_name VARCHAR(100) NULL COMMENT '负责人姓名',
    ADD COLUMN leader_phone VARCHAR(20) NULL COMMENT '负责人电话',
    ADD COLUMN address VARCHAR(500) NULL COMMENT '地址',
    ADD COLUMN remark VARCHAR(1000) NULL COMMENT '备注';

-- 为现有数据生成Path
-- 先更新根节点
UPDATE org_structure SET org_path = CONCAT('/', id, '/') WHERE parent_id IS NULL;
-- 再更新子节点（温宿县等）
UPDATE org_structure os
    INNER JOIN org_structure parent ON os.parent_id = parent.id
SET os.org_path = CONCAT(parent.org_path, os.id, '/')
WHERE os.parent_id IS NOT NULL;

-- 为现有数据生成org_code
UPDATE org_structure SET org_code = CONCAT('ORG-', LPAD(id, 4, '0')) WHERE org_code IS NULL;

-- 2. 新建岗位表 sys_position
CREATE TABLE IF NOT EXISTS sys_position (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    position_code VARCHAR(50) NOT NULL COMMENT '岗位编码',
    position_name VARCHAR(100) NOT NULL COMMENT '岗位名称',
    category VARCHAR(20) NULL COMMENT '类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政',
    salary_range_min DECIMAL(10,2) NULL COMMENT '最低薪资',
    salary_range_max DECIMAL(10,2) NULL COMMENT '最高薪资',
    org_id BIGINT NULL COMMENT '所属组织ID',
    level INT NOT NULL DEFAULT 1 COMMENT '岗位级别',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：1启用,0禁用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    description VARCHAR(500) NULL COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_position_code (position_code),
    FOREIGN KEY (org_id) REFERENCES org_structure(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 3. 新建职务表 sys_job_title
CREATE TABLE IF NOT EXISTS sys_job_title (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title_code VARCHAR(50) NOT NULL COMMENT '职务编码',
    title_name VARCHAR(100) NOT NULL COMMENT '职务名称',
    category VARCHAR(20) NULL COMMENT '类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政',
    is_leadership TINYINT NOT NULL DEFAULT 0 COMMENT '是否领导职务：1是,0否',
    level INT NOT NULL DEFAULT 1 COMMENT '职务级别',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：1启用,0禁用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    description VARCHAR(500) NULL COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_title_code (title_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职务表';

-- 4. 新建用户岗位职务关联表 sys_user_position
CREATE TABLE IF NOT EXISTS sys_user_position (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    org_id BIGINT NOT NULL COMMENT '组织ID',
    position_id BIGINT NULL COMMENT '岗位ID',
    job_title_id BIGINT NULL COMMENT '职务ID',
    is_primary TINYINT NOT NULL DEFAULT 1 COMMENT '是否主职：1主职,0兼职',
    start_date DATE NULL COMMENT '任职开始日期',
    end_date DATE NULL COMMENT '任职结束日期',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：1在职,0离职',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (org_id) REFERENCES org_structure(id) ON DELETE CASCADE,
    FOREIGN KEY (position_id) REFERENCES sys_position(id) ON DELETE SET NULL,
    FOREIGN KEY (job_title_id) REFERENCES sys_job_title(id) ON DELETE SET NULL,
    UNIQUE KEY uk_user_org (user_id, org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位职务关联表';

-- 5. 改造 sys_role 表（添加父级ID支持角色继承）
ALTER TABLE sys_role
    ADD COLUMN parent_id BIGINT NULL COMMENT '父角色ID（角色继承）',
    ADD COLUMN role_type VARCHAR(20) NULL COMMENT '角色类型：ADMIN系统管理员/SECURITY安全管理员/AUDIT审计管理员/BUSINESS业务角色',
    ADD COLUMN data_scope VARCHAR(20) NULL DEFAULT 'ALL' COMMENT '数据权限范围：ALL全部/DEPT本部门/SELF仅自己/CUSTOM自定义',
    ADD COLUMN sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号';

-- 6. 改造 sys_permission 表（添加resource_type字段，如已有则忽略）
-- 检查并添加字段
SET @exist_col = (SELECT COUNT(*) FROM information_schema.columns 
                  WHERE table_schema = 'aksu_supervision' 
                  AND table_name = 'sys_permission' 
                  AND column_name = 'resource_type');
SET @sql = IF(@exist_col = 0, 'ALTER TABLE sys_permission ADD COLUMN resource_type VARCHAR(20) NULL COMMENT ''资源类型：menu菜单/button按钮/api接口''', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 7. 插入初始化岗位数据
INSERT INTO sys_position (position_code, position_name, category, level, status, sort_order, description) VALUES
('POS-ADMIN', '系统管理员', 'ADMIN', 1, 1, 1, '负责系统运维、备份，无业务数据权'),
('POS-SECURITY', '安全管理员', 'ADMIN', 1, 1, 2, '负责制定安全策略、分配角色'),
('POS-AUDIT', '审计管理员', 'ADMIN', 1, 1, 3, '负责查看日志、独立监督'),
('POS-INSPECTOR', '执法人员', 'TECHNICAL', 2, 1, 4, '负责执法检查、企业巡查'),
('POS-CLERK', '科室人员', 'ADMIN', 3, 1, 5, '科室日常工作人员'),
('POS-LEADER', '部门领导', 'MANAGEMENT', 1, 1, 6, '部门负责人，拥有部门数据权限');

-- 8. 插入初始化职务数据
INSERT INTO sys_job_title (title_code, title_name, category, is_leadership, level, status, sort_order) VALUES
('TITLE-DIRECTOR', '局长', 'MANAGEMENT', 1, 1, 1, 0),
('TITLE-DEPUTY', '副局长', 'MANAGEMENT', 1, 2, 1, 0),
('TITLE-CHIEF', '科长', 'MANAGEMENT', 1, 3, 1, 0),
('TITLE-DEPUTY-CHIEF', '副科长', 'MANAGEMENT', 1, 4, 1, 0),
('TITLE-SENIOR', '高级工程师', 'TECHNICAL', 0, 5, 1, 0),
('TITLE-ENGINEER', '工程师', 'TECHNICAL', 0, 6, 1, 0),
('TITLE-CLERK', '科员', 'ADMIN', 0, 7, 1, 0);

-- 9. 更新现有sys_role数据，添加role_type
UPDATE sys_role SET role_type = 'BUSINESS' WHERE role_type IS NULL;
