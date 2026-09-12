@echo off
chcp 65001 >nul
cd /d D:\aksu-supervision\web-admin
D:\aksu-supervision\dev-env\node\node.exe D:\aksu-supervision\web-admin\node_modules\vite\bin\vite.js --port 3000 --host
