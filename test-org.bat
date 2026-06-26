@echo off
chcp 65001 >nul
echo 停止旧进程...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo 启动后端...
start /B "" "D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe" -jar "D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar"
timeout /t 8 /nobreak >nul

echo 测试登录...
curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"123456\"}" -o D:\aksu-supervision\login.json
type D:\aksu-supervision\login.json
echo.

echo 测试组织架构树...
curl -s -X GET http://localhost:8080/api/admin/system/org/tree -H "Authorization: Bearer test-token" -o D:\aksu-supervision\org-tree.json
type D:\aksu-supervision\org-tree.json
echo.

echo 测试组织架构列表...
curl -s -X GET http://localhost:8080/api/admin/system/org/list -H "Authorization: Bearer test-token" -o D:\aksu-supervision\org-list.json
type D:\aksu-supervision\org-list.json
echo.

echo 完成测试
taskkill /F /IM java.exe 2>nul
