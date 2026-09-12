-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: aksu_supervision
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `acceptance_record`
--

DROP TABLE IF EXISTS `acceptance_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acceptance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notice_id` bigint NOT NULL COMMENT '整改通知书ID',
  `feedback_id` bigint DEFAULT NULL,
  `inspector_id` bigint NOT NULL COMMENT '验收人员ID',
  `conclusion` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '合格/不合格/需现场复核',
  `opinion` text COLLATE utf8mb4_unicode_ci COMMENT '验收意见',
  `reject_reason` text COLLATE utf8mb4_unicode_ci COMMENT '不合格原因',
  `ai_comparison_result` json DEFAULT NULL COMMENT 'AI视觉比对结果{score,differences,highlights}',
  `before_images` json DEFAULT NULL COMMENT '整改前图片',
  `after_images` json DEFAULT NULL COMMENT '整改后图片',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `evidence_images` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notice` (`notice_id`),
  KEY `idx_inspector` (`inspector_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验收记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acceptance_record`
--

LOCK TABLES `acceptance_record` WRITE;
/*!40000 ALTER TABLE `acceptance_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `acceptance_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alert`
--

DROP TABLE IF EXISTS `alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alert` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `alert_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备异常/报告到期/高风险诉求/任务逾期',
  `alert_level` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '高/中/低',
  `source_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型',
  `source_id` bigint DEFAULT NULL COMMENT '来源ID',
  `enterprise_id` bigint DEFAULT NULL COMMENT '关联企业ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '预警内容',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待处理' COMMENT '待处理/处理中/已处理/已关闭',
  `handler_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `handle_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '立即派单检查/电话核实/标记关注',
  `handle_remark` text COLLATE utf8mb4_unicode_ci COMMENT '处理备注',
  `handled_at` datetime DEFAULT NULL,
  `created_task_id` bigint DEFAULT NULL COMMENT '派单创建的任务ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `enterprise_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_level` (`alert_level`),
  KEY `idx_enterprise` (`enterprise_id`),
  KEY `idx_handler` (`handler_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alert`
--

LOCK TABLES `alert` WRITE;
/*!40000 ALTER TABLE `alert` DISABLE KEYS */;
INSERT INTO `alert` VALUES (1,'许可到期','HIGH',NULL,NULL,5,'特种设备安装许可证即将到期','该企业特种设备安装许可证将于30日内到期，请及时提醒企业办理续期','HANDLED','张明','NOTICE','已通知企业整改','2026-06-25 17:08:29',NULL,'2026-06-25 14:39:49','2026-06-26 17:08:29','阿克苏市鑫达特种设备安装有限公司'),(2,'异常经营','MEDIUM',NULL,NULL,6,'企业年报信息异常','该企业连续两年年报数据存在较大波动，需关注','HANDLED','张明','NOTICE','已通知企业整改','2026-06-25 17:08:29',NULL,'2026-06-24 14:39:49','2026-06-26 17:08:29','沙雅县恒通化工有限公司'),(3,'许可到期','LOW',NULL,NULL,2,'营业执照即将到期','营业执照将于60日内到期','HANDLED','张明','NOTICE','已通知企业整改','2026-06-25 17:08:29',NULL,'2026-06-26 14:39:49','2026-06-26 17:08:29','阿克苏华联商贸有限公司'),(4,'抽检不合格','HIGH',NULL,NULL,1,'食品抽检不合格预警','近期食品抽检中发现该企业部分产品微生物指标超标','PROCESSING','李强','INSPECTION','已安排现场检查',NULL,NULL,'2026-06-26 14:39:49','2026-06-26 17:08:29','阿克苏市天山食品有限责任公司'),(5,'抽检不合格','HIGH',NULL,NULL,23,'水泥强度不合格预警','近期抽检发现该企业水泥强度不达标，需重点关注','PROCESSING','李强','INSPECTION','已安排现场检查',NULL,NULL,'2026-06-26 12:13:51','2026-06-26 17:08:29','拜城县盛达水泥有限公司'),(6,'许可到期','MEDIUM',NULL,NULL,13,'药品经营许可证即将到期','药品经营许可证将于45日内到期','HANDLED','admin',NULL,'Notice_sent','2026-06-26 17:10:39',NULL,'2026-06-25 15:13:51','2026-06-26 17:10:39','拜城县康泰医药有限公司'),(7,'异常经营','HIGH',NULL,NULL,30,'安全生产许可证即将到期','安全生产许可证将于15日内到期，请及时提醒','PENDING',NULL,NULL,NULL,NULL,NULL,'2026-06-26 09:13:51','2026-06-26 09:13:51','库车市恒泰化工有限公司'),(8,'许可到期','LOW',NULL,NULL,9,'排污许可证即将到期','排污许可证将于60日内到期','PENDING',NULL,NULL,NULL,NULL,NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','阿克苏市红旗纺织有限公司'),(9,'许可到期','MEDIUM',NULL,NULL,26,'采矿许可证即将到期','采矿许可证将于30日内到期','PENDING',NULL,NULL,NULL,NULL,NULL,'2026-06-24 15:13:51','2026-06-24 15:13:51','乌什县雪域矿业开发有限公司'),(10,'异常经营','LOW',NULL,NULL,19,'营业执照变更提醒','企业地址变更后未及时更新营业执照','PENDING',NULL,NULL,NULL,NULL,NULL,'2026-06-21 15:13:51','2026-06-21 15:13:51','库车市永安汽车维修有限公司');
/*!40000 ALTER TABLE `alert` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appeal`
--

DROP TABLE IF EXISTS `appeal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appeal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `appeal_no` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `appeal_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '政策咨询/许可办理/检查整改困惑/跨部门协调/服务建议',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '诉求标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '诉求描述',
  `related_fields` json DEFAULT NULL COMMENT '涉及领域["食品安全","特种设备"...]',
  `attachments` json DEFAULT NULL COMMENT '附件列表[{name,url,type,size}]',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '已接收' COMMENT '已接收/处理中/已办结/已驳回',
  `assigned_department` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配科室',
  `assigned_user_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `assign_remark` text COLLATE utf8mb4_unicode_ci COMMENT '分配意见',
  `result_content` text COLLATE utf8mb4_unicode_ci COMMENT '处理结果',
  `reject_reason` text COLLATE utf8mb4_unicode_ci COMMENT '驳回原因',
  `satisfaction_score` tinyint DEFAULT NULL COMMENT '满意度1-5',
  `satisfaction_comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评价内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `handle_attachment` text COLLATE utf8mb4_unicode_ci,
  `handle_time` datetime DEFAULT NULL,
  `evaluate_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `appeal_no` (`appeal_no`),
  KEY `idx_enterprise` (`enterprise_id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`appeal_type`),
  KEY `idx_assigned` (`assigned_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诉求表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appeal`
--

LOCK TABLES `appeal` WRITE;
/*!40000 ALTER TABLE `appeal` DISABLE KEYS */;
INSERT INTO `appeal` VALUES (1,1,'AP2026060001','质量投诉','食品包装标识不规范','我公司在生产过程中发现部分食品包装标识存在不规范情况，希望监管部门给予指导',NULL,NULL,'HANDLED','食品安全监管科','张明','请尽快核实处理','AUTO_TEST',NULL,NULL,NULL,'2026-06-21 14:39:49','2026-06-26 17:32:37',NULL,'2026-06-26 17:32:37',NULL),(2,2,'AP2026060002','许可证','营业执照变更申请','公司经营范围变更，需重新办理营业执照',NULL,NULL,'HANDLED','食品安全监管科','张明','请尽快核实处理','AUTO_TEST',NULL,NULL,NULL,'2026-06-23 14:39:49','2026-06-26 17:36:04',NULL,'2026-06-26 17:36:04',NULL),(3,5,'AP2026060003','特种设备','压力容器定期检验申请','公司压力容器已到检验周期，申请定期检验',NULL,NULL,'HANDLED','食品安全监管科','张明','请尽快核实处理','AUTO_TEST',NULL,NULL,NULL,'2026-06-25 14:39:49','2026-06-26 17:37:43',NULL,'2026-06-26 17:37:43',NULL),(4,6,'AP2026060004','安全举报','化工仓储安全隐患举报','发现化工仓库通风设施不完善，存在安全隐患',NULL,NULL,'HANDLED','特种设备监管科','李强','正在调查中','Resolved_on_site',NULL,NULL,NULL,'2026-06-24 14:39:49','2026-06-26 17:10:39',NULL,'2026-06-26 17:10:39',NULL),(5,3,'AP2026060005','计量','计量器具检定申请','公司磅秤需要年度检定',NULL,NULL,'HANDLING','特种设备监管科','李强','正在调查中',NULL,NULL,NULL,NULL,'2026-06-26 14:39:49','2026-06-26 17:08:29',NULL,NULL,NULL),(11,9,'AP2026060006','质量投诉','纺织品色牢度不合格','我公司生产的部分纺织品色牢度测试不达标，请求技术指导',NULL,NULL,'HANDLED',NULL,NULL,NULL,'完成整改',NULL,3,'处理速度有待提升','2026-06-26 09:13:51','2026-06-26 17:08:29',NULL,'2026-06-22 17:08:29','2026-06-23 17:08:29'),(12,18,'AP2026060007','安全举报','天然气管道安全隐患','小区附近天然气管道存在泄漏隐患',NULL,NULL,'HANDLED','安全科','1',NULL,'AUTO_TEST',NULL,NULL,NULL,'2026-06-26 03:13:51','2026-06-26 17:39:38',NULL,'2026-06-26 17:39:38',NULL),(13,23,'AP2026060008','许可证','水泥生产许可证续期','水泥生产许可证即将到期，申请续期',NULL,NULL,'ASSIGNED',NULL,'Inspector_Wang',NULL,NULL,NULL,NULL,NULL,'2026-06-25 15:13:51','2026-06-26 17:11:26',NULL,NULL,NULL),(14,12,'AP2026060009','质量投诉','农药标签标识问题','农药产品标签与登记内容不符',NULL,NULL,'HANDLED','农资科','1',NULL,'已责令企业整改，标签已重新印制',NULL,NULL,NULL,'2026-06-16 15:13:51','2026-06-21 15:13:51',NULL,'2026-06-21 15:13:51',NULL),(15,30,'AP2026060010','安全举报','化工废水排放问题','厂区周边发现不明废水排放',NULL,NULL,'ASSIGNED','环保科','1',NULL,NULL,NULL,NULL,NULL,'2026-06-24 15:13:51','2026-06-25 15:13:51',NULL,NULL,NULL),(16,13,'AP2026060011','许可证','药品经营许可证变更','药品经营许可证经营范围需变更',NULL,NULL,'PENDING',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-06-26 12:13:51','2026-06-26 12:13:51',NULL,NULL,NULL),(17,16,'AP2026060012','特种设备','电梯年检申请','公司办公楼电梯需年度检验',NULL,NULL,'PENDING',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51',NULL,NULL,NULL),(18,25,'AP2026060013','质量投诉','食品添加剂超标','抽检发现部分产品食品添加剂超标',NULL,NULL,'ASSIGNED','食品科','1',NULL,NULL,NULL,NULL,NULL,'2026-06-22 15:13:51','2026-06-23 15:13:51',NULL,NULL,NULL);
/*!40000 ALTER TABLE `appeal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appeal_process_log`
--

DROP TABLE IF EXISTS `appeal_process_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appeal_process_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appeal_id` bigint NOT NULL,
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `action` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '操作内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_appeal` (`appeal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诉求处理记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appeal_process_log`
--

LOCK TABLES `appeal_process_log` WRITE;
/*!40000 ALTER TABLE `appeal_process_log` DISABLE KEYS */;
INSERT INTO `appeal_process_log` VALUES (1,1,NULL,'admin','ASSIGN','分配给: 检查员张三','2026-06-26 16:05:38'),(2,1,NULL,'admin','HANDLE','处理诉求','2026-06-26 16:05:38'),(3,3,NULL,'admin','ASSIGN','分配给: Inspector1','2026-06-26 16:09:49'),(4,3,NULL,'admin','HANDLE','处理诉求','2026-06-26 16:09:49'),(5,2,NULL,'admin','HANDLE','处理诉求','2026-06-26 16:54:04'),(6,4,NULL,'admin','HANDLE','处理诉求','2026-06-26 17:10:39'),(7,13,NULL,'admin','ASSIGN','分配给: Inspector_Wang','2026-06-26 17:11:26'),(8,1,NULL,'admin','HANDLE','处理诉求','2026-06-26 17:32:37'),(9,2,NULL,'admin','HANDLE','处理诉求','2026-06-26 17:36:04'),(10,3,NULL,'admin','HANDLE','处理诉求','2026-06-26 17:37:43'),(11,12,NULL,'admin','HANDLE','处理诉求','2026-06-26 17:39:38'),(12,19,NULL,NULL,'CREATE','企业提交诉求','2026-06-26 19:32:08'),(13,19,NULL,'inspector1','HANDLE','处理诉求','2026-06-26 19:33:33'),(14,20,NULL,NULL,'CREATE','企业提交诉求','2026-06-26 19:35:56');
/*!40000 ALTER TABLE `appeal_process_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compliance_report`
--

DROP TABLE IF EXISTS `compliance_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compliance_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enterprise_id` bigint NOT NULL,
  `report_no` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '报告编号',
  `industry` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行业分类',
  `report_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '出厂检验/第三方型式检验/特种设备定期检验/计量器具检定等',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `file_format` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件格式',
  `product_batch_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品批次号',
  `device_reg_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备注册代码',
  `inspection_date` date DEFAULT NULL COMMENT '检验日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '已提交' COMMENT '已提交/已审查/待更新/已过期',
  `reviewed_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `review_remark` text COLLATE utf8mb4_unicode_ci COMMENT '审核意见',
  `has_electronic_seal` tinyint DEFAULT '0' COMMENT '是否有电子签章',
  `seal_verified` tinyint DEFAULT '0' COMMENT '签章是否验证通过',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `uploaded_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_no` (`report_no`),
  KEY `idx_enterprise` (`enterprise_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expiry` (`expiry_date`),
  KEY `idx_type` (`report_type`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合规报告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compliance_report`
--

LOCK TABLES `compliance_report` WRITE;
/*!40000 ALTER TABLE `compliance_report` DISABLE KEYS */;
INSERT INTO `compliance_report` VALUES (1,1,'RPT-2026-001','食品生产','出厂检验报告','factory_1.pdf','/reports/factory_1.pdf',NULL,NULL,NULL,NULL,'2026-05-10','2027-05-10','APPROVED','admin','2026-06-26 17:39:38','Auto test',0,0,'2026-06-26 15:44:08','2026-06-26 17:39:38','阿克苏市天山食品出厂检验','enterprise1'),(2,2,'RPT-2026-002','食品生产','第三方型式检验','third_2.pdf','/reports/third_2.pdf',NULL,NULL,NULL,NULL,'2026-04-20','2026-10-20','APPROVED','admin','2026-06-26 16:06:14','审核通过',0,0,'2026-06-26 15:44:08','2026-06-26 16:06:14','库车市绿洲食品型式检验','enterprise2'),(3,3,'RPT-2026-003','特种设备','特种设备定期检验','equip_3.pdf','/reports/equip_3.pdf',NULL,NULL,NULL,NULL,'2026-06-01','2027-06-01','PENDING',NULL,NULL,NULL,0,0,'2026-06-26 15:44:08','2026-06-26 15:44:08','温宿县宏达化工设备检验','enterprise3'),(4,5,'RPT-2026-005','化工','计量器具检定','metro_5.pdf','/reports/metro_5.pdf',NULL,NULL,NULL,NULL,'2026-03-15','2027-03-15','APPROVED','admin','2026-06-26 16:09:49','OK',0,0,'2026-06-26 15:44:08','2026-06-26 16:09:49','沙雅县鑫源化工计量检定','enterprise5'),(5,8,'RPT-2026-008','商贸流通','出厂检验报告','factory_8.pdf','/reports/factory_8.pdf',NULL,NULL,NULL,NULL,'2026-05-20','2026-07-20','APPROVED','admin','2026-06-26 17:10:39','Documents_complete',0,0,'2026-06-26 15:44:08','2026-06-26 17:10:39','拜城县天成商行出厂检验','enterprise8'),(6,10,'RPT-2026-010','建筑材料','第三方型式检验','third_10.pdf','/reports/third_10.pdf',NULL,NULL,NULL,NULL,'2026-06-15','2027-06-15','REJECTED','admin','2026-06-26 17:10:39','Incomplete_documents',0,0,'2026-06-26 15:44:08','2026-06-26 17:10:39','新和县金盛建材型式检验','enterprise10');
/*!40000 ALTER TABLE `compliance_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dept_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `dept_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `area_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `leader_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_order` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_code` (`dept_code`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_type` (`dept_type`),
  KEY `idx_area` (`area_name`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,'AKSU_REGION','阿克苏地区市场监督管理局',NULL,'REGION','阿克苏地区','徐云峰','0997-2132829','阿克苏市文化路40号',1,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(2,'REGION_ZHB','综合协调部（办公室/财务与审计科）',1,'OFFICE','阿克苏地区','刁春林','0997-2132829',NULL,10,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(3,'REGION_YSHJ','营商环境服务部（行政审批科/登记注册科）',1,'OFFICE','阿克苏地区','刁春林','0997-2132829',NULL,20,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(4,'REGION_CXJG','创新监管部（信用监管科/科技信息化科）',1,'OFFICE','阿克苏地区','刁春林','0997-2132829',NULL,30,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(5,'REGION_SPAQ','食品安全工作部（食品安全协调科/食品生产流通安全监管科/餐饮食品安全监管科）',1,'OFFICE','阿克苏地区','汪连江','0997-2132829',NULL,40,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(6,'REGION_FZGH','法治规划部（法规科）',1,'OFFICE','阿克苏地区','王志强','0997-2132829',NULL,50,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(7,'REGION_ZFZD','综合执法部（执法稽查科/反垄断和反不正当竞争科/价格监督检查科/网络交易和广告监督管理科）',1,'OFFICE','阿克苏地区','王志强','0997-2132829',NULL,60,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(8,'REGION_ZLAQ','质量安全工作部（质量发展监管科/计量科/标准化科/纤维质量监管科）',1,'OFFICE','阿克苏地区','王志强','0997-2132829',NULL,70,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(9,'REGION_TZSB','特种设备安全工作部（特种设备安全监察科）',1,'OFFICE','阿克苏地区','王志强','0997-2132829',NULL,80,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(10,'REGION_ZHISH','知识产权部（知识产权科）',1,'OFFICE','阿克苏地区','王志强','0997-2132829',NULL,90,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(11,'REGION_DJRS','党建人事工作部（组织人事科）',1,'OFFICE','阿克苏地区','陈易','0997-2132829',NULL,100,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(12,'REGION_YXAQ','药械安全工作部（风险监测抽检科/药品和医疗器械监管科/化妆品监管科）',1,'OFFICE','阿克苏地区','陈易','0997-2132829',NULL,110,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(13,'REGION_FZFG','阿克苏纺织工业城（开发区）市场监督管理分局',1,'BRANCH','阿克苏地区','王志强','0997-2132829',NULL,120,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(14,'AKSU_CITY','阿克苏市市场监督管理局',NULL,'CITY','阿克苏市','王枫','0997-2124971','阿克苏市红旗坡片区北京路67号',200,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(15,'CITY_BGS','办公室',14,'OFFICE','阿克苏市',NULL,NULL,NULL,210,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(16,'CITY_FGK','法规科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,220,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(17,'CITY_ZCS','注册登记科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,230,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(18,'CITY_XZSP','行政审批科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,240,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(19,'CITY_XYJG','信用监管科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,250,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(20,'CITY_SPAQ','食品安全监管科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,260,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(21,'CITY_CYPZ','药品化妆品监管科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,270,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(22,'CITY_YLQX','医疗器械监管科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,280,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(23,'CITY_ZLJG','质量监管科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,290,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(24,'CITY_TZSB','特种设备安全监察科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,300,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(25,'CITY_JLK','计量科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,310,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(26,'CITY_BZK','标准化科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,320,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(27,'CITY_JGJC','价格监督检查科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,330,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(28,'CITY_FLD','反垄断和反不正当竞争科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,340,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(29,'CITY_WLJG','网络交易和广告监管科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,350,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(30,'CITY_ZHISH','知识产权保护科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,360,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(31,'CITY_XFQY','消费者权益保护科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,370,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(32,'CITY_ZFZD','执法稽查科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,380,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(33,'CITY_SJXX','科技信息化科',14,'OFFICE','阿克苏市',NULL,NULL,NULL,390,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(34,'KUCHE_COUNTY','库车市市场监督管理局',NULL,'COUNTY','库车市',NULL,NULL,NULL,400,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(35,'KUCHE_BGS','办公室',34,'OFFICE','库车市',NULL,NULL,NULL,410,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(36,'KUCHE_ZCS','注册登记科',34,'OFFICE','库车市',NULL,NULL,NULL,420,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(37,'KUCHE_XYJG','信用监管科',34,'OFFICE','库车市',NULL,NULL,NULL,430,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(38,'KUCHE_SPAQ','食品安全监管科',34,'OFFICE','库车市',NULL,NULL,NULL,440,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(39,'KUCHE_CYPZ','药品化妆品监管科',34,'OFFICE','库车市',NULL,NULL,NULL,450,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(40,'KUCHE_ZLJG','质量监管科',34,'OFFICE','库车市',NULL,NULL,NULL,460,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(41,'KUCHE_TZSB','特种设备安全监察科',34,'OFFICE','库车市',NULL,NULL,NULL,470,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(42,'KUCHE_JGJC','价格监督检查科',34,'OFFICE','库车市',NULL,NULL,NULL,480,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(43,'KUCHE_ZFZD','执法稽查科',34,'OFFICE','库车市',NULL,NULL,NULL,490,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(44,'KUCHE_XFQY','消费者权益保护科',34,'OFFICE','库车市',NULL,NULL,NULL,500,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(45,'WENSU_COUNTY','温宿县市场监督管理局',NULL,'COUNTY','温宿县',NULL,NULL,NULL,600,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(46,'WENSU_BGS','办公室',45,'OFFICE','温宿县',NULL,NULL,NULL,610,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(47,'WENSU_ZCS','注册登记科',45,'OFFICE','温宿县',NULL,NULL,NULL,620,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(48,'WENSU_XYJG','信用监管科',45,'OFFICE','温宿县',NULL,NULL,NULL,630,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(49,'WENSU_SPAQ','食品安全监管科',45,'OFFICE','温宿县',NULL,NULL,NULL,640,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(50,'WENSU_CYPZ','药品化妆品监管科',45,'OFFICE','温宿县',NULL,NULL,NULL,650,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(51,'WENSU_ZLJG','质量监管科',45,'OFFICE','温宿县',NULL,NULL,NULL,660,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(52,'WENSU_TZSB','特种设备安全监察科',45,'OFFICE','温宿县',NULL,NULL,NULL,670,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(53,'WENSU_JGJC','价格监督检查科',45,'OFFICE','温宿县',NULL,NULL,NULL,680,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(54,'WENSU_ZFZD','执法稽查科',45,'OFFICE','温宿县',NULL,NULL,NULL,690,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(55,'WENSU_XFQY','消费者权益保护科',45,'OFFICE','温宿县',NULL,NULL,NULL,700,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(56,'SHAYA_COUNTY','沙雅县市场监督管理局',NULL,'COUNTY','沙雅县',NULL,NULL,NULL,800,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(57,'SHAYA_BGS','办公室',56,'OFFICE','沙雅县',NULL,NULL,NULL,810,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(58,'SHAYA_ZCS','注册登记科',56,'OFFICE','沙雅县',NULL,NULL,NULL,820,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(59,'SHAYA_XYJG','信用监管科',56,'OFFICE','沙雅县',NULL,NULL,NULL,830,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(60,'SHAYA_SPAQ','食品安全监管科',56,'OFFICE','沙雅县',NULL,NULL,NULL,840,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(61,'SHAYA_CYPZ','药品化妆品监管科',56,'OFFICE','沙雅县',NULL,NULL,NULL,850,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(62,'SHAYA_ZLJG','质量监管科',56,'OFFICE','沙雅县',NULL,NULL,NULL,860,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(63,'SHAYA_TZSB','特种设备安全监察科',56,'OFFICE','沙雅县',NULL,NULL,NULL,870,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(64,'SHAYA_JGJC','价格监督检查科',56,'OFFICE','沙雅县',NULL,NULL,NULL,880,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(65,'SHAYA_ZFZD','执法稽查科',56,'OFFICE','沙雅县',NULL,NULL,NULL,890,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(66,'SHAYA_XFQY','消费者权益保护科',56,'OFFICE','沙雅县',NULL,NULL,NULL,900,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(67,'BAICHENG_COUNTY','拜城县市场监督管理局',NULL,'COUNTY','拜城县',NULL,NULL,NULL,1000,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(68,'BAICHENG_BGS','办公室',67,'OFFICE','拜城县',NULL,NULL,NULL,1010,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(69,'BAICHENG_ZCS','注册登记科',67,'OFFICE','拜城县',NULL,NULL,NULL,1020,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(70,'BAICHENG_XYJG','信用监管科',67,'OFFICE','拜城县',NULL,NULL,NULL,1030,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(71,'BAICHENG_SPAQ','食品安全监管科',67,'OFFICE','拜城县',NULL,NULL,NULL,1040,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(72,'BAICHENG_CYPZ','药品化妆品监管科',67,'OFFICE','拜城县',NULL,NULL,NULL,1050,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(73,'BAICHENG_ZLJG','质量监管科',67,'OFFICE','拜城县',NULL,NULL,NULL,1060,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(74,'BAICHENG_TZSB','特种设备安全监察科',67,'OFFICE','拜城县',NULL,NULL,NULL,1070,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(75,'BAICHENG_JGJC','价格监督检查科',67,'OFFICE','拜城县',NULL,NULL,NULL,1080,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(76,'BAICHENG_ZFZD','执法稽查科',67,'OFFICE','拜城县',NULL,NULL,NULL,1090,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(77,'BAICHENG_XFQY','消费者权益保护科',67,'OFFICE','拜城县',NULL,NULL,NULL,1100,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(78,'XINHE_COUNTY','新和县市场监督管理局',NULL,'COUNTY','新和县',NULL,NULL,NULL,1200,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(79,'XINHE_BGS','办公室',78,'OFFICE','新和县',NULL,NULL,NULL,1210,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(80,'XINHE_ZCS','注册登记科',78,'OFFICE','新和县',NULL,NULL,NULL,1220,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(81,'XINHE_XYJG','信用监管科',78,'OFFICE','新和县',NULL,NULL,NULL,1230,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(82,'XINHE_SPAQ','食品安全监管科',78,'OFFICE','新和县',NULL,NULL,NULL,1240,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(83,'XINHE_CYPZ','药品化妆品监管科',78,'OFFICE','新和县',NULL,NULL,NULL,1250,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(84,'XINHE_ZLJG','质量监管科',78,'OFFICE','新和县',NULL,NULL,NULL,1260,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(85,'XINHE_TZSB','特种设备安全监察科',78,'OFFICE','新和县',NULL,NULL,NULL,1270,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(86,'XINHE_JGJC','价格监督检查科',78,'OFFICE','新和县',NULL,NULL,NULL,1280,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(87,'XINHE_ZFZD','执法稽查科',78,'OFFICE','新和县',NULL,NULL,NULL,1290,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(88,'XINHE_XFQY','消费者权益保护科',78,'OFFICE','新和县',NULL,NULL,NULL,1300,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(89,'WUSHI_COUNTY','乌什县市场监督管理局',NULL,'COUNTY','乌什县',NULL,NULL,NULL,1400,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(90,'WUSHI_BGS','办公室',89,'OFFICE','乌什县',NULL,NULL,NULL,1410,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(91,'WUSHI_ZCS','注册登记科',89,'OFFICE','乌什县',NULL,NULL,NULL,1420,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(92,'WUSHI_XYJG','信用监管科',89,'OFFICE','乌什县',NULL,NULL,NULL,1430,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(93,'WUSHI_SPAQ','食品安全监管科',89,'OFFICE','乌什县',NULL,NULL,NULL,1440,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(94,'WUSHI_CYPZ','药品化妆品监管科',89,'OFFICE','乌什县',NULL,NULL,NULL,1450,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(95,'WUSHI_ZLJG','质量监管科',89,'OFFICE','乌什县',NULL,NULL,NULL,1460,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(96,'WUSHI_TZSB','特种设备安全监察科',89,'OFFICE','乌什县',NULL,NULL,NULL,1470,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(97,'WUSHI_JGJC','价格监督检查科',89,'OFFICE','乌什县',NULL,NULL,NULL,1480,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(98,'WUSHI_ZFZD','执法稽查科',89,'OFFICE','乌什县',NULL,NULL,NULL,1490,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(99,'WUSHI_XFQY','消费者权益保护科',89,'OFFICE','乌什县',NULL,NULL,NULL,1500,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(100,'AWATI_COUNTY','阿瓦提县市场监督管理局',NULL,'COUNTY','阿瓦提县',NULL,NULL,NULL,1600,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(101,'AWATI_BGS','办公室',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1610,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(102,'AWATI_ZCS','注册登记科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1620,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(103,'AWATI_XYJG','信用监管科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1630,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(104,'AWATI_SPAQ','食品安全监管科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1640,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(105,'AWATI_CYPZ','药品化妆品监管科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1650,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(106,'AWATI_ZLJG','质量监管科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1660,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(107,'AWATI_TZSB','特种设备安全监察科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1670,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(108,'AWATI_JGJC','价格监督检查科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1680,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(109,'AWATI_ZFZD','执法稽查科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1690,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(110,'AWATI_XFQY','消费者权益保护科',100,'OFFICE','阿瓦提县',NULL,NULL,NULL,1700,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(111,'KEPING_COUNTY','柯坪县市场监督管理局',NULL,'COUNTY','柯坪县',NULL,NULL,NULL,1800,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(112,'KEPING_BGS','办公室',111,'OFFICE','柯坪县',NULL,NULL,NULL,1810,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(113,'KEPING_ZCS','注册登记科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1820,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(114,'KEPING_XYJG','信用监管科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1830,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(115,'KEPING_SPAQ','食品安全监管科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1840,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(116,'KEPING_CYPZ','药品化妆品监管科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1850,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(117,'KEPING_ZLJG','质量监管科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1860,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(118,'KEPING_TZSB','特种设备安全监察科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1870,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(119,'KEPING_JGJC','价格监督检查科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1880,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(120,'KEPING_ZFZD','执法稽查科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1890,1,'2026-06-27 14:17:27','2026-06-27 14:17:27'),(121,'KEPING_XFQY','消费者权益保护科',111,'OFFICE','柯坪县',NULL,NULL,NULL,1900,1,'2026-06-27 14:17:27','2026-06-27 14:17:27');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enterprise`
--

DROP TABLE IF EXISTS `enterprise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enterprise` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `credit_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enterprise_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '企业名称',
  `legal_person` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经营地址',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `industry` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `scale` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规模(微型/小型/中型/大型)',
  `employee_count` int DEFAULT NULL COMMENT '员工数',
  `business_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '正常' COMMENT '正常/停业/注销',
  `license_info` json DEFAULT NULL COMMENT '许可证信息(JSON数组)',
  `wx_openid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信OpenID',
  `wx_unionid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信UnionID',
  `status` tinyint DEFAULT '1' COMMENT '1-正常 0-禁用',
  `data_source` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'manual' COMMENT 'manual/sync 同步来源',
  `last_sync_at` datetime DEFAULT NULL COMMENT '最后同步时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `area` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `business_scope` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `license_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `credit_code` (`credit_code`),
  KEY `idx_name` (`enterprise_name`),
  KEY `idx_industry` (`industry`),
  KEY `idx_area` (`address`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enterprise`
--

LOCK TABLES `enterprise` WRITE;
/*!40000 ALTER TABLE `enterprise` DISABLE KEYS */;
INSERT INTO `enterprise` VALUES (1,'91652901MA7XXXX1','阿克苏市天山食品有限责任公司','张三','阿克苏市人民路15号',NULL,NULL,'食品生产','中型',120,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 17:10:39','13800001001',NULL,'阿克苏市','食品生产、加工及销售',NULL,3),(2,'91652901MA7XXXX2','阿克苏华联商贸有限公司','李四','阿克苏市解放路28号',NULL,NULL,'商贸流通','小型',45,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 17:06:44','13800001002',NULL,'阿克苏市','日用百货、五金交电销售',NULL,4),(3,'91652901MA7XXXX3','库车县金桥建材有限公司','王五','库车市友谊路56号',NULL,NULL,'建筑材料','中型',80,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 17:35:30','13800001003',NULL,'库车市','建材生产与销售',NULL,16),(4,'91652901MA7XXXX4','温宿县绿洲农业科技有限公司','赵六','温宿县幸福路12号',NULL,NULL,'农产品','小型',60,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 17:35:30','13800001004',NULL,'温宿县','农产品种植、加工、销售',NULL,17),(5,'91652901MA7XXXX5','阿克苏市鑫达特种设备安装有限公司','钱七','阿克苏市建设路99号',NULL,NULL,'特种设备','中型',90,'重点监管',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 14:38:26','13800001005',NULL,'阿克苏市','特种设备安装、维修',NULL,NULL),(6,'91652901MA7XXXX6','沙雅县恒通化工有限公司','孙八','沙雅县工业路33号',NULL,NULL,'化工','大型',200,'重点监管',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 14:38:26','13800001006',NULL,'沙雅县','化工产品生产销售',NULL,NULL),(7,'91652901MA7XXXX7','拜城县隆盛矿业有限责任公司','周九','拜城县矿业路8号',NULL,NULL,'矿业','大型',350,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 14:38:26','13800001007',NULL,'拜城县','煤炭开采及销售',NULL,NULL),(8,'91652901MA7XXXX8','新和县顺达物流有限公司','吴十','新和县物流园1号',NULL,NULL,'物流运输','小型',30,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 14:38:26','2026-06-26 14:38:26','13800001008',NULL,'新和县','道路货物运输',NULL,NULL),(9,'91652901MA7XXXX9','阿克苏市红旗纺织有限公司','马一','阿克苏市红旗路45号',NULL,NULL,'纺织业','中型',150,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001009',NULL,'阿克苏市','棉纺织加工销售',NULL,NULL),(10,'91652901MA7XXX10','库车市鑫源粮油有限公司','马二','库车市新华路88号',NULL,NULL,'食品生产','小型',55,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001010',NULL,'库车市','粮油加工销售',NULL,NULL),(11,'91652901MA7XXX11','温宿县金穗面粉有限公司','马三','温宿县粮贸路22号',NULL,NULL,'食品生产','小型',40,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001011',NULL,'温宿县','面粉加工',NULL,NULL),(12,'91652901MA7XXX12','沙雅县丰产农药有限公司','马四','沙雅县农资路11号',NULL,NULL,'农资','小型',25,'重点监管',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001012',NULL,'沙雅县','农药销售',NULL,NULL),(13,'91652901MA7XXX13','拜城县康泰医药有限公司','马五','拜城县健康路6号',NULL,NULL,'医药','中型',80,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001013',NULL,'拜城县','药品零售批发',NULL,NULL),(14,'91652901MA7XXX14','新和县绿源农资有限公司','马六','新和县农贸路9号',NULL,NULL,'农资','小型',20,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001014',NULL,'新和县','化肥农药销售',NULL,NULL),(15,'91652901MA7XXX15','乌什县天山水电有限公司','马七','乌什县水利路3号',NULL,NULL,'能源','中型',100,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001015',NULL,'乌什县','水力发电',NULL,NULL),(16,'91652901MA7XXX16','阿瓦提县棉都纺织有限公司','马八','阿瓦提县棉纺路18号',NULL,NULL,'纺织业','大型',300,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001016',NULL,'阿瓦提县','棉纺织加工出口',NULL,NULL),(17,'91652901MA7XXX17','柯坪县玉山畜牧有限公司','马九','柯坪县牧业路7号',NULL,NULL,'畜牧业','小型',35,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001017',NULL,'柯坪县','畜牧养殖销售',NULL,NULL),(18,'91652901MA7XXX18','阿克苏市天燃气有限公司','马十','阿克苏市能源路52号',NULL,NULL,'能源','大型',250,'重点监管',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001018',NULL,'阿克苏市','天然气供应',NULL,NULL),(19,'91652901MA7XXX19','库车市永安汽车维修有限公司','刘一','库车市交通路33号',NULL,NULL,'汽车服务','小型',15,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001019',NULL,'库车市','汽车维修保养',NULL,NULL),(20,'91652901MA7XXX20','阿克苏市金龙面粉有限公司','刘二','阿克苏市粮贸路66号',NULL,NULL,'食品生产','中型',70,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001020',NULL,'阿克苏市','面粉加工销售',NULL,NULL),(21,'91652901MA7XXX21','温宿县兴达塑料制品有限公司','刘三','温宿县工业园5号',NULL,NULL,'塑料制品','小型',45,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001021',NULL,'温宿县','塑料制品生产',NULL,NULL),(22,'91652901MA7XXX22','沙雅县润泽水务有限公司','刘四','沙雅县水务路1号',NULL,NULL,'水务','中型',90,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001022',NULL,'沙雅县','供水服务',NULL,NULL),(23,'91652901MA7XXX23','拜城县盛达水泥有限公司','刘五','拜城县建材路15号',NULL,NULL,'建筑材料','大型',280,'重点监管',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001023',NULL,'拜城县','水泥生产销售',NULL,NULL),(24,'91652901MA7XXX24','阿克苏市利民医疗器械有限公司','刘六','阿克苏市科技路8号',NULL,NULL,'医疗器械','小型',30,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001024',NULL,'阿克苏市','医疗器械销售',NULL,NULL),(25,'91652901MA7XXX25','新和县诚信食品加工有限公司','刘七','新和县食品园2号',NULL,NULL,'食品生产','小型',50,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001025',NULL,'新和县','食品加工销售',NULL,NULL),(26,'91652901MA7XXX26','乌什县雪域矿业开发有限公司','刘八','乌什县矿区路1号',NULL,NULL,'矿业','中型',120,'重点监管',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001026',NULL,'乌什县','矿产开采',NULL,NULL),(27,'91652901MA7XXX27','阿瓦提县金桥商贸有限公司','刘九','阿瓦提县商业街20号',NULL,NULL,'商贸流通','小型',25,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001027',NULL,'阿瓦提县','日用百货销售',NULL,NULL),(28,'91652901MA7XXX28','柯坪县绿洲生态农业有限公司','刘十','柯坪县农业路12号',NULL,NULL,'农产品','小型',40,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001028',NULL,'柯坪县','生态农业种植',NULL,NULL),(29,'91652901MA7XXX29','阿克苏市天山塑料制品有限公司','陈一','阿克苏市工业路77号',NULL,NULL,'塑料制品','中型',85,'正常',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001029',NULL,'阿克苏市','塑料包装生产',NULL,NULL),(30,'91652901MA7XXX30','库车市恒泰化工有限公司','陈二','库车市化工园3号',NULL,NULL,'化工','大型',350,'重点监管',NULL,NULL,NULL,1,'manual',NULL,'2026-06-26 15:13:51','2026-06-26 15:13:51','13800001030',NULL,'库车市','化工原料生产',NULL,NULL);
/*!40000 ALTER TABLE `enterprise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enterprise_contact`
--

DROP TABLE IF EXISTS `enterprise_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enterprise_contact` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `contact_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `is_primary` tinyint DEFAULT '0' COMMENT '1-主要联系人',
  `verified` tinyint DEFAULT '0' COMMENT '1-已验证',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `position` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_enterprise` (`enterprise_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业联系人表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enterprise_contact`
--

LOCK TABLES `enterprise_contact` WRITE;
/*!40000 ALTER TABLE `enterprise_contact` DISABLE KEYS */;
INSERT INTO `enterprise_contact` VALUES (7,1,'王建华','13900139001',1,1,'2026-06-26 17:08:29','2026-06-26 17:08:29','总经理','wangjh@tianshan.com'),(8,1,'张小芳','13900139003',0,1,'2026-06-26 17:08:29','2026-06-26 17:08:29','质量负责人','zhangxf@tianshan.com'),(9,2,'刘丽','13900139002',1,1,'2026-06-26 17:08:29','2026-06-26 17:08:29','负责人','liuli@huafeng.com'),(10,3,'陈国强','13900139004',1,1,'2026-06-26 17:08:29','2026-06-26 17:08:29','负责人','chengq@keqin.com'),(11,4,'赵红','13900139005',1,0,'2026-06-26 17:08:29','2026-06-26 17:08:29','负责人','zhaoh@yatai.com'),(12,5,'马文','13900139006',1,1,'2026-06-26 17:08:29','2026-06-26 17:08:29','负责人','mawen@xinjiang.com');
/*!40000 ALTER TABLE `enterprise_contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_check_template`
--

DROP TABLE IF EXISTS `equipment_check_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_check_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备类型',
  `template_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `check_items` json NOT NULL COMMENT '点检项[{name,description,check_method}]',
  `status` tinyint DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备点检模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_check_template`
--

LOCK TABLES `equipment_check_template` WRITE;
/*!40000 ALTER TABLE `equipment_check_template` DISABLE KEYS */;
INSERT INTO `equipment_check_template` VALUES (1,'电梯','电梯日常点检模板','[{\"name\": \"仪表读数\", \"description\": \"检查运行参数显示\", \"check_method\": \"观察记录\"}, {\"name\": \"运行声音\", \"description\": \"运行是否有异响\", \"check_method\": \"听觉判断\"}, {\"name\": \"安全装置外观\", \"description\": \"安全钳、限速器等外观\", \"check_method\": \"目视检查\"}, {\"name\": \"紧急报警装置\", \"description\": \"报警按钮和对讲功能\", \"check_method\": \"功能测试\"}]',1,'2026-06-26 03:50:10','2026-06-26 03:50:10'),(2,'锅炉','锅炉日常点检模板','[{\"name\": \"压力表读数\", \"description\": \"检查压力表显示值\", \"check_method\": \"观察记录\"}, {\"name\": \"水位计\", \"description\": \"水位是否在正常范围\", \"check_method\": \"观察比对\"}, {\"name\": \"安全阀状态\", \"description\": \"安全阀是否正常\", \"check_method\": \"外观+手动测试\"}, {\"name\": \"运行声音\", \"description\": \"有无异常声响\", \"check_method\": \"听觉判断\"}]',1,'2026-06-26 03:50:10','2026-06-26 03:50:10');
/*!40000 ALTER TABLE `equipment_check_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_ledger`
--

DROP TABLE IF EXISTS `equipment_ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_ledger` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enterprise_id` bigint NOT NULL,
  `device_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
  `reg_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注册代码',
  `model` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '型号',
  `use_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '使用地点',
  `inspection_expiry` date DEFAULT NULL COMMENT '检验有效期',
  `device_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备类型',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '正常' COMMENT '正常/异常/停用',
  `data_source` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'manual' COMMENT 'manual/sync',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `manufacturer` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `install_date` datetime DEFAULT NULL,
  `certificate_no` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_enterprise` (`enterprise_id`),
  KEY `idx_expiry` (`inspection_expiry`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='特种设备台账表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_ledger`
--

LOCK TABLES `equipment_ledger` WRITE;
/*!40000 ALTER TABLE `equipment_ledger` DISABLE KEYS */;
/*!40000 ALTER TABLE `equipment_ledger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_report`
--

DROP TABLE IF EXISTS `equipment_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `equipment_id` bigint NOT NULL COMMENT '设备台账ID',
  `enterprise_id` bigint NOT NULL,
  `report_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '每日点检/周检/月检/问题上报',
  `check_items` json DEFAULT NULL COMMENT '检查项数据[{item,result:normal/abnormal,value}]',
  `is_abnormal` tinyint DEFAULT '0' COMMENT '是否有异常',
  `abnormal_items` json DEFAULT NULL COMMENT '异常项详情[{item,description,images}]',
  `abnormal_images` json DEFAULT NULL COMMENT '异常图片',
  `abnormal_videos` json DEFAULT NULL COMMENT '异常视频',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '已提交' COMMENT '已提交/已处理',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `content` text COLLATE utf8mb4_unicode_ci,
  `images` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reporter` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_equipment` (`equipment_id`),
  KEY `idx_enterprise` (`enterprise_id`),
  KEY `idx_type` (`report_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备上报记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_report`
--

LOCK TABLES `equipment_report` WRITE;
/*!40000 ALTER TABLE `equipment_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `equipment_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inspection_record`
--

DROP TABLE IF EXISTS `inspection_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint DEFAULT '0' COMMENT '任务ID',
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `inspector_id` bigint NOT NULL COMMENT '检查人员ID',
  `inspector_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检查人员姓名',
  `check_type` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `check_date` datetime NOT NULL COMMENT '检查时间',
  `form_data` json DEFAULT NULL COMMENT '检查表单数据(含检查项和结果)',
  `issues` text COLLATE utf8mb4_unicode_ci COMMENT '问题描述',
  `evidence_images` json DEFAULT NULL COMMENT '证据图片[{url,watermark_info}]',
  `evidence_videos` json DEFAULT NULL COMMENT '证据视频[{url,watermark_info}]',
  `watermark_info` json DEFAULT NULL COMMENT '水印信息{time,location,enterprise,inspector}',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT' COMMENT 'DRAFT/SUBMITTED/NOTICE_GENERATED',
  `is_offline` tinyint DEFAULT '0' COMMENT '是否离线录入',
  `synced_at` datetime DEFAULT NULL COMMENT '同步时间',
  `rectification_notice_id` bigint DEFAULT NULL COMMENT '关联整改通知书ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `summary` text COLLATE utf8mb4_unicode_ci,
  `enterprise_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `attachment_urls` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_task` (`task_id`),
  KEY `idx_enterprise` (`enterprise_id`),
  KEY `idx_inspector` (`inspector_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场检查记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inspection_record`
--

LOCK TABLES `inspection_record` WRITE;
/*!40000 ALTER TABLE `inspection_record` DISABLE KEYS */;
INSERT INTO `inspection_record` VALUES (1,3,1,2,'张明','ROUTINE','2026-06-20 09:00:00',NULL,NULL,NULL,NULL,NULL,'COMPLETED',0,NULL,NULL,'2026-06-26 17:08:29','2026-06-26 17:08:29','现场检查合格，未发现问题','阿克苏市天山食品有限责任公司',NULL),(2,3,2,2,'张明','ROUTINE','2026-06-21 09:00:00',NULL,'1.生产车间卫生不达标;2.原料存放不规范',NULL,NULL,NULL,'COMPLETED',0,NULL,NULL,'2026-06-26 17:08:29','2026-06-26 17:08:29','检查发现2项问题，需整改','阿克苏华丰化工有限公司',NULL),(3,3,3,3,'李强','ROUTINE','2026-06-22 09:00:00',NULL,'1.特种设备未按期检验;2.安全标识缺失;3.操作人员无证上岗',NULL,NULL,NULL,'COMPLETED',0,NULL,NULL,'2026-06-26 17:08:29','2026-06-26 17:08:29','检查发现3项问题，需限期整改','阿克苏科钦建材有限公司',NULL),(7,0,1,1,'超级管理员','foodProduction_licenseCheck,foodProduction_hygieneCheck','2026-06-27 05:07:06',NULL,'license expired, hygiene issues','[]','[]',NULL,'SUBMITTED',0,NULL,NULL,'2026-06-27 05:07:06','2026-06-27 05:07:06',NULL,'阿克苏市天山食品有限责任公司','[]'),(8,0,2,1,'超级管理员','drugMedical_gmpCheck','2026-06-27 05:07:58',NULL,'GMP cert expired','[]','[]',NULL,'SUBMITTED',0,NULL,NULL,'2026-06-27 05:07:58','2026-06-27 05:07:58',NULL,'阿克苏华联商贸有限公司','[]'),(9,0,2,1,'超级管理员','drugMedical_gmpCheck','2026-06-27 05:12:15',NULL,'GMP cert expired','[]','[]',NULL,'SUBMITTED',0,NULL,NULL,'2026-06-27 05:12:15','2026-06-27 05:12:15',NULL,'阿克苏华联商贸有限公司','[]');
/*!40000 ALTER TABLE `inspection_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inspection_task`
--

DROP TABLE IF EXISTS `inspection_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_no` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `task_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划内/双随机/信访举报',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '任务描述',
  `inspection_focus` text COLLATE utf8mb4_unicode_ci COMMENT '检查重点',
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '待分配' COMMENT '待分配/待认领/在办/已完成/逾期/已终止',
  `random_enterprise_ratio` decimal(5,2) DEFAULT NULL COMMENT '企业抽取比例',
  `random_inspector_ratio` decimal(5,2) DEFAULT NULL COMMENT '人员抽取比例',
  `exclude_months` int DEFAULT NULL COMMENT '排除近N月已检查企业',
  `terminated_reason` text COLLATE utf8mb4_unicode_ci COMMENT '终止原因',
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `actual_start_time` datetime DEFAULT NULL,
  `actual_end_time` datetime DEFAULT NULL,
  `check_items` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_no` (`task_no`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`task_type`),
  KEY `idx_dates` (`start_date`,`end_date`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检查任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inspection_task`
--

LOCK TABLES `inspection_task` WRITE;
/*!40000 ALTER TABLE `inspection_task` DISABLE KEYS */;
INSERT INTO `inspection_task` VALUES (1,'IT2026060001','日常检查','2026年第二季度食品安全专项检查','对辖区内食品生产企业进行专项检查','生产环境、原料采购、产品检验','2026-06-26 14:39:49','2026-07-26 14:39:49','TERMINATED',30.00,50.00,NULL,'No_longer_needed','1','2026-06-19 14:39:49','2026-06-26 17:10:39',NULL,NULL,NULL),(2,'IT2026060002','专项检查','特种设备安全专项排查','重点排查特种设备使用单位安全隐患','设备登记、检验情况、操作人员资质','2026-06-26 14:39:49','2026-07-11 14:39:49','PENDING',20.00,40.00,NULL,NULL,'1','2026-06-23 14:39:49','2026-06-23 14:39:49',NULL,NULL,NULL),(3,'IT2026060003','随机抽查','双随机一公开抽查','随机抽取企业和检查人员','营业执照、经营行为、产品质量','2026-07-01 14:39:49','2026-08-10 14:39:49','PENDING',100.00,100.00,NULL,NULL,'1','2026-06-25 14:39:49','2026-06-25 14:39:49',NULL,NULL,NULL),(7,'TASK1782461305151','ROUTINE','E2E Test','Test',NULL,NULL,NULL,'PENDING',NULL,NULL,NULL,NULL,'admin','2026-06-26 16:08:25','2026-06-26 16:08:25',NULL,NULL,NULL),(8,'TASK1782461389349','ROUTINE','Write Test',NULL,NULL,NULL,NULL,'PENDING',NULL,NULL,NULL,NULL,'admin','2026-06-26 16:09:49','2026-06-26 16:09:49',NULL,NULL,NULL),(9,'TASK1782465039099','SPECIAL','Food Safety Special Inspection','Special inspection for food companies',NULL,NULL,NULL,'PENDING',NULL,NULL,NULL,NULL,'admin','2026-06-26 17:10:39','2026-06-26 17:10:39',NULL,NULL,NULL),(10,'TASK1782465039106','ROUTINE','Double-Random: Food Industry','Double-Random: ratio=30% | fields=food safety | items=hygiene,raw material',NULL,NULL,NULL,'PENDING',NULL,NULL,NULL,NULL,'admin','2026-06-26 17:10:39','2026-06-26 17:10:39',NULL,NULL,'hygiene,raw material,production environment'),(11,'TASK1782466662647','SPECIAL','AUTO_TEST','Test',NULL,NULL,NULL,'PENDING',NULL,NULL,NULL,NULL,'admin','2026-06-26 17:37:43','2026-06-26 17:37:43',NULL,NULL,NULL),(12,'TASK1782466710944','SPECIAL','UPD_TEST_V2','Updated',NULL,NULL,NULL,'TERMINATED',NULL,NULL,NULL,'Auto test','admin','2026-06-26 17:38:31','2026-06-26 17:38:31',NULL,NULL,NULL),(13,'TASK1782466777821','SPECIAL','AUTO_TEST_UPD','Upd',NULL,NULL,NULL,'TERMINATED',NULL,NULL,NULL,'Auto test','admin','2026-06-26 17:39:38','2026-06-26 17:39:38',NULL,NULL,NULL),(14,'TASK1782469266158','日常检查','顶顶顶顶','\n【双随机】抽查比例:30% | 检查领域: | 检查事项:',NULL,'2026-06-26 18:20:56','2026-06-26 18:20:58','PENDING',NULL,NULL,NULL,NULL,'admin','2026-06-26 18:21:06','2026-06-26 18:21:06',NULL,NULL,NULL);
/*!40000 ALTER TABLE `inspection_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inspection_template`
--

DROP TABLE IF EXISTS `inspection_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `industry` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '适用行业',
  `check_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检查类型',
  `template_content` json NOT NULL COMMENT '检查项模板[{group,items:[{name,standard,required}]}]',
  `status` tinyint DEFAULT '1' COMMENT '1-启用 0-禁用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_industry` (`industry`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检查表单模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inspection_template`
--

LOCK TABLES `inspection_template` WRITE;
/*!40000 ALTER TABLE `inspection_template` DISABLE KEYS */;
INSERT INTO `inspection_template` VALUES (1,'食品生产日常检查','食品','日常检查','[{\"group\": \"生产环境\", \"items\": [{\"name\": \"生产车间卫生状况\", \"required\": true, \"standard\": \"整洁无污染\"}, {\"name\": \"原料存储条件\", \"required\": true, \"standard\": \"分类存放、温度适宜\"}]}, {\"group\": \"生产流程\", \"items\": [{\"name\": \"生产工艺合规性\", \"required\": true, \"standard\": \"按标准工艺执行\"}, {\"name\": \"产品检验记录\", \"required\": true, \"standard\": \"批次检验记录完整\"}]}, {\"group\": \"人员管理\", \"items\": [{\"name\": \"健康证有效性\", \"required\": true, \"standard\": \"全员持有效健康证\"}, {\"name\": \"培训记录\", \"required\": false, \"standard\": \"定期培训有记录\"}]}]',1,'2026-06-26 03:50:10','2026-06-26 03:50:10'),(2,'特种设备定期检查','特种设备','定期检查','[{\"group\": \"设备本体\", \"items\": [{\"name\": \"设备外观完整性\", \"required\": true, \"standard\": \"无变形、无裂纹\"}, {\"name\": \"安全装置状态\", \"required\": true, \"standard\": \"功能正常、在校验期内\"}]}, {\"group\": \"运行状态\", \"items\": [{\"name\": \"仪表读数\", \"required\": true, \"standard\": \"在正常范围内\"}, {\"name\": \"运行声音\", \"required\": true, \"standard\": \"无异常声响\"}, {\"name\": \"安全装置外观\", \"required\": true, \"standard\": \"无损坏、标识清晰\"}]}, {\"group\": \"档案资料\", \"items\": [{\"name\": \"使用登记证\", \"required\": true, \"standard\": \"在有效期内\"}, {\"name\": \"检验报告\", \"required\": true, \"standard\": \"最新检验合格\"}]}]',1,'2026-06-26 03:50:10','2026-06-26 03:50:10');
/*!40000 ALTER TABLE `inspection_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'enterprise',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `msg_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '诉求通知/整改通知/报告提醒/系统通知/任务提醒/预警提醒',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `related_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联业务类型',
  `related_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `is_read` tinyint DEFAULT '0',
  `read_at` datetime DEFAULT NULL,
  `push_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待推送' COMMENT '待推送/已推送/推送失败',
  `push_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `link` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_type`,`user_id`),
  KEY `idx_type` (`msg_type`),
  KEY `idx_read` (`is_read`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,'admin',14,'APPEAL','新诉求通知','阿克苏市鑫达特种设备安装有限公司提交了压力容器定期检验申请',NULL,NULL,0,NULL,'待推送',NULL,'2026-06-25 14:39:49',NULL),(2,'admin',14,'TASK','任务分配通知','您已被分配到2026年第二季度食品安全专项检查任务',NULL,NULL,0,NULL,'待推送',NULL,'2026-06-24 14:39:49',NULL),(3,'admin',14,'ALERT','预警通知','特种设备安装许可证即将到期，请及时处理',NULL,NULL,1,NULL,'待推送',NULL,'2026-06-23 14:39:49',NULL),(7,'admin',1,'TASK','任务完成通知','食品安全专项检查任务已完成80%',NULL,NULL,0,NULL,'待推送',NULL,'2026-06-25 15:13:51',NULL),(9,'enterprise',4,'RECTIFICATION_NOTICE','Rectification Notice: ZG202606277299','checkType: drugMedical_gmpCheck\nissues: GMP cert expired\nrequirements: renew GMP within 10 days\ndeadline: 2026-07-20\ninspector: 超级管理员\n',NULL,4,0,NULL,'待推送',NULL,'2026-06-27 05:12:15','/rectification/4'),(10,'INSPECTOR',2,'TASK','New inspection task assigned','You have a new inspection task: Food safety special inspection, planned date 2026-06-20','INSPECTION_TASK',1,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/inspection/1'),(11,'INSPECTOR',2,'NOTIFICATION','Enterprise submitted rectification feedback','Tianshan Food has submitted rectification feedback, please review','RECTIFICATION_FEEDBACK',1,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/rectification/1'),(12,'ENTERPRISE',5,'RECTIFICATION','Rectification notice received','You received a rectification notice: Food safety, deadline 2026-07-05','RECTIFICATION_NOTICE',1,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/rectification/1'),(13,'INSPECTOR',3,'NOTIFICATION','Enterprise submitted rectification feedback','Xinglong Commerce has submitted rectification feedback, please review','RECTIFICATION_FEEDBACK',2,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/rectification/2'),(14,'ENTERPRISE',2,'RECTIFICATION','Rectification notice received','You received a rectification notice: Fire safety, deadline 2026-07-03','RECTIFICATION_NOTICE',2,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/rectification/2'),(15,'ENTERPRISE',3,'RECTIFICATION','Rectification notice received','You received a rectification notice: Special equipment safety, deadline 2026-06-30','RECTIFICATION_NOTICE',3,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/rectification/3'),(16,'ENTERPRISE',5,'RECTIFICATION','URGENT: Rectification notice received','You received an URGENT rectification notice: Chemical safety, immediate shutdown required, deadline 2026-06-25','RECTIFICATION_NOTICE',4,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/rectification/4'),(17,'INSPECTOR',4,'NOTIFICATION','Enterprise submitted rectification feedback','Shunda Logistics has submitted rectification feedback, please review','RECTIFICATION_FEEDBACK',5,0,NULL,'待推送',NULL,'2026-06-27 14:06:44','/rectification/5');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `org_structure`
--

DROP TABLE IF EXISTS `org_structure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `org_structure` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父级ID',
  `level` int NOT NULL COMMENT '层级:1地区,2县市,3乡镇街道,4单位,5科室',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态:1启用,0禁用',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `org_code` varchar(50) DEFAULT NULL COMMENT '组织编码，如 AKS-001',
  `org_path` varchar(500) DEFAULT NULL COMMENT '路径，如 /1/2/5/',
  `org_type` varchar(50) DEFAULT NULL COMMENT '组织类型：企业/机关/事业单位/社会团体',
  `unified_social_credit_code` varchar(18) DEFAULT NULL COMMENT '统一社会信用代码',
  `leader_name` varchar(100) DEFAULT NULL COMMENT '负责人姓名',
  `leader_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `address` varchar(500) DEFAULT NULL COMMENT '地址',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `region_sub_type` varchar(20) DEFAULT NULL COMMENT '县级行政区划细分：CITY-县级市, COUNTY-县',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织架构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `org_structure`
--

LOCK TABLES `org_structure` WRITE;
/*!40000 ALTER TABLE `org_structure` DISABLE KEYS */;
INSERT INTO `org_structure` VALUES (1,'阿克苏地区',NULL,1,1,1,'阿克苏地区行政公署','2026-06-27 00:36:22','2026-06-27 01:36:02','ORG-0001','/1/',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'阿克苏市',1,2,1,1,'阿克苏市','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0002','/1/2/',NULL,NULL,NULL,NULL,NULL,NULL,'CITY'),(3,'库车市',1,2,2,1,'库车市','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0003','/1/3/',NULL,NULL,NULL,NULL,NULL,NULL,'CITY'),(4,'温宿县',1,2,3,1,'温宿县','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0004','/1/4/',NULL,NULL,NULL,NULL,NULL,NULL,'COUNTY'),(5,'拜城县',1,2,4,1,'拜城县','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0005','/1/5/',NULL,NULL,NULL,NULL,NULL,NULL,'COUNTY'),(6,'新和县',1,2,5,1,'新和县','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0006','/1/6/',NULL,NULL,NULL,NULL,NULL,NULL,'COUNTY'),(7,'沙雅县',1,2,6,1,'沙雅县','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0007','/1/7/',NULL,NULL,NULL,NULL,NULL,NULL,'COUNTY'),(8,'乌什县',1,2,7,1,'乌什县','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0008','/1/8/',NULL,NULL,NULL,NULL,NULL,NULL,'COUNTY'),(9,'阿瓦提县',1,2,8,1,'阿瓦提县','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0009','/1/9/',NULL,NULL,NULL,NULL,NULL,NULL,'COUNTY'),(10,'柯坪县',1,2,9,1,'柯坪县','2026-06-27 00:36:22','2026-06-27 14:30:40','ORG-0010','/1/10/',NULL,NULL,NULL,NULL,NULL,NULL,'COUNTY'),(12,'综合协调部（办公室/财务与审计科）',1,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_ZHB','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(13,'营商环境服务部（行政审批科/登记注册科）',1,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_YSHJ','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(14,'创新监管部（信用监管科/科技信息化科）',1,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_CXJG','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(15,'食品安全工作部（食品安全协调科/食品生产流通安全监管科/餐饮食品安全监管科）',1,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_SPAQ','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(16,'法治规划部（法规科）',1,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_FZGH','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(17,'综合执法部（执法稽查科/反垄断和反不正当竞争科/价格监督检查科/网络交易和广告监督管理科）',1,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_ZFZD','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(18,'质量安全工作部（质量发展监管科/计量科/标准化科/纤维质量监管科）',1,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_ZLAQ','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(19,'特种设备安全工作部（特种设备安全监察科）',1,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_TZSB','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(20,'知识产权部（知识产权科）',1,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_ZHISH','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(21,'党建人事工作部（组织人事科）',1,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_DJRS','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(22,'药械安全工作部（风险监测抽检科/药品和医疗器械监管科/化妆品监管科）',1,5,11,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_YXAQ','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(23,'阿克苏纺织工业城（开发区）市场监督管理分局',1,5,12,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','REGION_FZFG','/1/1/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(24,'办公室',2,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_BGS','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(25,'法规科',2,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_FGK','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(26,'注册登记科',2,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_ZCS','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(27,'行政审批科',2,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_XZSP','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(28,'信用监管科',2,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_XYJG','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(29,'食品安全监管科',2,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_SPAQ','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(30,'药品化妆品监管科',2,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_CYPZ','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(31,'医疗器械监管科',2,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_YLQX','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(32,'质量监管科',2,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_ZLJG','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(33,'特种设备安全监察科',2,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_TZSB','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(34,'计量科',2,5,11,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_JLK','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(35,'标准化科',2,5,12,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_BZK','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(36,'价格监督检查科',2,5,13,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_JGJC','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(37,'反垄断和反不正当竞争科',2,5,14,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_FLD','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(38,'网络交易和广告监管科',2,5,15,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_WLJG','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(39,'知识产权保护科',2,5,16,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_ZHISH','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(40,'消费者权益保护科',2,5,17,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_XFQY','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(41,'执法稽查科',2,5,18,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_ZFZD','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(42,'科技信息化科',2,5,19,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','CITY_SJXX','/1/2/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(43,'办公室',3,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_BGS','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(44,'注册登记科',3,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_ZCS','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(45,'信用监管科',3,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_XYJG','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(46,'食品安全监管科',3,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_SPAQ','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(47,'药品化妆品监管科',3,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_CYPZ','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(48,'质量监管科',3,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_ZLJG','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(49,'特种设备安全监察科',3,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_TZSB','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(50,'价格监督检查科',3,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_JGJC','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(51,'执法稽查科',3,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_ZFZD','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(52,'消费者权益保护科',3,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KUCHE_XFQY','/1/3/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(53,'办公室',4,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_BGS','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(54,'注册登记科',4,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_ZCS','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(55,'信用监管科',4,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_XYJG','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(56,'食品安全监管科',4,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_SPAQ','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(57,'药品化妆品监管科',4,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_CYPZ','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(58,'质量监管科',4,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_ZLJG','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(59,'特种设备安全监察科',4,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_TZSB','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(60,'价格监督检查科',4,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_JGJC','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(61,'执法稽查科',4,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_ZFZD','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(62,'消费者权益保护科',4,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WENSU_XFQY','/1/4/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(63,'办公室',5,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_BGS','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(64,'注册登记科',5,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_ZCS','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(65,'信用监管科',5,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_XYJG','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(66,'食品安全监管科',5,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_SPAQ','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(67,'药品化妆品监管科',5,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_CYPZ','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(68,'质量监管科',5,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_ZLJG','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(69,'特种设备安全监察科',5,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_TZSB','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(70,'价格监督检查科',5,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_JGJC','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(71,'执法稽查科',5,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_ZFZD','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(72,'消费者权益保护科',5,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','BAICHENG_XFQY','/1/5/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(73,'办公室',6,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_BGS','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(74,'注册登记科',6,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_ZCS','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(75,'信用监管科',6,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_XYJG','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(76,'食品安全监管科',6,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_SPAQ','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(77,'药品化妆品监管科',6,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_CYPZ','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(78,'质量监管科',6,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_ZLJG','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(79,'特种设备安全监察科',6,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_TZSB','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(80,'价格监督检查科',6,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_JGJC','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(81,'执法稽查科',6,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_ZFZD','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(82,'消费者权益保护科',6,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','XINHE_XFQY','/1/6/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(83,'办公室',7,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_BGS','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(84,'注册登记科',7,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_ZCS','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(85,'信用监管科',7,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_XYJG','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(86,'食品安全监管科',7,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_SPAQ','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(87,'药品化妆品监管科',7,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_CYPZ','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(88,'质量监管科',7,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_ZLJG','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(89,'特种设备安全监察科',7,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_TZSB','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(90,'价格监督检查科',7,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_JGJC','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(91,'执法稽查科',7,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_ZFZD','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(92,'消费者权益保护科',7,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','SHAYA_XFQY','/1/7/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(93,'办公室',8,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_BGS','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(94,'注册登记科',8,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_ZCS','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(95,'信用监管科',8,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_XYJG','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(96,'食品安全监管科',8,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_SPAQ','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(97,'药品化妆品监管科',8,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_CYPZ','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(98,'质量监管科',8,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_ZLJG','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(99,'特种设备安全监察科',8,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_TZSB','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(100,'价格监督检查科',8,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_JGJC','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(101,'执法稽查科',8,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_ZFZD','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(102,'消费者权益保护科',8,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','WUSHI_XFQY','/1/8/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(103,'办公室',9,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_BGS','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(104,'注册登记科',9,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_ZCS','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(105,'信用监管科',9,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_XYJG','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(106,'食品安全监管科',9,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_SPAQ','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(107,'药品化妆品监管科',9,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_CYPZ','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(108,'质量监管科',9,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_ZLJG','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(109,'特种设备安全监察科',9,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_TZSB','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(110,'价格监督检查科',9,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_JGJC','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(111,'执法稽查科',9,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_ZFZD','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(112,'消费者权益保护科',9,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','AWATI_XFQY','/1/9/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(113,'办公室',10,5,1,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_BGS','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(114,'注册登记科',10,5,2,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_ZCS','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(115,'信用监管科',10,5,3,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_XYJG','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(116,'食品安全监管科',10,5,4,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_SPAQ','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(117,'药品化妆品监管科',10,5,5,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_CYPZ','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(118,'质量监管科',10,5,6,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_ZLJG','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(119,'特种设备安全监察科',10,5,7,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_TZSB','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(120,'价格监督检查科',10,5,8,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_JGJC','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(121,'执法稽查科',10,5,9,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_ZFZD','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL),(122,'消费者权益保护科',10,5,10,1,NULL,'2026-06-27 14:46:04','2026-06-27 14:46:04','KEPING_XFQY','/1/10/','GOVERNMENT',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `org_structure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rectification_feedback`
--

DROP TABLE IF EXISTS `rectification_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rectification_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notice_id` bigint NOT NULL COMMENT '整改通知书ID',
  `enterprise_id` bigint NOT NULL,
  `measures` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '整改措施描述',
  `after_images` json DEFAULT NULL COMMENT '整改后图片',
  `after_videos` json DEFAULT NULL COMMENT '整改后视频',
  `third_party_reports` json DEFAULT NULL COMMENT '第三方补充报告',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待提交' COMMENT '待提交/审核中/验收合格/验收不合格',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` text COLLATE utf8mb4_unicode_ci,
  `check_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检查类型代码',
  `attachment_urls` json DEFAULT NULL COMMENT '附件URL列表',
  PRIMARY KEY (`id`),
  KEY `idx_notice` (`notice_id`),
  KEY `idx_enterprise` (`enterprise_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='整改反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rectification_feedback`
--

LOCK TABLES `rectification_feedback` WRITE;
/*!40000 ALTER TABLE `rectification_feedback` DISABLE KEYS */;
INSERT INTO `rectification_feedback` VALUES (3,4,1,'All 4 issues rectified: drainage repaired, cold storage recorder installed, health certs renewed, additive ledger updated','[\"/uploads/feedback/1/drainage.jpg\", \"/uploads/feedback/1/recorder.jpg\"]','[\"/uploads/feedback/1/workshop_video.mp4\"]',NULL,'SUBMITTED','2026-06-27 14:06:19','2026-06-27 14:06:19','Cost 23000 CNY','SPSC','[\"/uploads/feedback/1/report.pdf\"]'),(4,2,2,'All 3 issues rectified: fire passage cleared, merchandise shelved, extinguishers replaced','[\"/uploads/feedback/2/fire_passage.jpg\", \"/uploads/feedback/2/new_extinguisher.jpg\"]',NULL,NULL,'SUBMITTED','2026-06-27 14:06:19','2026-06-27 14:06:19','Cost 6000 CNY','SMLT','[\"/uploads/feedback/2/report.pdf\"]'),(5,5,3,'Partially rectified: forklift registration renewed, limit switch replaced','[\"/uploads/feedback/3/forklift_cert.jpg\"]','[\"/uploads/feedback/3/limit_switch_test.mp4\"]',NULL,'SUBMITTED','2026-06-27 14:06:19','2026-06-27 14:06:19','Valve calibration pending','TZSB','[\"/uploads/feedback/3/progress.pdf\"]'),(6,7,5,'Emergency rectification completed: anti-leakage installed, chemicals separated, safety signs added, emergency cabinet deployed','[\"/uploads/feedback/5/anti_leakage.jpg\", \"/uploads/feedback/5/chemical_separation.jpg\", \"/uploads/feedback/5/safety_signs.jpg\"]','[\"/uploads/feedback/5/warehouse_overview.mp4\"]',NULL,'SUBMITTED','2026-06-27 14:06:44','2026-06-27 14:06:44','Emergency plan filing in progress','HGKC','[\"/uploads/feedback/5/report.pdf\"]'),(7,8,7,'All 3 issues rectified: vehicle inspections done, GPS camera fixed, loading records completed','[\"/uploads/feedback/7/vehicle_inspection.jpg\", \"/uploads/feedback/7/gps_repair.jpg\"]','[\"/uploads/feedback/7/vehicle_video.mp4\"]',NULL,'SUBMITTED','2026-06-27 14:06:44','2026-06-27 14:06:44','Cost 3500 CNY','WLYS','[\"/uploads/feedback/7/report.pdf\"]');
/*!40000 ALTER TABLE `rectification_feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rectification_notice`
--

DROP TABLE IF EXISTS `rectification_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rectification_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notice_no` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知书编号',
  `inspection_record_id` bigint NOT NULL COMMENT '关联检查记录ID',
  `enterprise_id` bigint NOT NULL,
  `task_id` bigint DEFAULT '0',
  `check_date` datetime DEFAULT NULL COMMENT '检查时间',
  `inspector_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执法人员',
  `issues` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题描述',
  `requirements` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '整改要求',
  `deadline` date NOT NULL COMMENT '整改截止日期',
  `evidence_images` json DEFAULT NULL COMMENT '问题佐证图片(带水印)',
  `evidence_videos` json DEFAULT NULL COMMENT '问题佐证视频(带水印)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ISSUED',
  `is_read` tinyint DEFAULT '0' COMMENT '企业是否已读',
  `read_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `inspector_id` bigint NOT NULL DEFAULT '0',
  `attachment_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `notice_no` (`notice_no`),
  KEY `idx_enterprise` (`enterprise_id`),
  KEY `idx_status` (`status`),
  KEY `idx_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='整改通知书表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rectification_notice`
--

LOCK TABLES `rectification_notice` WRITE;
/*!40000 ALTER TABLE `rectification_notice` DISABLE KEYS */;
INSERT INTO `rectification_notice` VALUES (1,'ZG20260622001',2,2,3,'2026-06-21 09:00:00','张明','1.生产车间卫生不达标;2.原料存放不规范','限期整改，确保生产环境达标','2026-07-20',NULL,NULL,'已下发',0,NULL,'2026-06-26 17:08:29','2026-06-26 17:08:29',2,NULL),(2,'ZG20260622002',3,3,3,'2026-06-22 09:00:00','李强','1.特种设备未按期检验;2.安全标识缺失;3.操作人员无证上岗','立即整改，15日内完成','2026-07-15',NULL,NULL,'ISSUED',0,NULL,'2026-06-26 17:08:29','2026-06-26 19:36:05',3,NULL),(4,'ZG202606277299',9,2,0,NULL,NULL,'GMP cert expired','renew GMP within 10 days','2026-07-20',NULL,NULL,'ISSUED',0,NULL,'2026-06-27 05:12:15','2026-06-27 05:12:15',1,'[]'),(5,'ZG20260620001',1,1,1,'2026-06-20 00:00:00','张建国','1.生产车间地面有积水排水不畅;2.冷库温度记录不完整6月15-17日缺失;3.从业人员健康证3张已过期;4.食品添加剂使用台账记录不规范','1.修缮车间排水系统;2.完善冷库温度记录机制;3.立即安排过期人员重新体检;4.按规范重新登记添加剂台账。请于2026年7月5日前完成整改。','2026-07-05',NULL,NULL,'ISSUED',0,NULL,'2026-06-27 14:03:37','2026-06-27 14:03:37',2,NULL),(6,'ZG20260615001',3,3,3,'2026-06-15 00:00:00','张建国','1.2台叉车使用登记证已过期;2.压力容器安全阀校验报告缺失;3.特种设备操作人员1人无有效资质证书;4.起重机械限位装置失灵','1.立即重新办理叉车使用登记证;2.安排安全阀校验;3.无证人员停止操作并安排培训取证;4.更换限位装置。请于2026年6月30日前完成整改。','2026-06-30',NULL,NULL,'ISSUED',0,NULL,'2026-06-27 14:03:37','2026-06-27 14:03:37',2,NULL),(7,'ZG20260610001',4,5,5,'2026-06-10 00:00:00','李明远','1.危化品仓库未设置防泄漏设施;2.部分化学品混放存在反应风险;3.应急预案未按规定备案;4.仓库区域安全标识缺失严重;5.未配备应急物资柜','1.立即停业整改;2.安装防泄漏设施;3.分类存放化学品;4.完成应急预案备案;5.补全安全标识;6.配备应急物资柜。请于2026年6月25日前完成整改。','2026-06-25',NULL,NULL,'ISSUED',0,NULL,'2026-06-27 14:03:37','2026-06-27 14:03:37',3,NULL),(8,'ZG20260612001',6,7,7,'2026-06-12 00:00:00','王秀芳','1.2辆运输车辆年检即将到期;2.GPS监控平台1路摄像头故障;3.货物装载记录部分缺失','1.及时完成车辆年检;2.修复GPS监控平台摄像头;3.完善货物装载记录。请于2026年7月10日前完成整改。','2026-07-10',NULL,NULL,'ISSUED',0,NULL,'2026-06-27 14:03:37','2026-06-27 14:03:37',4,NULL);
/*!40000 ALTER TABLE `rectification_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text COLLATE utf8mb4_unicode_ci,
  `config_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'string' COMMENT 'string/number/json',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `config_group` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'system.name','uv','string','Updated','2026-06-26 03:50:10','2026-06-26 17:32:37',NULL),(2,'file.max_size','209715200','number','文件上传最大字节数(200MB)','2026-06-26 03:50:10','2026-06-26 03:50:10',NULL),(3,'file.allowed_formats','JPG,PNG,MP4,PDF','string','允许上传的文件格式','2026-06-26 03:50:10','2026-06-26 03:50:10',NULL),(4,'report.warning_days','30,15,7','string','报告到期预警天数','2026-06-26 03:50:10','2026-06-26 03:50:10',NULL),(5,'rectification.default_days','30','number','默认整改天数','2026-06-26 03:50:10','2026-06-26 03:50:10',NULL),(6,'message.retain_months','3','number','消息保留月数','2026-06-26 03:50:10','2026-06-26 03:50:10',NULL),(7,'log.retain_months','12','number','日志保留月数','2026-06-26 03:50:10','2026-06-26 03:50:10',NULL),(12,'inspection.deadline','45','string','Updated','2026-06-26 17:10:39','2026-06-26 17:11:53','inspection'),(13,'auto.test.key','tv','string','Auto test','2026-06-26 17:32:37','2026-06-26 17:32:37',NULL),(14,'auto.test.key3','tv3','string','Auto test','2026-06-26 17:37:34','2026-06-26 17:37:34',NULL),(15,'auto.test.key5','tv5','string','Auto test 5','2026-06-26 17:38:31','2026-06-26 17:38:31',NULL),(16,'auto.test.k1782466777','tv','string','Auto test','2026-06-26 17:39:38','2026-06-26 17:39:38',NULL);
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_title`
--

DROP TABLE IF EXISTS `sys_job_title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_title` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title_code` varchar(50) NOT NULL COMMENT '职务编码',
  `title_name` varchar(100) NOT NULL COMMENT '职务名称',
  `category` varchar(20) DEFAULT NULL COMMENT '类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政',
  `is_leadership` tinyint NOT NULL DEFAULT '0' COMMENT '是否领导职务：1是,0否',
  `level` int NOT NULL DEFAULT '1' COMMENT '职务级别',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：1启用,0禁用',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `role_id` bigint DEFAULT NULL COMMENT '关联角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_title_code` (`title_code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_title`
--

LOCK TABLES `sys_job_title` WRITE;
/*!40000 ALTER TABLE `sys_job_title` DISABLE KEYS */;
INSERT INTO `sys_job_title` VALUES (1,'TITLE-DIRECTOR','局长','MANAGEMENT',1,1,1,0,NULL,'2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(2,'TITLE-DEPUTY','副局长','MANAGEMENT',1,2,1,0,NULL,'2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(3,'TITLE-CHIEF','科长','MANAGEMENT',1,3,1,0,NULL,'2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(4,'TITLE-DEPUTY-CHIEF','副科长','MANAGEMENT',1,4,1,0,NULL,'2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(5,'TITLE-SENIOR','高级工程师','TECHNICAL',0,5,1,0,NULL,'2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(6,'TITLE-ENGINEER','工程师','TECHNICAL',0,6,1,0,NULL,'2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(7,'TITLE-CLERK','科员','ADMIN',0,7,1,0,NULL,'2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(8,'JT_TEST01','测试职务','MANAGEMENT',1,3,1,0,NULL,'2026-06-26 19:47:11','2026-06-26 19:47:11',NULL);
/*!40000 ALTER TABLE `sys_job_title` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_operation_log`
--

DROP TABLE IF EXISTS `sys_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operation` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型',
  `method` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `params` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `http_method` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `result` text COLLATE utf8mb4_unicode_ci,
  `duration` bigint DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `error_msg` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
INSERT INTO `sys_operation_log` VALUES (1,2,'testadmin','登录系统','POST','{\"username\":\"testadmin\"}','192.168.1.100','2026-06-25 15:08:24','/api/auth/login','POST','success',120,'SUCCESS',NULL),(2,2,'testadmin','查看企业列表','GET','{\"page\":1,\"size\":10}','192.168.1.100','2026-06-25 15:08:24','/api/admin/enterprise/list','GET','success',85,'SUCCESS',NULL),(3,2,'testadmin','查看诉求列表','GET','{\"page\":1,\"size\":10}','192.168.1.100','2026-06-24 15:08:24','/api/admin/appeal/list','GET','success',60,'SUCCESS',NULL),(4,2,'testadmin','创建检查任务','POST','{\"title\":\"食品安全专项检查\"}','192.168.1.100','2026-06-24 15:08:24','/api/admin/task','POST','success',350,'SUCCESS',NULL),(5,2,'testadmin','查看预警列表','GET','{\"page\":1,\"size\":10}','192.168.1.100','2026-06-23 15:08:24','/api/inspector/alert/list','GET','success',45,'SUCCESS',NULL),(6,2,'testadmin','修改参数配置','PUT','{\"key\":\"inspection.remind.days\",\"value\":\"7\"}','192.168.1.100','2026-06-23 15:08:24','/api/admin/system/config/1','PUT','success',180,'SUCCESS',NULL),(7,1,'admin','登录系统','AuthController.login',NULL,'192.168.1.100','2026-06-26 12:08:29','/api/auth/login','POST',NULL,NULL,'SUCCESS',NULL),(8,1,'admin','查看企业列表','EnterpriseController.list',NULL,'192.168.1.100','2026-06-26 13:08:29','/api/admin/enterprise/list','GET',NULL,NULL,'SUCCESS',NULL),(9,2,'inspector1','登录系统','AuthController.login',NULL,'192.168.1.101','2026-06-26 14:08:29','/api/auth/login','POST',NULL,NULL,'SUCCESS',NULL),(10,1,'admin','创建任务','TaskController.create',NULL,'192.168.1.100','2026-06-26 15:08:29','/api/admin/task','POST',NULL,NULL,'SUCCESS',NULL),(11,2,'inspector1','提交检查','InspectionController.submit',NULL,'192.168.1.101','2026-06-26 16:08:29','/api/inspector/inspection/submit','POST',NULL,NULL,'SUCCESS',NULL),(12,1,'admin','审核报告','ReportController.review',NULL,'192.168.1.100','2026-06-26 16:38:29','/api/admin/report/1/review','PUT',NULL,NULL,'SUCCESS',NULL),(13,1,'admin','分流诉求','AppealController.assign',NULL,'192.168.1.100','2026-06-26 16:48:29','/api/admin/appeal/1/assign','PUT',NULL,NULL,'SUCCESS',NULL),(14,5,'admin2','查看Dashboard','DashboardController.overview',NULL,'192.168.1.102','2026-06-26 16:58:29','/api/admin/dashboard/overview','GET',NULL,NULL,'SUCCESS',NULL);
/*!40000 ALTER TABLE `sys_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `resource_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'menu/button/api',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID',
  `sort_order` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  `module` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `method` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_code` (`permission_code`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (13,'appeal:view','查看诉求','menu',0,1,1,'2026-06-26 17:08:29',NULL,'诉求管理','/appeal','GET',NULL),(14,'appeal:assign','分流诉求','button',1,2,1,'2026-06-26 17:08:29',NULL,'诉求管理','/admin/appeal/*/assign','PUT',NULL),(15,'appeal:handle','处理诉求','button',1,3,1,'2026-06-26 17:08:29',NULL,'诉求管理','/admin/appeal/*/handle','PUT',NULL),(16,'task:view','查看任务','menu',0,4,1,'2026-06-26 17:08:29',NULL,'任务调度','/task','GET',NULL),(17,'task:create','创建任务','button',4,5,1,'2026-06-26 17:08:29',NULL,'任务调度','/admin/task','POST',NULL),(18,'task:terminate','终止任务','button',4,6,1,'2026-06-26 17:08:29',NULL,'任务调度','/admin/task/*/terminate','POST',NULL),(19,'enterprise:view','查看企业','menu',0,7,1,'2026-06-26 17:08:29',NULL,'数据管理','/enterprise','GET',NULL),(20,'enterprise:edit','编辑企业','button',7,8,1,'2026-06-26 17:08:29',NULL,'数据管理','/admin/enterprise/*','PUT',NULL),(21,'report:review','审核报告','button',7,9,1,'2026-06-26 17:08:29',NULL,'数据管理','/admin/report/*/review','PUT',NULL),(22,'system:user','用户管理','menu',0,10,1,'2026-06-26 17:08:29',NULL,'系统管理','/system/user','GET',NULL),(23,'system:role','角色管理','menu',0,11,1,'2026-06-26 17:08:29',NULL,'系统管理','/system/role','GET',NULL),(24,'system:config','参数配置','menu',0,12,1,'2026-06-26 17:08:29',NULL,'系统管理','/system/config','GET',NULL);
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_position`
--

DROP TABLE IF EXISTS `sys_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_position` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `position_code` varchar(50) NOT NULL COMMENT '岗位编码',
  `position_name` varchar(100) NOT NULL COMMENT '岗位名称',
  `category` varchar(20) DEFAULT NULL COMMENT '类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政',
  `salary_range_min` decimal(10,2) DEFAULT NULL COMMENT '最低薪资',
  `salary_range_max` decimal(10,2) DEFAULT NULL COMMENT '最高薪资',
  `org_id` bigint DEFAULT NULL COMMENT '所属组织ID',
  `level` int NOT NULL DEFAULT '1' COMMENT '岗位级别',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：1启用,0禁用',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `role_id` bigint DEFAULT NULL COMMENT '关联角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_position_code` (`position_code`),
  KEY `org_id` (`org_id`),
  CONSTRAINT `sys_position_ibfk_1` FOREIGN KEY (`org_id`) REFERENCES `org_structure` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_position`
--

LOCK TABLES `sys_position` WRITE;
/*!40000 ALTER TABLE `sys_position` DISABLE KEYS */;
INSERT INTO `sys_position` VALUES (1,'POS-ADMIN','系统管理员','ADMIN',NULL,NULL,NULL,1,1,1,'负责系统运维、备份，无业务数据权','2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(2,'POS-SECURITY','安全管理员','ADMIN',NULL,NULL,NULL,1,1,2,'负责制定安全策略、分配角色','2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(3,'POS-AUDIT','审计管理员','ADMIN',NULL,NULL,NULL,1,1,3,'负责查看日志、独立监督','2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(4,'POS-INSPECTOR','执法人员','TECHNICAL',NULL,NULL,NULL,2,1,4,'负责执法检查、企业巡查','2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(5,'POS-CLERK','科室人员','ADMIN',NULL,NULL,NULL,3,1,5,'科室日常工作人员','2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(6,'POS-LEADER','部门领导','MANAGEMENT',NULL,NULL,NULL,1,1,6,'部门负责人，拥有部门数据权限','2026-06-26 17:36:03','2026-06-26 17:36:03',NULL),(7,'POS_TEST01','测试岗位','TECHNICAL',NULL,NULL,NULL,5,1,0,NULL,'2026-06-26 19:47:11','2026-06-26 19:47:11',NULL);
/*!40000 ALTER TABLE `sys_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1' COMMENT '1-启用 0-禁用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `parent_id` bigint DEFAULT NULL COMMENT '父角色ID（角色继承）',
  `role_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色类型：ADMIN系统管理员/SECURITY安全管理员/AUDIT审计管理员/BUSINESS业务角色',
  `data_scope` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ALL' COMMENT '数据权限范围：ALL全部/DEPT本部门/SELF仅自己/CUSTOM自定义',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'ADMIN','系统管理员','拥有所有权限',1,'2026-06-26 03:50:10','2026-06-27 01:36:03',NULL,'BUSINESS','ALL',0),(2,'LEADER','科室负责人','科室管理、任务审核',1,'2026-06-26 03:50:10','2026-06-27 01:36:03',NULL,'BUSINESS','ALL',0),(3,'INSPECTOR','执法人员','现场检查、整改验收',1,'2026-06-26 03:50:10','2026-06-27 01:36:03',NULL,'BUSINESS','ALL',0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名/工号',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(BCrypt)',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属科室',
  `area_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责区域编码',
  `industry_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责行业编码',
  `user_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'admin/inspector/leader',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wx_openid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1' COMMENT '1-启用 0-禁用',
  `last_login_at` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$pCnP67YHuIiQozl2tXJSEeUUPoC4P0N4G9a9I1QOq1SvfadRLvxya','超级管理员',NULL,NULL,NULL,NULL,NULL,'admin',NULL,NULL,1,NULL,'2026-06-26 03:50:10','2026-06-27 03:41:56'),(14,'inspector1','$2a$10$t08wgFWWuqNhha9EsXId9eOdGOGRtupJvMzrr2ySeJ1ROWAFUz7G6','Zhang_Ming_updated','13800138011','zhangming@aksu.gov.cn','食品安全监管科',NULL,NULL,'inspector',NULL,NULL,1,NULL,'2026-06-26 17:08:29','2026-06-26 19:06:26'),(15,'inspector2','$2b$10$gGDIpOZqwd0aq0hceleRO.ocjYx8yM7pfTk9hZStfgeZLktOQ0w.i','李强','13800138002','liqiang@aksu.gov.cn','特种设备监管科',NULL,NULL,'inspector',NULL,NULL,1,NULL,'2026-06-26 17:08:29','2026-06-26 19:06:24'),(16,'ent_user1','$2a$10$vVyNIfaBfBiVra2VIc60A.5mb5oJAu8S4CgUvKXnibK9XnI6PmWdu','王建华','13900139001','wangjh@tianshan.com',NULL,NULL,NULL,'enterprise',NULL,NULL,1,NULL,'2026-06-26 17:08:29','2026-06-26 19:06:26'),(17,'ent_user2','$2b$10$gGDIpOZqwd0aq0hceleRO.ocjYx8yM7pfTk9hZStfgeZLktOQ0w.i','刘丽','13900139002','liuli@huafeng.com',NULL,NULL,NULL,'enterprise',NULL,NULL,1,NULL,'2026-06-26 17:08:29','2026-06-26 19:06:24'),(18,'admin2','$2b$10$gGDIpOZqwd0aq0hceleRO.ocjYx8yM7pfTk9hZStfgeZLktOQ0w.i','赵主任','13700137001','zhao@aksu.gov.cn','综合管理科',NULL,NULL,'admin',NULL,NULL,1,NULL,'2026-06-26 17:08:29','2026-06-26 19:06:24'),(19,'auto_tst_x2','$2a$10$SYOc2M9RskL/hgJrzs.1Y.aTPy4hDok.R64/Rr7l2g45VKPWWEOgi','AT','13900008877',NULL,NULL,NULL,NULL,'enterprise',NULL,NULL,1,NULL,'2026-06-26 17:32:37','2026-06-26 17:32:37'),(20,'auto_reg_test','$2a$10$i87YFG5EcL7K3fH/K3BUseEATmHDig86RdeK4ofrgxN5SdGdtKwAq','AutoTest','13877779999',NULL,NULL,NULL,NULL,'enterprise',NULL,NULL,1,NULL,'2026-06-26 17:36:48','2026-06-26 17:36:48'),(21,'auto_r_7957','$2a$10$LLWfys9NeezZAsHIO0/WHuJzwyPavsAj8qSZhX5UEdjr.bYidmkCG','AutoTest','13898676109',NULL,NULL,NULL,NULL,'enterprise',NULL,NULL,1,NULL,'2026-06-26 17:38:31','2026-06-26 17:38:31'),(22,'at_1782466777','$2a$10$APMN94Ku.4ZyGqbkseXZIuf4rbqOVGmm7tCLa.PbTeNYGOkWcAZ5O','AutoTest','13882466777',NULL,NULL,NULL,NULL,'enterprise',NULL,NULL,1,NULL,'2026-06-26 17:39:38','2026-06-26 17:39:38'),(23,'test_ent_user','$2a$10$VzltH7DN/hj9huHH.mo8luevsPq2cY89T8.6yYXTrvqae1Lr51iYi','测试用户','13800001111',NULL,NULL,NULL,NULL,'enterprise_user',NULL,NULL,1,NULL,'2026-06-26 19:08:39','2026-06-26 19:08:39'),(24,'test_e2e_user','$2a$10$P33wbZ0usww9/klV5yJgGeK2psNAZeSKA8mX.Mg5V2/gNOW2ozq7.','E2E','13900009999',NULL,NULL,NULL,NULL,'enterprise_user',NULL,NULL,1,NULL,'2026-06-26 19:09:02','2026-06-26 19:09:02');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_position`
--

DROP TABLE IF EXISTS `sys_user_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_position` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `org_id` bigint NOT NULL COMMENT '组织ID',
  `position_id` bigint DEFAULT NULL COMMENT '岗位ID',
  `job_title_id` bigint DEFAULT NULL COMMENT '职务ID',
  `is_primary` tinyint NOT NULL DEFAULT '1' COMMENT '是否主职：1主职,0兼职',
  `start_date` date DEFAULT NULL COMMENT '任职开始日期',
  `end_date` date DEFAULT NULL COMMENT '任职结束日期',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：1在职,0离职',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_org` (`user_id`,`org_id`),
  KEY `org_id` (`org_id`),
  KEY `position_id` (`position_id`),
  KEY `job_title_id` (`job_title_id`),
  CONSTRAINT `sys_user_position_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `sys_user_position_ibfk_2` FOREIGN KEY (`org_id`) REFERENCES `org_structure` (`id`) ON DELETE CASCADE,
  CONSTRAINT `sys_user_position_ibfk_3` FOREIGN KEY (`position_id`) REFERENCES `sys_position` (`id`) ON DELETE SET NULL,
  CONSTRAINT `sys_user_position_ibfk_4` FOREIGN KEY (`job_title_id`) REFERENCES `sys_job_title` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户岗位职务关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_position`
--

LOCK TABLES `sys_user_position` WRITE;
/*!40000 ALTER TABLE `sys_user_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,1);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_enterprise`
--

DROP TABLE IF EXISTS `task_enterprise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_enterprise` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL,
  `enterprise_id` bigint NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待检查' COMMENT '待检查/检查中/已完成',
  `inspection_result` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_enterprise` (`task_id`,`enterprise_id`),
  KEY `idx_task` (`task_id`),
  KEY `idx_enterprise` (`enterprise_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务-企业关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_enterprise`
--

LOCK TABLES `task_enterprise` WRITE;
/*!40000 ALTER TABLE `task_enterprise` DISABLE KEYS */;
INSERT INTO `task_enterprise` VALUES (1,7,1,'PENDING',NULL,'2026-06-26 16:08:25'),(2,7,2,'PENDING',NULL,'2026-06-26 16:08:25'),(3,8,1,'PENDING',NULL,'2026-06-26 16:09:49'),(4,9,1,'PENDING',NULL,'2026-06-26 17:10:39'),(5,9,2,'PENDING',NULL,'2026-06-26 17:10:39'),(6,9,3,'PENDING',NULL,'2026-06-26 17:10:39'),(7,10,1,'PENDING',NULL,'2026-06-26 17:10:39'),(8,10,2,'PENDING',NULL,'2026-06-26 17:10:39'),(9,10,3,'PENDING',NULL,'2026-06-26 17:10:39'),(10,10,4,'PENDING',NULL,'2026-06-26 17:10:39'),(11,10,5,'PENDING',NULL,'2026-06-26 17:10:39'),(12,11,1,'PENDING',NULL,'2026-06-26 17:37:43'),(13,12,1,'PENDING',NULL,'2026-06-26 17:38:31'),(14,13,1,'PENDING',NULL,'2026-06-26 17:39:38');
/*!40000 ALTER TABLE `task_enterprise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_extension`
--

DROP TABLE IF EXISTS `task_extension`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_extension` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL,
  `applicant_id` bigint NOT NULL COMMENT '申请人ID',
  `reason` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '延期理由',
  `extend_days` int NOT NULL COMMENT '申请延期天数',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待审批' COMMENT '待审批/已批准/已驳回',
  `approved_by` bigint DEFAULT NULL COMMENT '审批人ID',
  `approved_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `original_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `requested_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `review_comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='延期申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_extension`
--

LOCK TABLES `task_extension` WRITE;
/*!40000 ALTER TABLE `task_extension` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_extension` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_inspector`
--

DROP TABLE IF EXISTS `task_inspector`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_inspector` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '检查人员ID',
  `claimed` tinyint DEFAULT '0' COMMENT '是否已认领',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ASSIGNED',
  `claim_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_inspector` (`task_id`,`user_id`),
  KEY `idx_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务-检查人员关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_inspector`
--

LOCK TABLES `task_inspector` WRITE;
/*!40000 ALTER TABLE `task_inspector` DISABLE KEYS */;
INSERT INTO `task_inspector` VALUES (5,1,2,0,'ASSIGNED',NULL,NULL,'2026-06-26 17:08:29'),(6,2,3,0,'CLAIMED',NULL,NULL,'2026-06-26 17:08:29'),(7,3,2,0,'COMPLETED',NULL,NULL,'2026-06-26 17:08:29'),(8,3,3,0,'COMPLETED',NULL,NULL,'2026-06-26 17:08:29'),(9,9,2,0,'ASSIGNED',NULL,NULL,'2026-06-26 17:10:39'),(10,9,3,0,'ASSIGNED',NULL,NULL,'2026-06-26 17:10:39'),(11,11,14,0,'ASSIGNED',NULL,NULL,'2026-06-26 17:37:43'),(12,12,14,0,'ASSIGNED',NULL,NULL,'2026-06-26 17:38:31'),(13,13,14,0,'ASSIGNED',NULL,NULL,'2026-06-26 17:39:38');
/*!40000 ALTER TABLE `task_inspector` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27 15:02:52
