$ErrorActionPreference = 'Continue'

Write-Host "=== 1. Test Captcha (via proxy port 3000) ==="
try {
    $captcha = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/captcha' -Method GET -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($captcha.StatusCode)"
    $json = $captcha.Content | ConvertFrom-Json
    $key = $json.data.captchaKey
    Write-Host "Key: $key"
    Write-Host "Image starts with: $($json.data.captchaImage.Substring(0, 30))..."
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { 
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        Write-Host "Body: $($reader.ReadToEnd())"
    }
    $key = ""
}

Write-Host ""
Write-Host "=== 2. Test Login without captcha (via proxy) ==="
try {
    $body = '{"username":"admin","password":"123456"}'
    $login = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($login.StatusCode)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { 
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        Write-Host "Body: $($reader.ReadToEnd())"
    }
}

Write-Host ""
Write-Host "=== 3. Test Login with wrong captcha ==="
if ($key -ne "") {
    $body = "{`"username`":`"admin`",`"password`":`"123456`",`"captchaKey`":`"$key`",`"captchaCode`":`"WRONG`"}"
    try {
        $login = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
        Write-Host "Status: $($login.StatusCode)"
    } catch {
        Write-Host "Error: $($_.Exception.Message)"
        if ($_.Exception.Response) { 
            Write-Host "Status: $($_.Exception.Response.StatusCode)"
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $reader.BaseStream.Position = 0
            $reader.DiscardBufferedData()
            Write-Host "Body: $($reader.ReadToEnd())"
        }
    }
}

Write-Host ""
Write-Host "=== 4. Test Dashboard API (no auth) ==="
try {
    $dash = Invoke-WebRequest -Uri 'http://localhost:3000/api/admin/dashboard/stats' -Method GET -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($dash.StatusCode)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { Write-Host "Status: $($_.Exception.Response.StatusCode)" }
}

Write-Host ""
Write-Host "=== 5. Test Org Tree (no auth) ==="
try {
    $org = Invoke-WebRequest -Uri 'http://localhost:3000/api/admin/system/org/tree' -Method GET -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($org.StatusCode)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { Write-Host "Status: $($_.Exception.Response.StatusCode)" }
}

Write-Host ""
Write-Host "=== Tests Complete ==="
