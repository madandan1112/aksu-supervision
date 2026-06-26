try {
    $body = '{"username":"admin","password":"123456"}'
    Write-Host "Request body: $body"
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 10
    Write-Host "Status: $($r.StatusCode)"
    Write-Host "Body: $($r.Content)"
} catch {
    Write-Host "Exception type: $($_.Exception.GetType().FullName)"
    Write-Host "Message: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        $err = $reader.ReadToEnd()
        Write-Host "Error Body: $err"
    }
}
