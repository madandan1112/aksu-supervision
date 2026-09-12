# -*- coding: utf-8 -*-
import os, sys, io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

root = r'D:\aksu-supervision'
skip_dirs = {'node_modules', '.git', 'target', 'dist', '__pycache__', '.idea', '.vscode'}

def safe_name(name):
    return name.encode('ascii', 'replace').decode('ascii')

print("=" * 70)
print("[aksu-supervision] Project File List")
print("=" * 70)

# 1. Top-level
print("\n[1. Top-level directories]")
for item in sorted(os.listdir(root)):
    full = os.path.join(root, item)
    tag = "[DIR]" if os.path.isdir(full) else "[FILE]"
    try:
        print(f"  {tag} {item}")
    except:
        print(f"  {tag} {safe_name(item)}")

# 2. Backend Java
print("\n[2. Backend Java - backend/src/main/java/]")
java_root = os.path.join(root, 'backend', 'src', 'main', 'java')
if os.path.exists(java_root):
    for dirpath, dirnames, filenames in os.walk(java_root):
        dirnames[:] = [d for d in dirnames if d not in skip_dirs]
        rel = os.path.relpath(dirpath, java_root).replace('\\', '/')
        for f in sorted(filenames):
            if f.endswith('.java'):
                prefix = f"  {rel}/" if rel != '.' else "  "
                print(f"{prefix}{f}")

# 3. Backend resources
print("\n[3. Backend Config - backend/src/main/resources/]")
res_root = os.path.join(root, 'backend', 'src', 'main', 'resources')
if os.path.exists(res_root):
    for dirpath, dirnames, filenames in os.walk(res_root):
        dirnames[:] = [d for d in dirnames if d not in skip_dirs]
        rel = os.path.relpath(dirpath, res_root).replace('\\', '/')
        for f in sorted(filenames):
            prefix = f"  {rel}/" if rel != '.' else "  "
            print(f"{prefix}{f}")

# 4. Web admin Vue
print("\n[4. Web Admin - web-admin/src/]")
web_root = os.path.join(root, 'web-admin', 'src')
if os.path.exists(web_root):
    for dirpath, dirnames, filenames in os.walk(web_root):
        dirnames[:] = [d for d in dirnames if d not in skip_dirs]
        rel = os.path.relpath(dirpath, web_root).replace('\\', '/')
        for f in sorted(filenames):
            if f.endswith(('.vue', '.js', '.scss', '.css', '.ts')):
                prefix = f"  {rel}/" if rel != '.' else "  "
                print(f"{prefix}{f}")

# 5. Miniapp H5
print("\n[5. Miniapp H5 - miniapp-h5/src/]")
mini_root = os.path.join(root, 'miniapp-h5', 'src')
if os.path.exists(mini_root):
    for dirpath, dirnames, filenames in os.walk(mini_root):
        dirnames[:] = [d for d in dirnames if d not in skip_dirs]
        rel = os.path.relpath(dirpath, mini_root).replace('\\', '/')
        for f in sorted(filenames):
            if f.endswith(('.vue', '.js', '.scss', '.css', '.ts')):
                prefix = f"  {rel}/" if rel != '.' else "  "
                print(f"{prefix}{f}")

# 6. Android APP
print("\n[6. Android APP - android-app/]")
android_root = os.path.join(root, 'android-app')
if os.path.exists(android_root):
    for item in sorted(os.listdir(android_root)):
        print(f"  {item}")

# 7. Database SQL
print("\n[7. Database Scripts]")
for sql_dir in ['sql', 'db_backup']:
    full_dir = os.path.join(root, sql_dir)
    if os.path.exists(full_dir):
        for f in sorted(os.listdir(full_dir)):
            print(f"  {sql_dir}/{f}")

# 8. Build artifacts
print("\n[8. Build Artifacts]")
jar_path = os.path.join(root, 'backend', 'target', 'aksu-supervision-backend-1.0.0.jar')
if os.path.exists(jar_path):
    size_mb = os.path.getsize(jar_path) / 1024 / 1024
    print(f"  Backend JAR: backend/target/aksu-supervision-backend-1.0.0.jar ({size_mb:.1f}MB)")
else:
    # find any jar
    target_dir = os.path.join(root, 'backend', 'target')
    if os.path.exists(target_dir):
        for f in os.listdir(target_dir):
            if f.endswith('.jar') and not f.endswith('-sources.jar'):
                fp = os.path.join(target_dir, f)
                size_mb = os.path.getsize(fp) / 1024 / 1024
                print(f"  Backend JAR: backend/target/{f} ({size_mb:.1f}MB)")

for name, sub in [('Web Admin', 'web-admin'), ('Miniapp H5', 'miniapp-h5')]:
    dist_dir = os.path.join(root, sub, 'dist')
    if os.path.exists(dist_dir):
        fc = sum(len(ff) for _, _, ff in os.walk(dist_dir))
        print(f"  {name} dist: {sub}/dist/ ({fc} files)")

# 9. Docs
print("\n[9. Documentation]")
for doc_dir in ['docs', 'dev-docs']:
    full_dir = os.path.join(root, doc_dir)
    if os.path.exists(full_dir):
        for f in sorted(os.listdir(full_dir)):
            print(f"  {doc_dir}/{f}")

# 10. Dev Env
print("\n[10. Dev Environment - dev-env/]")
dev_root = os.path.join(root, 'dev-env')
if os.path.exists(dev_root):
    for item in sorted(os.listdir(dev_root)):
        full = os.path.join(dev_root, item)
        if os.path.isdir(full):
            print(f"  [DIR] {item}")
        else:
            try:
                size_mb = os.path.getsize(full) / 1024 / 1024
                print(f"  [FILE] {item} ({size_mb:.1f}MB)")
            except:
                print(f"  [FILE] {item}")

# 11. Sec Tools
print("\n[11. Security Tools - sec-tools/]")
sec_root = os.path.join(root, 'sec-tools')
if os.path.exists(sec_root):
    for item in sorted(os.listdir(sec_root)):
        print(f"  {item}")

# ====== DATABASE INFO ======
print("\n" + "=" * 70)
print("[DATABASE CONNECTION INFO]")
print("=" * 70)
print("  DB Type:       MySQL 8.0")
print("  Host:          localhost (127.0.0.1)")
print("  Port:          13306")
print("  Username:      root")
print("  Password:      Majunqiu110@")
print("  Database:      aksu_supervision")
print("  Charset:       utf8mb4")
print("  my.ini:        D:\\aksu-supervision\\dev-env\\mysql\\my.ini")
print("  Data Dir:      D:\\aksu-supervision\\dev-env\\mysql\\data\\")
print("  Also user:     crm_user (with same password)")

# ====== BACKEND CONFIG ======
print("\n" + "=" * 70)
print("[BACKEND SERVICE CONFIG]")
print("=" * 70)
print("  Port:          8080")
print("  JDK:           D:\\aksu-supervision\\dev-env\\jdk-17\\jdk-17.0.12+7\\")
print("  Maven:         D:\\aksu-supervision\\dev-env\\maven\\apache-maven-3.9.6\\")
print("  Config:        backend/src/main/resources/application.yml")

# ====== FRONTEND CONFIG ======
print("\n" + "=" * 70)
print("[FRONTEND SERVICE CONFIG]")
print("=" * 70)
print("  Web Admin:     http://localhost:3000 (Vite dev)")
print("  Miniapp H5:    http://localhost:5174 (Vite dev)")

# ====== JDZC-CRM Project ======
crm_root = r'E:\jdzc-crm'
if os.path.exists(crm_root):
    print("\n" + "=" * 70)
    print("[JDZC-CRM Project - E:\\jdzc-crm\\]")
    print("=" * 70)
    for item in sorted(os.listdir(crm_root)):
        full = os.path.join(crm_root, item)
        tag = "[DIR]" if os.path.isdir(full) else "[FILE]"
        print(f"  {tag} {item}")

# ====== Love Match System ======
love_root = r'E:\love-match-system'
if os.path.exists(love_root):
    print("\n" + "=" * 70)
    print("[Love Match System - E:\\love-match-system\\]")
    print("=" * 70)
    for item in sorted(os.listdir(love_root)):
        full = os.path.join(love_root, item)
        tag = "[DIR]" if os.path.isdir(full) else "[FILE]"
        print(f"  {tag} {item}")

print("\n[Done]")
