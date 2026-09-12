#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import subprocess
import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def run_sql(sql, db='aksu_supervision'):
    cmd = [
        'dev-env/mysql/bin/mysql.exe',
        '-uroot', '-p123456',
        '--default-character-set=utf8mb4',
        db, '-e', sql
    ]
    r = subprocess.run(cmd, capture_output=True, text=True, encoding='utf-8', errors='ignore')
    if r.returncode != 0 and 'ERROR' in r.stderr:
        print(f"[ERROR] {r.stderr[:200]}")
        return False
    return True

def check_column_exists(table, column):
    sql = f"SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = '{table}' AND column_name = '{column}'"
    r = subprocess.run([
        'dev-env/mysql/bin/mysql.exe', '-uroot', '-p123456',
        '--default-character-set=utf8mb4', 'aksu_supervision', '-e', sql
    ], capture_output=True, text=True, encoding='utf-8', errors='ignore')
    return r.returncode == 0 and '1' in r.stdout

def main():
    print("=== V7 Database Migration Start ===")
    
    columns_to_add = [
        ('region_scope', "VARCHAR(20) DEFAULT 'ALL' COMMENT 'Region data scope'"),
        ('dept_scope', "VARCHAR(20) DEFAULT 'ALL' COMMENT 'Dept data scope'"),
        ('org_level', "VARCHAR(20) DEFAULT NULL COMMENT 'Org level'"),
        ('org_id', "BIGINT DEFAULT NULL COMMENT 'Org ID'")
    ]
    
    for col, defn in columns_to_add:
        if not check_column_exists('sys_role', col):
            sql = f"ALTER TABLE sys_role ADD COLUMN {col} {defn}"
            if run_sql(sql):
                print(f"OK: Added sys_role.{col}")
            else:
                print(f"FAIL: Adding sys_role.{col}")
        else:
            print(f"OK: sys_role.{col} already exists")
    
    sql = """
    CREATE TABLE IF NOT EXISTS user_data_scope (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        org_id BIGINT NOT NULL,
        org_type VARCHAR(20) NOT NULL,
        data_scope VARCHAR(20) DEFAULT 'SELF',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE KEY uk_user_org (user_id, org_id)
    )
    """
    if run_sql(sql):
        print("OK: Created user_data_scope")
    
    sql = """
    CREATE TABLE IF NOT EXISTS role_permission_detail (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        role_id BIGINT NOT NULL,
        permission_code VARCHAR(100) NOT NULL,
        permission_name VARCHAR(100) NOT NULL,
        module VARCHAR(50) NOT NULL,
        status TINYINT DEFAULT 1,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE KEY uk_role_perm (role_id, permission_code)
    )
    """
    if run_sql(sql):
        print("OK: Created role_permission_detail")
    
    role_configs = [
        (1, 'ADMIN', 'ALL', 'ALL', 'ALL', 'REGION'),
        (2, 'LEADER', 'ALL', 'ALL', 'ALL', 'REGION'),
        (3, 'INSPECTOR', 'SELF', 'SELF', 'SELF', 'DEPT')
    ]
    
    for rid, rcode, ds, rs, dps, ol in role_configs:
        sql = f"""
        UPDATE sys_role SET 
            data_scope = '{ds}',
            region_scope = '{rs}',
            dept_scope = '{dps}',
            org_level = '{ol}'
        WHERE id = {rid}
        """
        if run_sql(sql):
            print(f"OK: Updated role {rcode}")
    
    print("=== V7 Migration Complete ===")

if __name__ == '__main__':
    main()
