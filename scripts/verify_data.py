#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试数据验证脚本 - 检查数据连通性
"""
import subprocess
import json

def run_mysql_query(sql):
    """执行MySQL查询"""
    cmd = [
        'D:/aksu-supervision/dev-env/mysql/bin/mysql.exe',
        '-uroot', '-p123456', '-hlocalhost', '-P3306',
        '-e', sql,
        '--skip-column-names', '--batch'
    ]
    result = subprocess.run(cmd, capture_output=True, text=True)
    return result.stdout.strip()

def check_data(table_name):
    """检查表数据量"""
    sql = f"SELECT COUNT(*) FROM aksu_supervision.{table_name}"
    result = run_mysql_query(sql)
    try:
        return int(result) if result else 0
    except:
        return result

def check_column(table, column):
    """检查表是否有某列"""
    sql = f"SHOW COLUMNS FROM aksu_supervision.{table} LIKE '{column}'"
    result = run_mysql_query(sql)
    return bool(result.strip())

print("=" * 60)
print("数据完整性检查报告")
print("=" * 60)

tables = {
    'sys_user': '用户表',
    'sys_role': '角色表',
    'sys_position': '岗位表',
    'sys_job_title': '职务表',
    'org_structure': '组织架构',
    'org_user_position': '用户组织关联',
    'user_data_scope': '用户数据权限',
    'enterprise': '企业表',
    'enterprise_contact': '企业联系人',
    'enterprise_registration': '企业注册审核',
    'inspection_record': '检查记录',
    'rectification_notice': '整改通知',
    'appeal': '申诉记录',
    'role_permission_detail': '角色权限明细'
}

for table, desc in tables.items():
    count = check_data(table)
    print(f"  {desc:<20} ({table:<25}): {count} 条")

print("\n" + "=" * 60)
print("关键数据分布检查")
print("=" * 60)

# 检查用户分布
print("\n用户角色分布:")
role_sql = """
SELECT r.name, COUNT(*) 
FROM aksu_supervision.sys_user u 
JOIN aksu_supervision.sys_role r ON u.role_id = r.id 
GROUP BY r.name
"""
roles = run_mysql_query(role_sql).split('\n')
for line in roles:
    if line.strip():
        print(f"  {line.strip()}")

# 检查企业分布
print("\n企业地区分布:")
area_sql = """
SELECT area, COUNT(*) FROM aksu_supervision.enterprise GROUP BY area
"""
areas = run_mysql_query(area_sql).split('\n')
for line in areas:
    if line.strip():
        print(f"  {line.strip()}")

# 检查注册审核状态
print("\n注册审核状态分布:")
reg_sql = """
SELECT registration_status, COUNT(*) FROM aksu_supervision.enterprise_registration GROUP BY registration_status
"""
regs = run_mysql_query(reg_sql).split('\n')
for line in regs:
    if line.strip():
        print(f"  {line.strip()}")

# 检查数据权限
print("\n数据权限范围分布:")
scope_sql = """
SELECT scope_type, COUNT(*) FROM aksu_supervision.user_data_scope GROUP BY scope_type
"""
scopes = run_mysql_query(scope_sql).split('\n')
for line in scopes:
    if line.strip():
        print(f"  {line.strip()}")

# 检查API连通性
print("\n" + "=" * 60)
print("后端API连通性测试")
print("=" * 60)

import urllib.request
import urllib.error

def test_api(url, method='GET', headers=None, data=None):
    try:
        req = urllib.request.Request(url, method=method)
        if headers:
            for k, v in headers.items():
                req.add_header(k, v)
        if data:
            req.add_header('Content-Type', 'application/json')
            req.data = data.encode('utf-8')
        with urllib.request.urlopen(req, timeout=5) as response:
            return response.status, response.read().decode('utf-8')[:100]
    except urllib.error.HTTPError as e:
        return e.code, str(e.reason)[:100]
    except Exception as e:
        return 'ERROR', str(e)[:100]

BASE = 'http://localhost:8080'

apis = [
    ('GET', f'{BASE}/v3/api-docs', None, 'Swagger文档'),
    ('GET', f'{BASE}/api/public/health', None, '健康检查'),
    ('POST', f'{BASE}/api/auth/login', '{"phone":"13800138000","password":"admin123","captcha":"1234","captchaKey":"test"}', '登录'),
    ('GET', f'{BASE}/api/admin/system/role', None, '角色列表(需认证)'),
    ('GET', f'{BASE}/api/admin/enterprise/list', None, '企业列表(需认证)'),
    ('GET', f'{BASE}/api/enterprise/register/status', None, '注册状态(需认证)'),
]

for method, url, data, desc in apis:
    code, resp = test_api(url, method, data=data)
    status = "OK" if code in [200, 201] else ("AUTH" if code in [401, 403] else "ERR")
    print(f"  {desc:<30} [{status}] HTTP {code} -> {resp[:50]}")

print("\n" + "=" * 60)
print("数据验证完成!")
print("=" * 60)
