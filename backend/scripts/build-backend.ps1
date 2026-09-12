$env:JAVA_HOME = "D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7"
$env:PATH = $env:JAVA_HOME + "\bin;" + $env:PATH
Set-Location "D:\aksu-supervision\backend"
$mvn = "D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6\bin\mvn.cmd"
& $mvn "clean" "package" "-DskipTests" "2>&1" | Tee-Object -FilePath "D:\aksu-supervision\backend\scripts\build.log"
Write-Host "Exit code: $LASTEXITCODE"
