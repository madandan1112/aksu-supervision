# -*- coding: utf-8 -*-
"""Database Migration V8: Enterprise registration, role hierarchy, data permission"""
import subprocess

MYSQL = 'D:/aksu-supervision/dev-env/mysql/bin/mysql.exe'
DB = 'aksu_supervision'

def run_sql(sql):
    result = subprocess.run(
        [MYSQL, '-uroot', '-p123456', DB, '--default-character-set=utf8mb4', '-e', sql],
        capture_output=True, text=True, encoding='utf-8'
    )
    if result.returncode != 0 and 'Duplicate' not in result.stderr and 'already exists' not in result.stderr:
        print(f"WARN: {result.stderr.strip()}")
    else:
        print(f"OK: {sql[:80]}...")

# 1. Add role_id column to sys_user
run_sql("ALTER TABLE sys_user ADD COLUMN role_id BIGINT NULL COMMENT 'role ID' AFTER user_type;")

# 2. Add enterprise registration fields
run_sql("ALTER TABLE enterprise ADD COLUMN registration_status VARCHAR(20) DEFAULT 'PENDING' AFTER status;")
run_sql("ALTER TABLE enterprise ADD COLUMN qualification_urls JSON NULL AFTER license_url;")
run_sql("ALTER TABLE enterprise ADD COLUMN industry_type_code VARCHAR(50) NULL AFTER industry;")
run_sql("ALTER TABLE enterprise ADD COLUMN review_comment VARCHAR(500) NULL AFTER registration_status;")
run_sql("ALTER TABLE enterprise ADD COLUMN reviewed_by BIGINT NULL AFTER review_comment;")
run_sql("ALTER TABLE enterprise ADD COLUMN reviewed_at DATETIME NULL AFTER reviewed_by;")

# 3. Create enterprise_registration table
sql_create_reg = """
CREATE TABLE IF NOT EXISTS enterprise_registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    step INT DEFAULT 1,
    credit_code VARCHAR(50),
    enterprise_name VARCHAR(200),
    legal_person VARCHAR(200),
    address VARCHAR(500),
    phone VARCHAR(20),
    industry VARCHAR(100),
    industry_type_code VARCHAR(50),
    area VARCHAR(100),
    license_url VARCHAR(500),
    qualification_urls JSON,
    status VARCHAR(20) DEFAULT 'DRAFT',
    review_comment VARCHAR(500),
    reviewed_by BIGINT,
    reviewed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
"""
run_sql(sql_create_reg)

# 4. Write SQL file for Chinese content to avoid encoding issues
sql_file = 'D:/aksu-supervision/db_v8_chinese.sql'
with open(sql_file, 'w', encoding='utf-8') as f:
    f.write("-- V8 Chinese data migration\n")
    # Update sys_role
    f.write("UPDATE sys_role SET role_code='REGION_DIRECTOR', role_name='地区主管领导', description='可查看整个阿克苏地区所有数据', data_scope='ALL', region_scope='REGION', dept_scope='ALL', org_level='REGION', parent_id=NULL, role_type='BUSINESS', sort_order=10 WHERE id=2;\n")
    f.write("UPDATE sys_role SET role_code='ENFORCER', role_name='执法人员', description='仅可查看本科室管辖范围内的数据', data_scope='DEPT', region_scope='COUNTY', dept_scope='DEPT', org_level='DEPT', parent_id=6, role_type='BUSINESS', sort_order=50 WHERE id=3;\n")
    
    # Insert new roles
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (4, 'REGION_DEPUTY', '地区分管领导', '分管地区某领域工作，可查看全地区数据', 1, 2, 'BUSINESS', 'ALL', 'REGION', 'ALL', 'REGION', 15);\n")
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (5, 'CITY_DIRECTOR', '市级主管领导', '可查看本市所有数据', 1, 2, 'BUSINESS', 'ALL', 'CITY', 'ALL', 'CITY', 20);\n")
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (6, 'COUNTY_DIRECTOR', '县/市级主管领导', '可查看本县/市所有数据', 1, 5, 'BUSINESS', 'ALL', 'COUNTY', 'ALL', 'COUNTY', 25);\n")
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (7, 'DEPT_CHIEF', '科室负责人', '可查看本科室管辖范围数据', 1, 6, 'BUSINESS', 'DEPT', 'COUNTY', 'DEPT', 'DEPT', 30);\n")
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (8, 'DEPT_DEPUTY_CHIEF', '副科长', '可查看本科室管辖范围数据', 1, 7, 'BUSINESS', 'DEPT', 'COUNTY', 'DEPT', 'DEPT', 35);\n")
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (9, 'REGION_DEPT_CHIEF', '地区科室负责人', '可查看全地区该科室管辖范围数据', 1, 4, 'BUSINESS', 'ALL', 'REGION', 'DEPT', 'REGION', 32);\n")
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (10, 'ENTERPRISE_USER', '企业用户', '企业用户角色，仅可查看自己企业数据', 1, NULL, 'BUSINESS', 'SELF', 'SELF', 'SELF', 'ENTERPRISE', 60);\n")
    f.write("INSERT IGNORE INTO sys_role (id, role_code, role_name, description, status, parent_id, role_type, data_scope, region_scope, dept_scope, org_level, sort_order) VALUES (11, 'CITY_DEPT_CHIEF', '市级科室负责人', '可查看本市该科室管辖范围数据', 1, 5, 'BUSINESS', 'ALL', 'CITY', 'DEPT', 'CITY', 33);\n")
    
    # Update sys_position role associations
    f.write("UPDATE sys_position SET role_id=1 WHERE id=1;\n")
    f.write("UPDATE sys_position SET role_id=3 WHERE id=4;\n")
    f.write("UPDATE sys_position SET role_id=7 WHERE id=5;\n")
    f.write("UPDATE sys_position SET role_id=2 WHERE id=6;\n")
    
    # Update sys_job_title role associations
    f.write("UPDATE sys_job_title SET role_id=2 WHERE id=1;\n")
    f.write("UPDATE sys_job_title SET role_id=4 WHERE id=2;\n")
    f.write("UPDATE sys_job_title SET role_id=7 WHERE id=3;\n")
    f.write("UPDATE sys_job_title SET role_id=8 WHERE id=4;\n")
    f.write("UPDATE sys_job_title SET role_id=3 WHERE id=5;\n")
    f.write("UPDATE sys_job_title SET role_id=3 WHERE id=6;\n")
    f.write("UPDATE sys_job_title SET role_id=3 WHERE id=7;\n")
    
    # Insert more job titles
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (9, 'TITLE-REGION-CHIEF', '地区局局长', 1, 1, 2, 1, 1);\n")
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (10, 'TITLE-REGION-DEPUTY', '地区局副局长', 1, 2, 4, 1, 2);\n")
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (11, 'TITLE-CITY-CHIEF', '市/县局局长', 1, 2, 5, 1, 3);\n")
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (12, 'TITLE-CITY-DEPUTY', '市/县局副局长', 1, 3, 6, 1, 4);\n")
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (13, 'TITLE-KEZHANG', '科长', 1, 4, 9, 1, 5);\n")
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (14, 'TITLE-FUKEZHANG', '副科长', 1, 5, 8, 1, 6);\n")
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (15, 'TITLE-ZHUREN-KEERYUAN', '科室主任科员', 0, 6, 3, 1, 7);\n")
    f.write("INSERT IGNORE INTO sys_job_title (id, title_code, title_name, is_leadership, level, role_id, status, sort_order) VALUES (16, 'TITLE-KEYYUAN', '科员', 0, 7, 3, 1, 8);\n")
    
    # Insert more positions
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (8, 'POS-REGION-CHIEF', '地区局局长', 'LEADERSHIP', 1, 2, 1, 1);\n")
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (9, 'POS-REGION-DEPUTY', '地区局副局长', 'LEADERSHIP', 2, 4, 1, 2);\n")
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (10, 'POS-CITY-CHIEF', '市/县局局长', 'LEADERSHIP', 2, 5, 1, 3);\n")
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (11, 'POS-CITY-DEPUTY', '市/县局副局长', 'LEADERSHIP', 3, 6, 1, 4);\n")
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (12, 'POS-KEZHANG', '科长', 'LEADERSHIP', 4, 9, 1, 5);\n")
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (13, 'POS-FUKEZHANG', '副科长', 'LEADERSHIP', 5, 8, 1, 6);\n")
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (14, 'POS-ZHUREN-KEERYUAN', '主任科员', 'GENERAL', 6, 3, 1, 7);\n")
    f.write("INSERT IGNORE INTO sys_position (id, position_code, position_name, category, level, role_id, status, sort_order) VALUES (15, 'POS-KEYYUAN', '科员', 'GENERAL', 7, 3, 1, 8);\n")

# Execute Chinese SQL file
result = subprocess.run(
    [MYSQL, '-uroot', '-p123456', DB, '--default-character-set=utf8mb4', '-e', f'source {sql_file}'],
    capture_output=True, text=True, encoding='utf-8'
)
if result.returncode != 0:
    print(f"Chinese SQL WARN: {result.stderr.strip()}")
else:
    print("OK: Chinese data migration complete")

# 5. Insert role_permission_detail (no Chinese, safe to run directly)
modules_all = ['enterprise', 'inspection', 'rectification', 'report', 'user', 'system']
actions_all = ['create', 'update', 'delete', 'view']

# REGION_DIRECTOR (id=2)
for m in modules_all:
    for a in actions_all:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (2, '{m}:{a}', '{m}:{a}', '{m}', 1);")

# REGION_DEPUTY (id=4)
for m in modules_all:
    for a in actions_all:
        if m in ['system', 'user'] and a == 'delete':
            continue
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (4, '{m}:{a}', '{m}:{a}', '{m}', 1);")

# CITY_DIRECTOR (id=5)
for m in ['enterprise', 'inspection', 'rectification', 'report']:
    for a in actions_all:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (5, '{m}:{a}', '{m}:{a}', '{m}', 1);")
for a in ['view', 'create', 'update']:
    run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (5, 'user:{a}', 'user:{a}', 'user', 1);")

# COUNTY_DIRECTOR (id=6)
for m in ['enterprise', 'inspection', 'rectification', 'report']:
    for a in actions_all:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (6, '{m}:{a}', '{m}:{a}', '{m}', 1);")
for a in ['view', 'create', 'update']:
    run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (6, 'user:{a}', 'user:{a}', 'user', 1);")

# DEPT_CHIEF (id=7)
for m in ['enterprise', 'inspection', 'rectification']:
    for a in ['view', 'create', 'update']:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (7, '{m}:{a}', '{m}:{a}', '{m}', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (7, 'report:view', 'report:view', 'report', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (7, 'report:create', 'report:create', 'report', 1);")

# DEPT_DEPUTY_CHIEF (id=8)
for m in ['enterprise', 'inspection', 'rectification']:
    for a in ['view', 'create', 'update']:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (8, '{m}:{a}', '{m}:{a}', '{m}', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (8, 'report:view', 'report:view', 'report', 1);")

# REGION_DEPT_CHIEF (id=9)
for m in ['enterprise', 'inspection', 'rectification']:
    for a in ['view', 'create', 'update']:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (9, '{m}:{a}', '{m}:{a}', '{m}', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (9, 'report:view', 'report:view', 'report', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (9, 'report:create', 'report:create', 'report', 1);")

# CITY_DEPT_CHIEF (id=11)
for m in ['enterprise', 'inspection', 'rectification']:
    for a in ['view', 'create', 'update']:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (11, '{m}:{a}', '{m}:{a}', '{m}', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (11, 'report:view', 'report:view', 'report', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (11, 'report:create', 'report:create', 'report', 1);")

# ENTERPRISE_USER (id=10)
for m in ['enterprise', 'rectification', 'report']:
    run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (10, '{m}:view', '{m}:view', '{m}', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (10, 'appeal:create', 'appeal:create', 'appeal', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (10, 'appeal:view', 'appeal:view', 'appeal', 1);")

# ENFORCER (id=3)
for m in ['enterprise', 'inspection', 'rectification']:
    for a in ['view', 'create', 'update']:
        run_sql(f"INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (3, '{m}:{a}', '{m}:{a}', '{m}', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (3, 'report:view', 'report:view', 'report', 1);")
run_sql("INSERT IGNORE INTO role_permission_detail (role_id, permission_code, permission_name, module, status) VALUES (3, 'report:create', 'report:create', 'report', 1);")

print("\n=== Migration V8 Complete ===")
