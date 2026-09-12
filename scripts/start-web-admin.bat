@echo off
chcp 65001 >nul
cd /d D:\aksu-supervision\web-admin
start /b D:\aksu-supervision\web-admin\node_modules\.bin\vite.cmd --port 3000 --host 0.0.0.0
