chcp 65001 >nul
set JAVA_HOME=D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7
set PATH=%JAVA_HOME%\bin;D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6\bin;%PATH%
cd /d D:\aksu-supervision\backend
call mvn clean package -DskipTests 2>&1
set EXITCODE=%ERRORLEVEL%
echo Maven exit code: %EXITCODE%

if %EXITCODE% neq 0 (
  echo BUILD FAILED
  exit /b %EXITCODE%
)

if exist target\supervision-0.0.1-SNAPSHOT.jar (
  echo BUILD SUCCESS
  start /min D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe -jar D:\aksu-supervision\backend\target\supervision-0.0.1-SNAPSHOT.jar --server.port=8080
  echo Server started
) else (
  echo JAR not found!
)
