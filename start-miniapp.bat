@echo off
chcp 65001 >nul
cd /d D:\aksu-supervision\miniapp-h5
D:\aksu-supervision\dev-env\node\node.exe node_modules\vite\bin\vite.js --port 5174 --host
