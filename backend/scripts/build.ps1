$env:JAVA_HOME = "D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7"
$env:PATH = $env:JAVA_HOME + "\bin;D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6\bin;" + $env:PATH
Set-Location "D:\aksu-supervision\backend"
$mvnPath = "D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6\bin\mvn.cmd"
Write-Host "JAVA_HOME: $env:JAVA_HOME"
Write-Host "Maven: $mvnPath"
Write-Host "Starting build..."
& $mvnPath "clean" "package" "-DskipTests" "2>&1"
$exitCode = $LASTEXITCODE
Write-Host "Exit code: $exitCode"
