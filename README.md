# 阿克苏地区市场监督管理局 数字化监管与服务平台

## 项目概述

本平台为阿克苏地区市场监督管理局提供数字化监管服务，包含三个终端：

| 终端 | 技术栈 | 目录 | 说明 |
|------|--------|------|------|
| 企业端（微信小程序） | uni-app + Vue3 | `miniapp/` | 面向企业用户 |
| 监管端（安卓APP） | Kotlin + Jetpack Compose | `android-app/` | 面向执法人员 |
| 管理后台（Web） | Vue3 + Element Plus | `web-admin/` | 面向管理人员 |
| 后端服务 | Spring Boot 3 + Java 17 | `backend/` | 统一API服务 |

## 功能模块

### 企业端（微信小程序）
1. 诉求直达 - 诉求提交、跟踪、历史查询
2. 企业信息管理 - 基础信息维护、联系人管理
3. 检查整改响应 - 整改通知接收、反馈提交、验收结果查看
4. 合规报告管理 - 报告上传、管理、到期提醒
5. 特种设备监测 - 设备台账、状态上报、上报记录
6. 其他功能 - 消息中心、OCR识别

### 监管端（安卓APP）
1. 任务管理 - 任务接收、认领、延期、统计
2. 现场检查 - 标准化检查、证据采集(水印相机)、数据上传(离线支持)
3. 整改验收 - 反馈接收、AI辅助比对、验收操作
4. 企业信息查询 - 扫码/搜索、全景档案
5. 预警处理 - 预警接收、处置、统计
6. 其他功能 - 个人中心(生物识别)、消息通知、离线功能

### 管理后台（Web）
1. 诉求管理 - 自动分流、人工分配、跟踪、统计分析
2. 任务调度 - 任务创建(含双随机)、进度监控、统计分析
3. 数据管理 - 企业档案、报告审核、数据可视化(行业/区域/风险画像)
4. 系统管理 - 角色权限、用户管理、参数配置、操作日志

## 快速开始

### 1. 环境安装

以管理员身份运行 PowerShell：

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force
D:\aksu-supervision\scripts\setup-env.ps1
```

脚本会自动下载安装到 `D:\aksu-supervision\dev-env\` 下：
- JDK 17（已安装）
- Maven 3.9.6
- MySQL 8.0
- Redis
- MinIO
- Node.js 20

### 2. 启动后端

```bash
cd D:\aksu-supervision\backend
# 确保MySQL和Redis已启动
mvn spring-boot:run
```

后端启动后访问：
- API地址: http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui.html

### 3. 启动Web管理后台

```bash
cd D:\aksu-supervision\web-admin
npm install
npm run dev
```

访问: http://localhost:5173
默认账号: admin / admin123

### 4. 启动小程序开发

```bash
cd D:\aksu-supervision\miniapp
npm install
# 使用HBuilderX打开项目，运行到微信小程序模拟器
```

### 5. 安卓APP开发

使用 Android Studio 打开 `D:\aksu-supervision\android-app` 目录，等待 Gradle 同步完成后运行。

## 数据库

数据库初始化脚本: `backend/sql/init.sql`

核心数据表：
- `sys_user` / `sys_role` / `sys_permission` - 用户权限
- `enterprise` / `enterprise_contact` - 企业信息
- `appeal` / `appeal_process_log` - 诉求管理
- `inspection_task` / `task_enterprise` / `task_inspector` - 检查任务
- `inspection_record` - 现场检查记录
- `rectification_notice` / `rectification_feedback` / `acceptance_record` - 整改验收
- `compliance_report` - 合规报告
- `equipment_ledger` / `equipment_report` - 设备监测
- `alert` - 预警
- `message` - 消息通知
- `inspection_template` / `equipment_check_template` - 检查模板

## 技术架构

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  微信小程序      │  │  安卓APP         │  │  Web管理后台     │
│  (uni-app+Vue3) │  │  (Kotlin+Compose)│  │  (Vue3+Element) │
└────────┬────────┘  └────────┬────────┘  └────────┬────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   Spring Boot 3   │
                    │   (REST API)      │
                    └─────────┬─────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
       ┌──────▼──────┐ ┌─────▼──────┐ ┌──────▼──────┐
       │   MySQL 8   │ │   Redis    │ │   MinIO     │
       │   (数据存储) │ │  (缓存/会话)│ │ (文件存储)  │
       └─────────────┘ └────────────┘ └─────────────┘
```

## 项目目录结构

```
D:\aksu-supervision\
├── backend/          # Spring Boot后端
│   ├── pom.xml
│   ├── sql/          # 数据库脚本
│   └── src/          # Java源码
├── miniapp/          # 微信小程序
│   ├── package.json
│   └── src/          # Vue3源码
├── android-app/      # 安卓APP
│   ├── build.gradle.kts
│   └── app/          # Kotlin源码
├── web-admin/        # Web管理后台
│   ├── package.json
│   └── src/          # Vue3源码
├── scripts/          # 工具脚本
│   └── setup-env.ps1 # 环境安装脚本
├── dev-env/          # 开发环境(安装后)
└── docs/             # 文档
```
