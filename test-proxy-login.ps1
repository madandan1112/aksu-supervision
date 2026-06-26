$ErrorActionPreference = 'Continue'

Write-Host "=== Test 1: Get Captcha via Proxy (port 3000) ==="
try {
    $captcha = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/captcha' -Method GET -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($captcha.StatusCode)"
    $json = $captcha.Content | ConvertFrom-Json
    $key = $json.data.captchaKey
    Write-Host "Key: $key"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { Write-Host "Status: $($_.Exception.Response.StatusCode)" }
    $key = ""
}

Write-Host ""
Write-Host "=== Test 2: Login via Proxy (port 3000) with correct captcha ==="
if ($key -ne "") {
    # We can't know the captcha code from API, but let's try without captcha first
    $body = '{"username":"admin","password":"123456"}'
    try {
        $login = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
        Write-Host "Status: $($login.StatusCode)"
        Write-Host "Body: $($login.Content)"
    } catch {
        Write-Host "Error: $($_.Exception.Message)"
        if ($_.Exception.Response) {
            Write-Host "Status: $($_.Exception.Response.StatusCode)"
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $reader.BaseStream.Position = 0
            $reader.DiscardBufferedData()
            $err = $reader.ReadToEnd()
            Write-Host "Error Body: $err"
        }
    }
}

Write-Host ""
Write-Host "=== Test 3: Direct backend (port 8080) ==="
try {
    $direct = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/captcha' -Method GET -UseBasicParsing -TimeoutSec 10
    Write-Host "Direct Status: $($direct.StatusCode)"
} catch {
    Write-Host "Direct Error: $($_.Exception.Message)"
}
