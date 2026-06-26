$outLog = "D:\aksu-supervision\backend-out.log"
$errLog = "D:\aksu-supervision\backend-err.log"
# Clear old logs
if (Test-Path $outLog) { Remove-Item $outLog }
if (Test-Path $errLog) { Remove-Item $errLog }

$proc = Start-Process -FilePath 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe' `
    -ArgumentList '-jar','D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar' `
    -RedirectStandardOutput $outLog `
    -RedirectStandardError $errLog `
    -PassThru -WindowStyle Hidden

Write-Host "Started PID: $($proc.Id)"
Write-Host "Out log: $outLog"
Write-Host "Err log: $errLog"

# Wait and show log
Start-Sleep -Seconds 3
Write-Host ""
Write-Host "=== First 3 seconds stdout ==="
if (Test-Path $outLog) {
    Get-Content $outLog -Tail 50 | ForEach-Object { Write-Host $_ }
}
Write-Host ""
Write-Host "=== First 3 seconds stderr ==="
if (Test-Path $errLog) {
    Get-Content $errLog -Tail 50 | ForEach-Object { Write-Host $_ }
}

Start-Sleep -Seconds 5
Write-Host ""
Write-Host "=== After 8 seconds stdout ==="
if (Test-Path $outLog) {
    Get-Content $outLog -Tail 100 | ForEach-Object { Write-Host $_ }
}
Write-Host ""
Write-Host "=== After 8 seconds stderr ==="
if (Test-Path $errLog) {
    Get-Content $errLog -Tail 100 | ForEach-Object { Write-Host $_ }
}

Write-Host ""
Write-Host "Backend still running. Checking port..."
$conn = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($conn) {
    Write-Host "Port 8080 is LISTENING"
} else {
    Write-Host "Port 8080 is NOT listening"
}
