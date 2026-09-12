#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
联调测试脚本：企业注册 + 数据权限
"""
import requests, json, time

BASE = "http://localhost:8080"

print("=" * 50)
print("联调测试：企业注册 + 数据权限体系")
print("=" * 50)

# 1. 测试Swagger文档可访问
print("\n[1/5] 检查API文档...")
r = requests.get(f"{BASE}/v3/api-docs", timeout=10)
print(f"  API文档状态: {r.status_code}")

# 2. 测试公共端点（无认证）
print("\n[2/5] 测试公开端点...")
r = requests.get(f"{BASE}/api/public/health", timeout=5)
print(f"  健康检查: {r.status_code} {r.text[:50] if r.text else ''}")

# 3. 测试企业列表（管理端 - 无认证应被拦截）
print("\n[3/5] 测试认证拦截...")
r = requests.get(f"{BASE}/api/admin/enterprise/list", timeout=5)
print(f"  无Token访问: {r.status_code} (应为401)")

# 4. 测试注册接口是否存在
print("\n[4/5] 检查企业注册API...")
r = requests.get(f"{BASE}/v3/api-docs", timeout=10)
api_data = json.loads(r.text)
paths = api_data.get("paths", {})

registration_paths = [p for p in paths.keys() if "registration" in p.lower() or "enterprise" in p.lower()]
print(f"  找到企业相关API: {len(registration_paths)} 个")
for p in registration_paths[:8]:
    print(f"    - {p}")

# 5. 检查数据权限相关API
print("\n[5/5] 检查数据权限API...")
perm_paths = [p for p in paths.keys() if "data" in p.lower() or "permission" in p.lower() or "role" in p.lower()]
print(f"  找到权限相关API: {len(perm_paths)} 个")
for p in perm_paths[:6]:
    print(f"    - {p}")

print("\n" + "=" * 50)
print("联调测试完成！")
print("=" * 50)
