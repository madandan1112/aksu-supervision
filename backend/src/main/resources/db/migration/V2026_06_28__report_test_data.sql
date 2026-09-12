-- 补充合规报告测试数据（2026-06-28）

-- 如果 enterprise_name 列不存在，先添加
ALTER TABLE compliance_report ADD COLUMN IF NOT EXISTS enterprise_name VARCHAR(200);
ALTER TABLE compliance_report ADD COLUMN IF NOT EXISTS report_no VARCHAR(100);
ALTER TABLE compliance_report ADD COLUMN IF NOT EXISTS industry VARCHAR(100);

-- 更新已有报告的企业名称（根据 enterprise_id 关联）
UPDATE compliance_report SET enterprise_name = 
  CASE 
    WHEN enterprise_id = 1 THEN '阿克苏市天山食品有限责任公司'
    WHEN enterprise_id = 2 THEN '阿克苏华联商贸有限公司'
    WHEN enterprise_id = 3 THEN '库车县金桥建材有限公司'
    WHEN enterprise_id = 4 THEN '温宿县绿洲农业科技有限公司'
    WHEN enterprise_id = 5 THEN '阿克苏市鑫达特种设备安装有限公司'
    ELSE '未知企业'
  END;

-- 补充更丰富的报告测试数据（report_no、industry、file_url等）
UPDATE compliance_report SET 
  report_no = CONCAT('RPT-', YEAR(CURRENT_DATE), '-', LPAD(id, 5, '0')),
  industry = CASE 
    WHEN enterprise_id = 1 THEN '食品制造'
    WHEN enterprise_id = 2 THEN '商贸流通'
    WHEN enterprise_id = 3 THEN '建材加工'
    WHEN enterprise_id = 4 THEN '农业科技'
    WHEN enterprise_id = 5 THEN '特种设备'
    ELSE '其他'
  END,
  file_url = CASE 
    WHEN id = 1 THEN '/uploads/report/食品检测报告_天山食品_2026.pdf'
    WHEN id = 2 THEN '/uploads/report/检验报告_华联商贸_2026.pdf'
    WHEN id = 3 THEN '/uploads/report/合格证明_金桥建材_2026.pdf'
    WHEN id = 4 THEN '/uploads/report/检测报告_绿洲农业_2026.pdf'
    WHEN id = 5 THEN '/uploads/report/特种设备检验报告_鑫达_2026.pdf'
    ELSE NULL
  END;

-- 如果记录不足5条，插入新数据
INSERT INTO compliance_report (enterprise_id, title, report_type, file_url, file_name, status, uploaded_by, created_at, updated_at, enterprise_name, report_no, industry)
SELECT 
  1, '天山食品年度检验报告', '检验报告', '/uploads/report/annual_天山食品_2026.pdf', 'annual_天山食品_2026.pdf', 'PENDING', 'admin', NOW(), NOW(), '阿克苏市天山食品有限责任公司', 'RPT-2026-00001', '食品制造'
WHERE NOT EXISTS (SELECT 1 FROM compliance_report WHERE enterprise_id = 1 AND title = '天山食品年度检验报告');

INSERT INTO compliance_report (enterprise_id, title, report_type, file_url, file_name, status, uploaded_by, created_at, updated_at, enterprise_name, report_no, industry)
SELECT 
  2, '华联商贸商品质量检测报告', '检测报告', '/uploads/report/quality_华联商贸_2026.pdf', 'quality_华联商贸_2026.pdf', 'APPROVED', 'admin', NOW(), NOW(), '阿克苏华联商贸有限公司', 'RPT-2026-00002', '商贸流通'
WHERE NOT EXISTS (SELECT 1 FROM compliance_report WHERE enterprise_id = 2 AND title = '华联商贸商品质量检测报告');

INSERT INTO compliance_report (enterprise_id, title, report_type, file_url, file_name, status, uploaded_by, created_at, updated_at, enterprise_name, report_no, industry)
SELECT 
  3, '金桥建材出厂合格证明', '合格证明', '/uploads/report/cert_金桥建材_2026.pdf', 'cert_金桥建材_2026.pdf', 'REJECTED', 'admin', NOW(), NOW(), '库车县金桥建材有限公司', 'RPT-2026-00003', '建材加工'
WHERE NOT EXISTS (SELECT 1 FROM compliance_report WHERE enterprise_id = 3 AND title = '金桥建材出厂合格证明');

INSERT INTO compliance_report (enterprise_id, title, report_type, file_url, file_name, status, uploaded_by, created_at, updated_at, enterprise_name, report_no, industry)
SELECT 
  4, '绿洲农业科技产品检验报告', '检验报告', '/uploads/report/inspect_绿洲农业_2026.pdf', 'inspect_绿洲农业_2026.pdf', 'PENDING', 'admin', NOW(), NOW(), '温宿县绿洲农业科技有限公司', 'RPT-2026-00004', '农业科技'
WHERE NOT EXISTS (SELECT 1 FROM compliance_report WHERE enterprise_id = 4 AND title = '绿洲农业科技产品检验报告');

INSERT INTO compliance_report (enterprise_id, title, report_type, file_url, file_name, status, uploaded_by, created_at, updated_at, enterprise_name, report_no, industry)
SELECT 
  5, '鑫达特种设备安装验收报告', '检测报告', '/uploads/report/accept_鑫达_2026.pdf', 'accept_鑫达_2026.pdf', 'PENDING', 'admin', NOW(), NOW(), '阿克苏市鑫达特种设备安装有限公司', 'RPT-2026-00005', '特种设备'
WHERE NOT EXISTS (SELECT 1 FROM compliance_report WHERE enterprise_id = 5 AND title = '鑫达特种设备安装验收报告');
