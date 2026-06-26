$ErrorActionPreference = 'Stop'

# Step 1: Get captcha
Write-Host '=== Step 1: Get Captcha ==='
$captcha = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/captcha' -Method GET -UseBasicParsing
Write-Host ('Captcha Status: ' + $captcha.StatusCode)
$captchaJson = $captcha.Content | ConvertFrom-Json
$captchaKey = $captchaJson.data.key
$captchaCode = $captchaJson.data.code
Write-Host ('Captcha Key: ' + $captchaKey)
Write-Host ('Captcha Code: ' + $captchaCode)

# Step 2: Login with captcha
Write-Host ''
Write-Host '=== Step 2: Login ==='
$body = '{"username":"admin","password":"123456","captchaKey":"' + $captchaKey + '","captchaCode":"' + $captchaCode + '"}'
Write-Host ('Request body: ' + $body)
$login = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
Write-Host ('Login Status: ' + $login.StatusCode)
$loginJson = $login.Content | ConvertFrom-Json
$token = $loginJson.data.token
Write-Host ('Token: ' + $token.Substring(0, 20) + '...')

# Step 3: Test org/tree
Write-Host ''
Write-Host '=== Step 3: Test /api/admin/system/org/tree ==='
$headers = @{ Authorization = ('Bearer ' + $token) }
$tree = Invoke-WebRequest -Uri 'http://localhost:8080/api/admin/system/org/tree' -Method GET -Headers $headers -UseBasicParsing
Write-Host ('Tree Status: ' + $tree.StatusCode)
$treeJson = $tree.Content | ConvertFrom-Json
Write-Host ('Tree Data: ' + ($treeJson.data | ConvertTo-Json -Depth 2))

# Step 4: Test org/list
Write-Host ''
Write-Host '=== Step 4: Test /api/admin/system/org/list ==='
$list = Invoke-WebRequest -Uri 'http://localhost:8080/api/admin/system/org/list' -Method GET -Headers $headers -UseBasicParsing
Write-Host ('List Status: ' + $list.StatusCode)
$listJson = $list.Content | ConvertFrom-Json
Write-Host ('List Count: ' + ($listJson.data | Measure-Object).Count)

Write-Host ''
Write-Host '=== ALL BACKEND APIs WORKING ==='
