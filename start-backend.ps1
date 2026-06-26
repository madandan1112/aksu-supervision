$p = Start-Process -FilePath 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe' -ArgumentList '-jar','D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar' -PassThru -WindowStyle Hidden
Write-Host "Started PID: $($p.Id)"
Start-Sleep -Seconds 3
Write-Host "Backend started, checking health..."
try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/api/admin/system/org/tree' -Method GET -UseBasicParsing -TimeoutSec 5
    Write-Host "Status: $($r.StatusCode)"
    Write-Host "Body: $($r.Content | ConvertFrom-Json | ConvertTo-Json -Depth 2)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
