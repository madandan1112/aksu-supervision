DROP TABLE IF EXISTS department;

CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(20) NOT NULL,
    dept_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    dept_type VARCHAR(20) NOT NULL,
    area_name VARCHAR(50),
    leader_name VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(200),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dept_code (dept_code),
    KEY idx_parent (parent_id),
    KEY idx_type (dept_type),
    KEY idx_area (area_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, leader_name, phone, address, sort_order) VALUES
('AKSU_REGION', '阿克苏地区市场监督管理局', NULL, 'REGION', '阿克苏地区', '徐云峰', '0997-2132829', '阿克苏市文化路40号', 1);

SET @region_id = LAST_INSERT_ID();

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, leader_name, phone, sort_order) VALUES
('REGION_ZHB', '综合协调部（办公室/财务与审计科）', @region_id, 'OFFICE', '阿克苏地区', '刁春林', '0997-2132829', 10),
('REGION_YSHJ', '营商环境服务部（行政审批科/登记注册科）', @region_id, 'OFFICE', '阿克苏地区', '刁春林', '0997-2132829', 20),
('REGION_CXJG', '创新监管部（信用监管科/科技信息化科）', @region_id, 'OFFICE', '阿克苏地区', '刁春林', '0997-2132829', 30),
('REGION_SPAQ', '食品安全工作部（食品安全协调科/食品生产流通安全监管科/餐饮食品安全监管科）', @region_id, 'OFFICE', '阿克苏地区', '汪连江', '0997-2132829', 40),
('REGION_FZGH', '法治规划部（法规科）', @region_id, 'OFFICE', '阿克苏地区', '王志强', '0997-2132829', 50),
('REGION_ZFZD', '综合执法部（执法稽查科/反垄断和反不正当竞争科/价格监督检查科/网络交易和广告监督管理科）', @region_id, 'OFFICE', '阿克苏地区', '王志强', '0997-2132829', 60),
('REGION_ZLAQ', '质量安全工作部（质量发展监管科/计量科/标准化科/纤维质量监管科）', @region_id, 'OFFICE', '阿克苏地区', '王志强', '0997-2132829', 70),
('REGION_TZSB', '特种设备安全工作部（特种设备安全监察科）', @region_id, 'OFFICE', '阿克苏地区', '王志强', '0997-2132829', 80),
('REGION_ZHISH', '知识产权部（知识产权科）', @region_id, 'OFFICE', '阿克苏地区', '王志强', '0997-2132829', 90),
('REGION_DJRS', '党建人事工作部（组织人事科）', @region_id, 'OFFICE', '阿克苏地区', '陈易', '0997-2132829', 100),
('REGION_YXAQ', '药械安全工作部（风险监测抽检科/药品和医疗器械监管科/化妆品监管科）', @region_id, 'OFFICE', '阿克苏地区', '陈易', '0997-2132829', 110),
('REGION_FZFG', '阿克苏纺织工业城（开发区）市场监督管理分局', @region_id, 'BRANCH', '阿克苏地区', '王志强', '0997-2132829', 120);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, leader_name, phone, address, sort_order) VALUES
('AKSU_CITY', '阿克苏市市场监督管理局', NULL, 'CITY', '阿克苏市', '王枫', '0997-2124971', '阿克苏市红旗坡片区北京路67号', 200);

SET @city_id = LAST_INSERT_ID();

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('CITY_BGS', '办公室', @city_id, 'OFFICE', '阿克苏市', 210),
('CITY_FGK', '法规科', @city_id, 'OFFICE', '阿克苏市', 220),
('CITY_ZCS', '注册登记科', @city_id, 'OFFICE', '阿克苏市', 230),
('CITY_XZSP', '行政审批科', @city_id, 'OFFICE', '阿克苏市', 240),
('CITY_XYJG', '信用监管科', @city_id, 'OFFICE', '阿克苏市', 250),
('CITY_SPAQ', '食品安全监管科', @city_id, 'OFFICE', '阿克苏市', 260),
('CITY_CYPZ', '药品化妆品监管科', @city_id, 'OFFICE', '阿克苏市', 270),
('CITY_YLQX', '医疗器械监管科', @city_id, 'OFFICE', '阿克苏市', 280),
('CITY_ZLJG', '质量监管科', @city_id, 'OFFICE', '阿克苏市', 290),
('CITY_TZSB', '特种设备安全监察科', @city_id, 'OFFICE', '阿克苏市', 300),
('CITY_JLK', '计量科', @city_id, 'OFFICE', '阿克苏市', 310),
('CITY_BZK', '标准化科', @city_id, 'OFFICE', '阿克苏市', 320),
('CITY_JGJC', '价格监督检查科', @city_id, 'OFFICE', '阿克苏市', 330),
('CITY_FLD', '反垄断和反不正当竞争科', @city_id, 'OFFICE', '阿克苏市', 340),
('CITY_WLJG', '网络交易和广告监管科', @city_id, 'OFFICE', '阿克苏市', 350),
('CITY_ZHISH', '知识产权保护科', @city_id, 'OFFICE', '阿克苏市', 360),
('CITY_XFQY', '消费者权益保护科', @city_id, 'OFFICE', '阿克苏市', 370),
('CITY_ZFZD', '执法稽查科', @city_id, 'OFFICE', '阿克苏市', 380),
('CITY_SJXX', '科技信息化科', @city_id, 'OFFICE', '阿克苏市', 390);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('KUCHE_COUNTY', '库车市市场监督管理局', NULL, 'COUNTY', '库车市', 400);
SET @kuche_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('KUCHE_BGS', '办公室', @kuche_id, 'OFFICE', '库车市', 410),
('KUCHE_ZCS', '注册登记科', @kuche_id, 'OFFICE', '库车市', 420),
('KUCHE_XYJG', '信用监管科', @kuche_id, 'OFFICE', '库车市', 430),
('KUCHE_SPAQ', '食品安全监管科', @kuche_id, 'OFFICE', '库车市', 440),
('KUCHE_CYPZ', '药品化妆品监管科', @kuche_id, 'OFFICE', '库车市', 450),
('KUCHE_ZLJG', '质量监管科', @kuche_id, 'OFFICE', '库车市', 460),
('KUCHE_TZSB', '特种设备安全监察科', @kuche_id, 'OFFICE', '库车市', 470),
('KUCHE_JGJC', '价格监督检查科', @kuche_id, 'OFFICE', '库车市', 480),
('KUCHE_ZFZD', '执法稽查科', @kuche_id, 'OFFICE', '库车市', 490),
('KUCHE_XFQY', '消费者权益保护科', @kuche_id, 'OFFICE', '库车市', 500);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('WENSU_COUNTY', '温宿县市场监督管理局', NULL, 'COUNTY', '温宿县', 600);
SET @wensu_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('WENSU_BGS', '办公室', @wensu_id, 'OFFICE', '温宿县', 610),
('WENSU_ZCS', '注册登记科', @wensu_id, 'OFFICE', '温宿县', 620),
('WENSU_XYJG', '信用监管科', @wensu_id, 'OFFICE', '温宿县', 630),
('WENSU_SPAQ', '食品安全监管科', @wensu_id, 'OFFICE', '温宿县', 640),
('WENSU_CYPZ', '药品化妆品监管科', @wensu_id, 'OFFICE', '温宿县', 650),
('WENSU_ZLJG', '质量监管科', @wensu_id, 'OFFICE', '温宿县', 660),
('WENSU_TZSB', '特种设备安全监察科', @wensu_id, 'OFFICE', '温宿县', 670),
('WENSU_JGJC', '价格监督检查科', @wensu_id, 'OFFICE', '温宿县', 680),
('WENSU_ZFZD', '执法稽查科', @wensu_id, 'OFFICE', '温宿县', 690),
('WENSU_XFQY', '消费者权益保护科', @wensu_id, 'OFFICE', '温宿县', 700);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('SHAYA_COUNTY', '沙雅县市场监督管理局', NULL, 'COUNTY', '沙雅县', 800);
SET @shaya_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('SHAYA_BGS', '办公室', @shaya_id, 'OFFICE', '沙雅县', 810),
('SHAYA_ZCS', '注册登记科', @shaya_id, 'OFFICE', '沙雅县', 820),
('SHAYA_XYJG', '信用监管科', @shaya_id, 'OFFICE', '沙雅县', 830),
('SHAYA_SPAQ', '食品安全监管科', @shaya_id, 'OFFICE', '沙雅县', 840),
('SHAYA_CYPZ', '药品化妆品监管科', @shaya_id, 'OFFICE', '沙雅县', 850),
('SHAYA_ZLJG', '质量监管科', @shaya_id, 'OFFICE', '沙雅县', 860),
('SHAYA_TZSB', '特种设备安全监察科', @shaya_id, 'OFFICE', '沙雅县', 870),
('SHAYA_JGJC', '价格监督检查科', @shaya_id, 'OFFICE', '沙雅县', 880),
('SHAYA_ZFZD', '执法稽查科', @shaya_id, 'OFFICE', '沙雅县', 890),
('SHAYA_XFQY', '消费者权益保护科', @shaya_id, 'OFFICE', '沙雅县', 900);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('BAICHENG_COUNTY', '拜城县市场监督管理局', NULL, 'COUNTY', '拜城县', 1000);
SET @baicheng_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('BAICHENG_BGS', '办公室', @baicheng_id, 'OFFICE', '拜城县', 1010),
('BAICHENG_ZCS', '注册登记科', @baicheng_id, 'OFFICE', '拜城县', 1020),
('BAICHENG_XYJG', '信用监管科', @baicheng_id, 'OFFICE', '拜城县', 1030),
('BAICHENG_SPAQ', '食品安全监管科', @baicheng_id, 'OFFICE', '拜城县', 1040),
('BAICHENG_CYPZ', '药品化妆品监管科', @baicheng_id, 'OFFICE', '拜城县', 1050),
('BAICHENG_ZLJG', '质量监管科', @baicheng_id, 'OFFICE', '拜城县', 1060),
('BAICHENG_TZSB', '特种设备安全监察科', @baicheng_id, 'OFFICE', '拜城县', 1070),
('BAICHENG_JGJC', '价格监督检查科', @baicheng_id, 'OFFICE', '拜城县', 1080),
('BAICHENG_ZFZD', '执法稽查科', @baicheng_id, 'OFFICE', '拜城县', 1090),
('BAICHENG_XFQY', '消费者权益保护科', @baicheng_id, 'OFFICE', '拜城县', 1100);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('XINHE_COUNTY', '新和县市场监督管理局', NULL, 'COUNTY', '新和县', 1200);
SET @xinhe_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('XINHE_BGS', '办公室', @xinhe_id, 'OFFICE', '新和县', 1210),
('XINHE_ZCS', '注册登记科', @xinhe_id, 'OFFICE', '新和县', 1220),
('XINHE_XYJG', '信用监管科', @xinhe_id, 'OFFICE', '新和县', 1230),
('XINHE_SPAQ', '食品安全监管科', @xinhe_id, 'OFFICE', '新和县', 1240),
('XINHE_CYPZ', '药品化妆品监管科', @xinhe_id, 'OFFICE', '新和县', 1250),
('XINHE_ZLJG', '质量监管科', @xinhe_id, 'OFFICE', '新和县', 1260),
('XINHE_TZSB', '特种设备安全监察科', @xinhe_id, 'OFFICE', '新和县', 1270),
('XINHE_JGJC', '价格监督检查科', @xinhe_id, 'OFFICE', '新和县', 1280),
('XINHE_ZFZD', '执法稽查科', @xinhe_id, 'OFFICE', '新和县', 1290),
('XINHE_XFQY', '消费者权益保护科', @xinhe_id, 'OFFICE', '新和县', 1300);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('WUSHI_COUNTY', '乌什县市场监督管理局', NULL, 'COUNTY', '乌什县', 1400);
SET @wushi_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('WUSHI_BGS', '办公室', @wushi_id, 'OFFICE', '乌什县', 1410),
('WUSHI_ZCS', '注册登记科', @wushi_id, 'OFFICE', '乌什县', 1420),
('WUSHI_XYJG', '信用监管科', @wushi_id, 'OFFICE', '乌什县', 1430),
('WUSHI_SPAQ', '食品安全监管科', @wushi_id, 'OFFICE', '乌什县', 1440),
('WUSHI_CYPZ', '药品化妆品监管科', @wushi_id, 'OFFICE', '乌什县', 1450),
('WUSHI_ZLJG', '质量监管科', @wushi_id, 'OFFICE', '乌什县', 1460),
('WUSHI_TZSB', '特种设备安全监察科', @wushi_id, 'OFFICE', '乌什县', 1470),
('WUSHI_JGJC', '价格监督检查科', @wushi_id, 'OFFICE', '乌什县', 1480),
('WUSHI_ZFZD', '执法稽查科', @wushi_id, 'OFFICE', '乌什县', 1490),
('WUSHI_XFQY', '消费者权益保护科', @wushi_id, 'OFFICE', '乌什县', 1500);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('AWATI_COUNTY', '阿瓦提县市场监督管理局', NULL, 'COUNTY', '阿瓦提县', 1600);
SET @awati_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('AWATI_BGS', '办公室', @awati_id, 'OFFICE', '阿瓦提县', 1610),
('AWATI_ZCS', '注册登记科', @awati_id, 'OFFICE', '阿瓦提县', 1620),
('AWATI_XYJG', '信用监管科', @awati_id, 'OFFICE', '阿瓦提县', 1630),
('AWATI_SPAQ', '食品安全监管科', @awati_id, 'OFFICE', '阿瓦提县', 1640),
('AWATI_CYPZ', '药品化妆品监管科', @awati_id, 'OFFICE', '阿瓦提县', 1650),
('AWATI_ZLJG', '质量监管科', @awati_id, 'OFFICE', '阿瓦提县', 1660),
('AWATI_TZSB', '特种设备安全监察科', @awati_id, 'OFFICE', '阿瓦提县', 1670),
('AWATI_JGJC', '价格监督检查科', @awati_id, 'OFFICE', '阿瓦提县', 1680),
('AWATI_ZFZD', '执法稽查科', @awati_id, 'OFFICE', '阿瓦提县', 1690),
('AWATI_XFQY', '消费者权益保护科', @awati_id, 'OFFICE', '阿瓦提县', 1700);

INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('KEPING_COUNTY', '柯坪县市场监督管理局', NULL, 'COUNTY', '柯坪县', 1800);
SET @keping_id = LAST_INSERT_ID();
INSERT INTO department (dept_code, dept_name, parent_id, dept_type, area_name, sort_order) VALUES
('KEPING_BGS', '办公室', @keping_id, 'OFFICE', '柯坪县', 1810),
('KEPING_ZCS', '注册登记科', @keping_id, 'OFFICE', '柯坪县', 1820),
('KEPING_XYJG', '信用监管科', @keping_id, 'OFFICE', '柯坪县', 1830),
('KEPING_SPAQ', '食品安全监管科', @keping_id, 'OFFICE', '柯坪县', 1840),
('KEPING_CYPZ', '药品化妆品监管科', @keping_id, 'OFFICE', '柯坪县', 1850),
('KEPING_ZLJG', '质量监管科', @keping_id, 'OFFICE', '柯坪县', 1860),
('KEPING_TZSB', '特种设备安全监察科', @keping_id, 'OFFICE', '柯坪县', 1870),
('KEPING_JGJC', '价格监督检查科', @keping_id, 'OFFICE', '柯坪县', 1880),
('KEPING_ZFZD', '执法稽查科', @keping_id, 'OFFICE', '柯坪县', 1890),
('KEPING_XFQY', '消费者权益保护科', @keping_id, 'OFFICE', '柯坪县', 1900);
