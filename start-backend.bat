@echo off
cd /d D:\aksu-supervision
start "" /MIN D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe -jar backend\target\aksu-supervision-backend-1.0.0.jar > backend.log 2>&1
