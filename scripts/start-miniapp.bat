@echo off
chcp 65001 >nul
cd /d D:\aksu-supervision\miniapp-h5
start /b D:\aksu-supervision\miniapp-h5\node_modules\.bin\vite.cmd --port 5174 --host 0.0.0.0
