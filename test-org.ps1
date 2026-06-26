# Kill existing Java processes
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Start backend
$proc = Start-Process -FilePath 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe' -ArgumentList '-jar','D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar' -PassThru -WindowStyle Hidden
Write-Host "Started backend PID: $($proc.Id)"
Start-Sleep -Seconds 8

# Login to get token
Write-Host "=== Login ==="
try {
    $body = '{"username":"admin","password":"123456"}'
    $login = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
    Write-Host "Login Status: $($login.StatusCode)"
    Write-Host "Login Body: $($login.Content)"

    $token = ($login.Content | ConvertFrom-Json).data.token
    Write-Host "Token: $($token.Substring(0,20))..."

    # Test org/tree with token
    Write-Host ""
    Write-Host "=== Test /api/admin/system/org/tree ==="
    $headers = @{ Authorization = "Bearer $token" }
    $tree = Invoke-WebRequest -Uri 'http://localhost:8080/api/admin/system/org/tree' -Method GET -Headers $headers -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($tree.StatusCode)"
    $data = $tree.Content | ConvertFrom-Json
    Write-Host "Response: $($data | ConvertTo-Json -Depth 3)"

    # Test org/list
    Write-Host ""
    Write-Host "=== Test /api/admin/system/org/list ==="
    $list = Invoke-WebRequest -Uri 'http://localhost:8080/api/admin/system/org/list' -Method GET -Headers $headers -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($list.StatusCode)"
    Write-Host "Count: $((($list.Content | ConvertFrom-Json).data | Measure-Object).Count) items"

    Write-Host ""
    Write-Host "=== ALL TESTS PASSED ==="

} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        $err = $reader.ReadToEnd()
        Write-Host "Body: $err"
    }
}

# Keep process running
Write-Host "Backend running. Press Enter to stop..."
Read-Host
Stop-Process -Id $proc.Id -Force
