# Build and Start Backend
$JAVA_HOME = "D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7"
$MAVEN_HOME = "D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6"
$PROJECT = "D:\aksu-supervision\backend"
$JAR = "$PROJECT\target\supervision-0.0.1-SNAPSHOT.jar"

$env:JAVA_HOME = $JAVA_HOME
$env:PATH = "$JAVA_HOME\bin;$MAVEN_HOME\bin;" + $env:PATH

Write-Host "=== Building backend... ==="
Set-Location $PROJECT
$proc = Start-Process -FilePath "$MAVEN_HOME\bin\mvn.cmd" -ArgumentList "clean", "package", "-DskipTests", "-q" -Wait -PassThru -NoNewWindow
if ($proc.ExitCode -ne 0) {
    Write-Error "Build failed"
    exit 1
}

if (-not (Test-Path $JAR)) {
    Write-Error "JAR not found: $JAR"
    exit 1
}

Write-Host "=== Starting backend on port 8080... ==="
$java = "$JAVA_HOME\bin\java.exe"
$args = "-jar", $JAR, "--server.port=8080"
$env:SERVER_PORT = "8080"
Start-Process -FilePath $java -ArgumentList $args -WindowStyle Hidden
Write-Host "Backend started"
Start-Sleep 2
