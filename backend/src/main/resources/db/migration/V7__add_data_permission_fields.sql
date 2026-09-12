-- ============================================
-- V7: 数据权限体系扩展
-- ============================================

-- 1. 扩展 sys_role 表（使用存储过程避免列已存在报错）
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS add_role_columns()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'sys_role' AND column_name = 'region_scope') THEN
        ALTER TABLE sys_role ADD COLUMN region_scope VARCHAR(20) DEFAULT 'ALL' COMMENT '区域数据权限范围: ALL-全部, REGION-地区级, CITY-市级, COUNTY-县级, DEPT-科室级';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'sys_role' AND column_name = 'dept_scope') THEN
        ALTER TABLE sys_role ADD COLUMN dept_scope VARCHAR(20) DEFAULT 'ALL' COMMENT '科室数据权限范围: ALL-全部, SELF-仅自己, DEPT-本科室, SUBDEPT-科室及下级';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'sys_role' AND column_name = 'org_level') THEN
        ALTER TABLE sys_role ADD COLUMN org_level VARCHAR(20) DEFAULT NULL COMMENT '组织层级: REGION, CITY, COUNTY, DEPT';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'sys_role' AND column_name = 'org_id') THEN
        ALTER TABLE sys_role ADD COLUMN org_id BIGINT DEFAULT NULL COMMENT '关联组织ID（用于限定特定组织）';
    END IF;
END //
DELIMITER ;
CALL add_role_columns();
DROP PROCEDURE IF EXISTS add_role_columns;

-- 2. 创建用户-组织数据权限关联表
CREATE TABLE IF NOT EXISTS user_data_scope (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    org_id BIGINT NOT NULL COMMENT '组织ID',
    org_type VARCHAR(20) NOT NULL COMMENT '组织类型: REGION, CITY, COUNTY, DEPT',
    data_scope VARCHAR(20) DEFAULT 'SELF' COMMENT '数据范围: ALL, REGION, CITY, COUNTY, DEPT, SELF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_org (user_id, org_id)
) COMMENT='用户数据权限范围配置表';

-- 3. 创建角色-按钮权限关联表
CREATE TABLE IF NOT EXISTS role_permission_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    module VARCHAR(50) NOT NULL COMMENT '所属模块',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_perm (role_id, permission_code)
) COMMENT='角色按钮级权限明细表';
