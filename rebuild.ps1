$env:JAVA_HOME = 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7'
$env:Path = $env:JAVA_HOME + '\bin;' + $env:Path

Write-Host "=== Cleaning old jar ==="
$jar = 'D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar'
if (Test-Path $jar) {
    Remove-Item $jar -Force
    Write-Host "Removed old jar"
}

Write-Host ""
Write-Host "=== Building with Maven ==="
& 'D:\aksu-supervision\dev-env\maven\apache-maven-3.9.6\bin\mvn.cmd' -f 'D:\aksu-supervision\backend\pom.xml' clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "BUILD FAILED"
    exit 1
}

Write-Host ""
Write-Host "=== Starting backend ==="
$proc = Start-Process -FilePath 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe' -ArgumentList '-jar','D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar' -PassThru -WindowStyle Hidden
Write-Host "Started PID: $($proc.Id)"

Write-Host ""
Write-Host "=== Waiting 10 seconds for startup ==="
Start-Sleep -Seconds 10

# Test login API
Write-Host ""
Write-Host "=== Testing Login API (without captcha - should get 400) ==="
try {
    $body = '{"username":"admin","password":"123456"}'
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 5
    Write-Host "Status: $($r.StatusCode)"
    Write-Host "Body: $($r.Content)"
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode)"
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $reader.BaseStream.Position = 0
    $reader.DiscardBufferedData()
    $err = $reader.ReadToEnd()
    Write-Host "Body: $err"
}

Write-Host ""
Write-Host "=== Backend ready ==="
