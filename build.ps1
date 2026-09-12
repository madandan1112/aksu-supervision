$env:JAVA_HOME = 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7'
& 'D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6\bin\mvn.cmd' -f 'D:\aksu-supervision\backend\pom.xml' clean package -DskipTests 2>&1 | Select-Object -Last 30
