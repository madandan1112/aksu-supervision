Write-Host "=== Test Swagger UI ==="
try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/swagger-ui.html' -Method GET -UseBasicParsing -TimeoutSec 5
    Write-Host "Status: $($r.StatusCode)"
    Write-Host "Length: $($r.Content.Length)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { Write-Host "Status: $($_.Exception.Response.StatusCode)" }
}

Write-Host ""
Write-Host "=== Test health check ==="
try {
    $r2 = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -Method GET -UseBasicParsing -TimeoutSec 5
    Write-Host "Status: $($r2.StatusCode)"
    Write-Host "Body: $($r2.Content)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}

Write-Host ""
Write-Host "=== Test OPTIONS preflight ==="
try {
    $r3 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method OPTIONS -UseBasicParsing -TimeoutSec 5
    Write-Host "Status: $($r3.StatusCode)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { Write-Host "Status: $($_.Exception.Response.StatusCode)" }
}

Write-Host ""
Write-Host "=== Process status ==="
Get-Process java -ErrorAction SilentlyContinue | Select-Object Id, ProcessName, StartTime, @{Name='WorkingSetMB';Expression={[math]::Round($_.WorkingSet64/1MB,2)}}
