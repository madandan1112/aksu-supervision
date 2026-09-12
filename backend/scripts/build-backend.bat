chcp 65001 >nul
set JAVA_HOME=D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7
set PATH=%JAVA_HOME%\bin;D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6\bin;%PATH%
cd /d D:\aksu-supervision\backend
mvn clean package -DskipTests
echo ExitCode=%ERRORLEVEL%
pause
