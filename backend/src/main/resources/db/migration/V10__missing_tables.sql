-- =====================================================================
-- V10: 补齐缺失的建表迁移（企业注册 / 企业行业分类）
-- 说明：这两张表此前由后端开发过程中手工创建，未纳入 migration 目录，
--       本脚本从生产库结构导出，用于全新环境复现（CREATE TABLE IF NOT EXISTS 幂等）。
-- =====================================================================

CREATE TABLE IF NOT EXISTS `enterprise_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `step` int DEFAULT '1',
  `credit_code` varchar(50) DEFAULT NULL,
  `enterprise_name` varchar(200) DEFAULT NULL,
  `legal_person` varchar(200) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `industry_type_code` varchar(50) DEFAULT NULL,
  `area` varchar(100) DEFAULT NULL,
  `license_url` varchar(500) DEFAULT NULL,
  `qualification_urls` json DEFAULT NULL,
  `status` varchar(20) DEFAULT 'DRAFT',
  `review_comment` varchar(500) DEFAULT NULL,
  `reviewed_by` bigint DEFAULT NULL,
  `reviewed_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `storefront_photo` varchar(500) DEFAULT NULL COMMENT '门头照片URL',
  `interior_photo` varchar(500) DEFAULT NULL COMMENT '店内照片URL',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `enterprise_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type_code` varchar(50) NOT NULL COMMENT '类型编码',
  `type_name` varchar(100) NOT NULL COMMENT '类型名称',
  `category` varchar(50) DEFAULT NULL COMMENT '大类：FOOD食品/DRUG药品/MEDICAL医疗器械/SPECIAL_EQUIP特种设备/HOTEL酒店/FACTORY工厂/OTHER其他',
  `required_license` text COMMENT '所需证照JSON数组',
  `description` varchar(500) DEFAULT NULL COMMENT '说明',
  `status` tinyint DEFAULT '1',
  `sort_order` int DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业行业分类表';
