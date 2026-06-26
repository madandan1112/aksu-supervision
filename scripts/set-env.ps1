$env:JAVA_HOME = 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7'
$env:MAVEN_HOME = 'D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6'
$env:MYSQL_HOME = 'D:\aksu-supervision\dev-env\mysql'
$env:PATH = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:MYSQL_HOME\bin;$env:PATH"

Write-Host "Java:" 
& "$env:JAVA_HOME\bin\java.exe" -version 2>&1 | Select-Object -First 1
Write-Host "Maven:"
& "$env:MAVEN_HOME\bin\mvn.cmd" -version 2>&1 | Select-Object -First 1  
Write-Host "MySQL:"
& "$env:MYSQL_HOME\bin\mysql.exe" --version 2>&1
