$outLog = "D:\aksu-supervision\backend-out2.log"
$errLog = "D:\aksu-supervision\backend-err2.log"
if (Test-Path $outLog) { Remove-Item $outLog }
if (Test-Path $errLog) { Remove-Item $errLog }

$proc = Start-Process -FilePath 'D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe' `
    -ArgumentList '-jar','D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar' `
    -RedirectStandardOutput $outLog `
    -RedirectStandardError $errLog `
    -PassThru -WindowStyle Hidden

Write-Host "Started PID: $($proc.Id)"
Write-Host "Waiting 8 seconds for startup..."
Start-Sleep -Seconds 8

# Get captcha
Write-Host ""
Write-Host "=== Getting Captcha ==="
try {
    $captcha = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/captcha' -Method GET -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($captcha.StatusCode)"
    $json = $captcha.Content | ConvertFrom-Json
    $key = $json.data.captchaKey
    Write-Host "Key: $key"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    $key = ""
}

# Wait a moment for log to flush
Start-Sleep -Seconds 2

# Read code from log
Write-Host ""
Write-Host "=== Reading code from log ==="
if (Test-Path $outLog) {
    $lines = Get-Content $outLog
    $codeLine = $lines | Select-String '生成验证码' | Select-Object -Last 1
    if ($codeLine) {
        Write-Host "Log line: $codeLine"
        # Extract code from line like: code=HHKN
        if ($codeLine -match 'code=([A-Z0-9]+)') {
            $code = $Matches[1]
            Write-Host "Extracted code: $code"
            
            # Test login with correct code
            Write-Host ""
            Write-Host "=== Testing Login with correct code ==="
            $body = "{`"username`":`"admin`",`"password`":`"123456`",`"captchaKey`":`"$key`",`"captchaCode`":`"$code`"}"
            try {
                $login = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
                Write-Host "Status: $($login.StatusCode)"
                Write-Host "Body: $($login.Content)"
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
        } else {
            Write-Host "Could not extract code from log"
        }
    } else {
        Write-Host "No captcha log found"
        Write-Host "Last 20 log lines:"
        $lines | Select-Object -Last 20 | ForEach-Object { Write-Host $_ }
    }
} else {
    Write-Host "Log file not found"
}

Write-Host ""
Write-Host "=== Done ==="
