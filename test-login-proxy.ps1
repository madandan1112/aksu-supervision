# Test login through frontend proxy (port 3000)
$ErrorActionPreference = 'Continue'

Write-Host "=== Test 1: Get Captcha through proxy ==="
try {
    $captcha = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/captcha' -Method GET -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($captcha.StatusCode)"
    $json = $captcha.Content | ConvertFrom-Json
    Write-Host "Key: $($json.data.captchaKey)"
    Write-Host "Image length: $($json.data.captchaImage.Length)"
    $key = $json.data.captchaKey
    $code = $json.data.captchaCode  # This field may not exist
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) { Write-Host "Status: $($_.Exception.Response.StatusCode)" }
    $key = ""
    $code = ""
}

Write-Host ""
Write-Host "=== Test 2: Login through proxy ==="
# First, get a fresh captcha from backend directly
Write-Host "Getting fresh captcha from backend directly..."
try {
    $captcha2 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/captcha' -Method GET -UseBasicParsing -TimeoutSec 10
    $json2 = $captcha2.Content | ConvertFrom-Json
    $key = $json2.data.captchaKey
    # Extract code from image? No, we need to know the code
    # The captcha API only returns key and image, not code
    Write-Host "Got key: $key"
    Write-Host "WARNING: We don't know the captcha code from API response. Login will fail with 400."
} catch {
    Write-Host "Error getting captcha: $($_.Exception.Message)"
}

# Try login with dummy values to see what error we get
$body = "{`"username`":`"admin`",`"password`":`"123456`",`"captchaKey`":`"$key`",`"captchaCode`":`"WRONG`"}"
Write-Host "Login body: $body"
try {
    $login = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
    Write-Host "Login Status: $($login.StatusCode)"
    Write-Host "Login Body: $($login.Content)"
} catch {
    Write-Host "Login Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Login Status: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        $errBody = $reader.ReadToEnd()
        Write-Host "Error Body: $errBody"
    }
}

# Test with no captcha fields to see what happens
Write-Host ""
Write-Host "=== Test 3: Login without captcha fields ==="
$body2 = '{"username":"admin","password":"123456"}'
try {
    $login2 = Invoke-WebRequest -Uri 'http://localhost:3000/api/auth/login' -Method POST -Body $body2 -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($login2.StatusCode)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        $errBody2 = $reader.ReadToEnd()
        Write-Host "Error Body: $errBody2"
    }
}
