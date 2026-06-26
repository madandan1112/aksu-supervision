# ============================================================
# setup-env.ps1 — 阿克苏监督平台开发环境一键安装脚本
# 目标目录: D:\aksu-supervision\dev-env\
# ============================================================

$ErrorActionPreference = "Stop"

# -------------------- 基础变量 --------------------
$BaseDir    = "D:\aksu-supervision\dev-env"
$ScriptsDir = "D:\aksu-supervision\scripts"
$SqlFile    = "D:\aksu-supervision\backend\sql\init.sql"

$MavenDir   = "$BaseDir\maven"
$MysqlDir   = "$BaseDir\mysql"
$RedisDir   = "$BaseDir\redis"
$MinioDir   = "$BaseDir\minio"
$NodejsDir  = "$BaseDir\nodejs"
$JavaHome   = "$BaseDir\jdk-17\jdk-17.0.12+7"

# 下载源（可按网络情况替换为镜像）
$MavenUrl   = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
$MysqlUrl   = "https://cdn.mysql.com/archives/mysql-8.0/mysql-8.0.39-winx64.zip"
$RedisUrl   = "https://github.com/tporadowski/redis/releases/download/v5.0.14.1/Redis-x64-5.0.14.1.zip"
$MinioUrl   = "https://dl.min.io/server/minio/release/windows-amd64/minio.exe"
$NodejsUrl  = "https://nodejs.org/dist/v20.11.0/node-v20.11.0-win-x64.zip"

# -------------------- 工具函数 --------------------

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "  $Message" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
}

function Write-Info {
    param([string]$Message)
    Write-Host "  [INFO] $Message" -ForegroundColor Green
}

function Write-Warn {
    param([string]$Message)
    Write-Host "  [WARN] $Message" -ForegroundColor Yellow
}

function Download-File {
    param(
        [string]$Url,
        [string]$OutFile
    )
    if (Test-Path $OutFile) {
        Write-Info "已存在，跳过下载: $OutFile"
        return
    }
    Write-Info "下载: $Url"
    Write-Info "保存: $OutFile"
    curl.exe -fSL -o $OutFile $Url
    if (-not (Test-Path $OutFile)) {
        throw "下载失败: $Url"
    }
    Write-Info "下载完成"
}

function Expand-ZipSafe {
    param(
        [string]$ZipPath,
        [string]$DestDir
    )
    # $DestDir 是最终目标目录（如 D:\...\maven），需要确认其是否存在
    if (Test-Path $DestDir) {
        Write-Info "目录已存在，跳过解压: $DestDir"
        return
    }
    Write-Info "解压: $ZipPath -> $DestDir"
    # 先解压到临时目录，再移动
    $TempExtract = "$DestDir__temp_extract"
    if (Test-Path $TempExtract) { Remove-Item $TempExtract -Recurse -Force }
    Expand-Archive -Path $ZipPath -DestinationPath $TempExtract -Force
    # zip 内通常有一层顶层目录（如 apache-maven-3.9.6），找到它并移动内容
    $topItems = Get-ChildItem $TempExtract
    if ($topItems.Count -eq 1 -and $topItems[0].PSIsContainer) {
        # 单一顶层目录，将其重命名为目标目录
        Move-Item $topItems[0].FullName $DestDir
        Remove-Item $TempExtract -Force
    } else {
        # 多个顶层项，直接重命名临时目录
        Move-Item $TempExtract $DestDir
    }
    Write-Info "解压完成: $DestDir"
}

# -------------------- 前置检查 --------------------

Write-Step "前置检查"

# 检查 JDK17 目录
if (-not (Test-Path $JavaHome)) {
    throw "JDK17 目录不存在: $JavaHome，请先安装 JDK17"
}
Write-Info "JDK17 目录确认: $JavaHome"

# 检查 SQL 文件
if (-not (Test-Path $SqlFile)) {
    Write-Warn "SQL 初始化文件不存在: $SqlFile（MySQL 初始化步骤将跳过执行 SQL）"
}

# 创建基础目录
if (-not (Test-Path $BaseDir)) {
    New-Item -Path $BaseDir -ItemType Directory -Force | Out-Null
    Write-Info "创建基础目录: $BaseDir"
}

# 创建下载临时目录
$DownloadDir = "$BaseDir\__downloads"
if (-not (Test-Path $DownloadDir)) {
    New-Item -Path $DownloadDir -ItemType Directory -Force | Out-Null
}

# -------------------- 1. 下载并安装各软件 --------------------

# ---------- Apache Maven 3.9.6 ----------
Write-Step "安装 Apache Maven 3.9.6"

$MavenZip = "$DownloadDir\apache-maven-3.9.6-bin.zip"
Download-File -Url $MavenUrl -OutFile $MavenZip
Expand-ZipSafe -ZipPath $MavenZip -DestDir $MavenDir
Write-Info "Maven 安装完成: $MavenDir"

# ---------- MySQL 8.0 (ZIP 免安装版) ----------
Write-Step "安装 MySQL 8.0 (ZIP 免安装版)"

$MysqlZip = "$DownloadDir\mysql-8.0.39-winx64.zip"
Download-File -Url $MysqlUrl -OutFile $MysqlZip
Expand-ZipSafe -ZipPath $MysqlZip -DestDir $MysqlDir
Write-Info "MySQL 安装完成: $MysqlDir"

# 创建 MySQL my.ini 配置文件
$MyIni = "$MysqlDir\my.ini"
if (-not (Test-Path $MyIni)) {
    $myIniContent = @"
[mysqld]
basedir=$MysqlDir
datadir=$MysqlDir\data
port=3306
max_connections=200
max_connect_errors=10
character-set-server=utf8mb4
collation-server=utf8mb4_general_ci
default_authentication_plugin=mysql_native_password
default-storage-engine=INNODB
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION

[mysql]
default-character-set=utf8mb4

[client]
port=3306
default-character-set=utf8mb4
"@
    Set-Content -Path $MyIni -Value $myIniContent -Encoding UTF8
    Write-Info "已创建 MySQL 配置文件: $MyIni"
}

# ---------- Redis for Windows ----------
Write-Step "安装 Redis for Windows"

$RedisZip = "$DownloadDir\Redis-x64-5.0.14.1.zip"
Download-File -Url $RedisUrl -OutFile $RedisZip
Expand-ZipSafe -ZipPath $RedisZip -DestDir $RedisDir
Write-Info "Redis 安装完成: $RedisDir"

# ---------- MinIO ----------
Write-Step "安装 MinIO"

$MinioExe = "$MinioDir\minio.exe"
if (-not (Test-Path $MinioExe)) {
    if (-not (Test-Path $MinioDir)) {
        New-Item -Path $MinioDir -ItemType Directory -Force | Out-Null
    }
    Download-File -Url $MinioUrl -OutFile $MinioExe
}
Write-Info "MinIO 安装完成: $MinioExe"

# ---------- Node.js 20 LTS ----------
Write-Step "安装 Node.js 20 LTS"

$NodejsZip = "$DownloadDir\node-v20.11.0-win-x64.zip"
Download-File -Url $NodejsUrl -OutFile $NodejsZip
Expand-ZipSafe -ZipPath $NodejsZip -DestDir $NodejsDir
Write-Info "Node.js 安装完成: $NodejsDir"

# -------------------- 2. 配置环境变量 --------------------

Write-Step "配置系统环境变量"

# 设置 JAVA_HOME
$CurrentJavaHome = [Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
if ($CurrentJavaHome -ne $JavaHome) {
    Write-Info "设置 JAVA_HOME = $JavaHome"
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $JavaHome, "Machine")
} else {
    Write-Info "JAVA_HOME 已正确设置"
}

# 构建需要添加到 PATH 的目录列表
$PathsToAdd = @(
    "$MavenDir\bin",
    "$MysqlDir\bin",
    "$RedisDir",
    "$MinioDir",
    "$NodejsDir",
    $JavaHome
)

# 获取当前系统 PATH
$CurrentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
$PathParts = $CurrentPath -split ";" | Where-Object { $_ -ne "" }

$Modified = $false
foreach ($P in $PathsToAdd) {
    $NormalizedP = $P.TrimEnd("\")
    $AlreadyInPath = $PathParts | Where-Object { $_.TrimEnd("\") -ieq $NormalizedP }
    if (-not $AlreadyInPath) {
        Write-Info "添加到 PATH: $P"
        $PathParts += $P
        $Modified = $true
    } else {
        Write-Info "已在 PATH 中: $P"
    }
}

if ($Modified) {
    $NewPath = $PathParts -join ";"
    [Environment]::SetEnvironmentVariable("Path", $NewPath, "Machine")
    Write-Info "系统 PATH 已更新"
}

# 刷新当前会话的环境变量
$env:JAVA_HOME = $JavaHome
$env:Path = [Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [Environment]::GetEnvironmentVariable("Path", "User")

# -------------------- 3. 初始化 MySQL --------------------

Write-Step "初始化 MySQL 8.0"

$Mysqld = "$MysqlDir\bin\mysqld.exe"
$Mysql   = "$MysqlDir\bin\mysql.exe"
$MysqlData = "$MysqlDir\data"

# 3.1 创建 data 目录
if (-not (Test-Path $MysqlData)) {
    Write-Info "创建 MySQL data 目录: $MysqlData"
    New-Item -Path $MysqlData -ItemType Directory -Force | Out-Null
} else {
    Write-Info "MySQL data 目录已存在"
}

# 3.2 初始化（无密码模式）
$DataFiles = Get-ChildItem $MysqlData -ErrorAction SilentlyContinue
if ($DataFiles.Count -eq 0) {
    Write-Info "执行 mysqld --initialize-insecure ..."
    & $Mysqld --initialize-insecure --console 2>&1 | ForEach-Object { Write-Host "  $_" }
    if ($LASTEXITCODE -ne 0) {
        throw "MySQL 初始化失败 (exit code: $LASTEXITCODE)"
    }
    Write-Info "MySQL 初始化完成"
} else {
    Write-Info "MySQL data 目录非空，跳过初始化"
}

# 3.3 安装为 Windows 服务
$MysqlService = Get-Service -Name "MySQL80" -ErrorAction SilentlyContinue
if (-not $MysqlService) {
    Write-Info "安装 MySQL Windows 服务..."
    & $Mysqld --install MySQL80
    if ($LASTEXITCODE -ne 0) {
        throw "MySQL 服务安装失败 (exit code: $LASTEXITCODE)"
    }
    Write-Info "MySQL 服务已安装 (服务名: MySQL80)"
} else {
    Write-Info "MySQL 服务已存在 (状态: $($MysqlService.Status))"
}

# 启动 MySQL 服务
if ($MysqlService.Status -ne "Running") {
    Write-Info "启动 MySQL 服务..."
    Start-Service -Name "MySQL80"
    # 等待服务就绪
    $RetryCount = 0
    while ($RetryCount -lt 30) {
        Start-Sleep -Seconds 1
        $Svc = Get-Service -Name "MySQL80"
        if ($Svc.Status -eq "Running") { break }
        $RetryCount++
    }
    if ((Get-Service -Name "MySQL80").Status -ne "Running") {
        throw "MySQL 服务启动超时"
    }
    Write-Info "MySQL 服务已启动"
} else {
    Write-Info "MySQL 服务已在运行"
}

# 等待 MySQL 完全就绪
Write-Info "等待 MySQL 就绪..."
Start-Sleep -Seconds 5

# 3.4 创建数据库
Write-Info "创建数据库 aksu_supervision ..."
$CreateDbCmd = "CREATE DATABASE IF NOT EXISTS aksu_supervision DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
& $Mysql -u root -e $CreateDbCmd 2>&1 | ForEach-Object { Write-Host "  $_" }
if ($LASTEXITCODE -ne 0) {
    throw "创建数据库失败 (exit code: $LASTEXITCODE)"
}
Write-Info "数据库 aksu_supervision 创建成功"

# 3.5 执行 init.sql
if (Test-Path $SqlFile) {
    Write-Info "执行 SQL 初始化脚本: $SqlFile"
    & $Mysql -u root aksu_supervision -e "source $SqlFile" 2>&1 | ForEach-Object { Write-Host "  $_" }
    if ($LASTEXITCODE -ne 0) {
        throw "执行 SQL 脚本失败 (exit code: $LASTEXITCODE)"
    }
    Write-Info "SQL 初始化脚本执行完成"
} else {
    Write-Warn "SQL 初始化文件不存在，跳过: $SqlFile"
}

# -------------------- 4. 初始化 Redis（安装为 Windows 服务）--------------------

Write-Step "初始化 Redis"

$RedisServerExe = "$RedisDir\redis-server.exe"
$RedisCliExe    = "$RedisDir\redis-cli.exe"

$RedisService = Get-Service -Name "Redis" -ErrorAction SilentlyContinue
if (-not $RedisService) {
    Write-Info "安装 Redis Windows 服务..."
    & $RedisServerExe --service-install "$RedisDir\redis.windows-service.conf" --service-name Redis --port 6379
    if ($LASTEXITCODE -ne 0) {
        # 如果服务安装失败，尝试直接使用 redis-server.exe 注册
        Write-Warn "使用 redis.windows-service.conf 安装失败，尝试使用 redis.windows.conf ..."
        & $RedisServerExe --service-install "$RedisDir\redis.windows.conf" --service-name Redis --port 6379
        if ($LASTEXITCODE -ne 0) {
            throw "Redis 服务安装失败 (exit code: $LASTEXITCODE)"
        }
    }
    Write-Info "Redis 服务已安装 (服务名: Redis)"
} else {
    Write-Info "Redis 服务已存在 (状态: $($RedisService.Status))"
}

# 启动 Redis 服务
if ((Get-Service -Name "Redis" -ErrorAction SilentlyContinue).Status -ne "Running") {
    Write-Info "启动 Redis 服务..."
    Start-Service -Name "Redis"
    Start-Sleep -Seconds 2
    if ((Get-Service -Name "Redis").Status -ne "Running") {
        throw "Redis 服务启动失败"
    }
    Write-Info "Redis 服务已启动"
} else {
    Write-Info "Redis 服务已在运行"
}

# -------------------- 5. 创建 MinIO data 目录 --------------------

Write-Step "初始化 MinIO"

$MinioDataDir = "$MinioDir\data"
if (-not (Test-Path $MinioDataDir)) {
    New-Item -Path $MinioDataDir -ItemType Directory -Force | Out-Null
    Write-Info "创建 MinIO data 目录: $MinioDataDir"
} else {
    Write-Info "MinIO data 目录已存在"
}

Write-Info "MinIO 初始化完成（MinIO 需手动启动，命令如下）:"
Write-Info "  $MinioDir\minio.exe server $MinioDataDir --console-address :9001"

# -------------------- 6. 版本验证 --------------------

Write-Step "版本验证"

Write-Host ""
Write-Host "---------- 版本信息 ----------" -ForegroundColor White

# JDK
try {
    $JdkVer = & "$JavaHome\bin\java.exe" -version 2>&1 | Select-Object -First 1
    Write-Info "JDK:       $JdkVer"
} catch {
    Write-Warn "JDK:       验证失败"
}

# Maven
try {
    $MvnVer = & "$MavenDir\bin\mvn.cmd" -version 2>&1 | Select-Object -First 1
    Write-Info "Maven:     $MvnVer"
} catch {
    Write-Warn "Maven:     验证失败"
}

# MySQL
try {
    $MysqlVer = & "$MysqlDir\bin\mysql.exe" -V 2>&1
    Write-Info "MySQL:     $MysqlVer"
} catch {
    Write-Warn "MySQL:     验证失败"
}

# Redis
try {
    $RedisVer = & "$RedisDir\redis-server.exe" --version 2>&1
    Write-Info "Redis:     $RedisVer"
} catch {
    Write-Warn "Redis:     验证失败"
}

# MinIO
try {
    $MinioVer = & "$MinioDir\minio.exe" --version 2>&1
    Write-Info "MinIO:     $MinioVer"
} catch {
    Write-Warn "MinIO:     验证失败"
}

# Node.js
try {
    $NodeVer = & "$NodejsDir\node.exe" --version 2>&1
    Write-Info "Node.js:   $NodeVer"
} catch {
    Write-Warn "Node.js:   验证失败"
}

# npm
try {
    $NpmVer = & "$NodejsDir\npm.cmd" --version 2>&1
    Write-Info "npm:       $NpmVer"
} catch {
    Write-Warn "npm:       验证失败"
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  所有环境安装和配置完成！" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
Write-Host "  MySQL 服务名: MySQL80    (端口 3306)" -ForegroundColor White
Write-Host "  Redis 服务名: Redis      (端口 6379)" -ForegroundColor White
Write-Host "  MinIO 需手动启动，命令:" -ForegroundColor White
Write-Host "    $MinioDir\minio.exe server $MinioDataDir --console-address :9001" -ForegroundColor Yellow
Write-Host ""
Write-Host "  数据库: aksu_supervision (utf8mb4)" -ForegroundColor White
Write-Host ""
Write-Host "  请重新打开终端以使环境变量生效" -ForegroundColor Yellow
Write-Host ""
