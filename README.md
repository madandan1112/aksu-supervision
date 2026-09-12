# 阿克苏地区市场监督管理局 数字化监管与服务平台

## 项目概述

本平台为阿克苏地区市场监督管理局提供数字化监管服务。**采用"一个微信小程序、双角色入口"架构**：商户（企业用户）与执法人员在同一个小程序中按身份进入各自的界面，另有 Web 管理后台与公众扫码公示页。

| 终端 | 技术栈 | 目录 | 说明 |
|------|--------|------|------|
| 微信小程序（企业 + 执法双角色） | uni-app + Vue3 | `miniapp/` | 登录选身份：企业=品牌蓝 / 执法=市监红 |
| 小程序 H5 版（同款双角色） | Vue3 + Vite SPA | `miniapp-h5/` | 浏览器/内网可用的同源实现，含公众公示页 |
| 管理后台（Web） | Vue3 + Element Plus | `web-admin/` | 市监红主题，面向管理人员 |
| 公众扫码公示 H5 | `miniapp-h5` 内公开路由 | `/public/credit/:creditCode` | 免登录，扫码即看企业信用公示 |
| 后端服务 | Spring Boot 3 + Java 17 | `backend/` | 统一 API（147 接口）+ MySQL + Redis + MinIO |

> 安卓 APP 方案已废弃，执法能力全部并入小程序（架构决策，需业主书面确认合同变更）。

## 功能模块

### 微信小程序 · 企业用户端（品牌蓝 2563EB）
1. 诉求直达——提交、跟踪、历史、评价
2. 企业信息管理——资料维护、联系人、行业字典
3. 检查整改响应——通知接收、反馈提交、验收结果
4. 合规报告管理——上传、管理、到期提醒
5. 特种设备监测——台账、状态上报、记录
6. 其他——消息中心、OCR 营业执照识别（可降级手动录入）、企业注册向导

### 微信小程序 · 执法人员端（市监红 C8102E）
1. 执法工作台——待处理预警、本月检查统计、属地预警列表
2. 扫码查企——扫监管码/手输信用代码，全景档案（诉求/整改/报告/检查记录）
3. 现场检查——企业检索（属地数据权限）→ 检查类型 → 问题登记（照片上传）→ 提交自动生成整改通知书
4. 预警处置——待处理/处理中/已处置三态，接收/退回/处置完成（对接 24h 督办升级链）
5. 执法我的——本月业绩、功能入口

### Web 管理后台（市监红主题）
诉求管理、任务调度、预警督办、执法管理（现场检查/整改管理）、统计分析、数据可视化、数据大屏、企业档案/注册审核、系统管理（用户/角色/组织架构/参数/日志）、组织四级行政树（地区→市县→机关/乡镇街道 94 个）。

## 快速开始（本地开发环境）

环境（JDK17 / MySQL8 / Redis / MinIO / Node）安装于 `dev-env/`。

```bash
# 1. 中间件
cd dev-env/mysql/bin && ./mysqld --defaults-file=../my.ini --console &
dev-env/minio-server.exe server minio/data --address :9000 --console-address :9001 &
# Redis 按本机服务方式启动（6379）

# 2. 后端（:8080）
dev-env/jdk-17/jdk-17.0.12+7/bin/java.exe -jar backend/target/aksu-supervision-backend-1.0.0.jar

# 3. 三个前端
cd web-admin   && npm run dev     # :3000  管理后台
cd miniapp     && npm run dev:h5  # :5174  小程序（uni-app）
cd miniapp-h5  && npx vite --port 5175   # :5175  小程序 H5 + 公众公示页

# 4. 小程序真机（appid 配置后）
cd miniapp && npm run build:mp-weixin   # 产物 dist/build/mp-weixin → 微信开发者工具导入
```

**测试账号**（密码均 `123456`）：`admin`（管理后台，验证码见后端日志）、`ent_user1`（企业：库车县金桥建材）、`inspector1`（执法：阿克苏市）。

**端到端回归**：`AKSU_BACKEND_LOG=<后端日志路径> python scripts/e2e_integration_test.py`（42 项：登录/申诉/检查整改/注册/预警督办/数据权限/角色隔离）。

## 生产部署

见 [docs/部署手册.md](docs/部署手册.md)。关键配置（环境变量）：

| 变量 | 说明 |
|---|---|
| `DB_PASSWORD` / `JWT_SECRET` | 数据库密码 / JWT 签名密钥（生产必须强随机） |
| `CAPTCHA_LOG_CODE` | 验证码日志明文，**生产必须 false** |
| `WX_MINI_APPID` / `WX_MINI_SECRET` | 小程序凭据（wx-login、订阅消息） |
| `WX_TPL_RECTIFY` / `WX_TPL_ALERT` | 订阅消息模板 ID（留空降级为仅站内信） |
| `BAIDU_OCR_*` | 百度 OCR 密钥（留空降级手动录入） |

## 技术架构

- **后端**：Spring Boot 3.2 / Java 17 / JPA / Spring Security（JWT + **路径级角色隔离**：`/api/admin/**` 管理员、`/api/inspector/**` 执法、`/api/enterprise/profile**` 企业自助）、MySQL 8 / Redis / MinIO
- **预警引擎**：8 类规则、3 级预警、24h 督办升级链（科员→科长→局领导→地区局）、每日 02:00 全量扫描
- **数据权限**：组织区域过滤（执法员仅见属地企业，企业仅见本企业）
- **前端**：统一设计语言 V2（市监红 C8102E / 品牌蓝 2563EB / 金 C9A063 / 官方徽章），iOS 高对比浅色，执法端中央凸起"现场检查"主键

## 项目目录结构

```
backend/        Spring Boot 后端（17 包）
miniapp/        uni-app 微信小程序（企业+执法双角色）
miniapp-h5/     小程序 H5 同源实现 + 公众公示页
web-admin/      Web 管理后台（Element Plus）
scripts/        E2E 回归 / 连通性测试 / 数据修复脚本
dev-env/        本地开发环境（JDK/MySQL/Maven/Node…）
docs/           文档（部署手册等）
design-ui/      UI 设计稿（React showcase，四端 18 屏）
deploy/         部署脚手架
```
