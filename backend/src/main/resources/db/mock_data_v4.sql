-- ========================================
-- 阿克苏监管平台 - 模拟数据脚本 V4
-- 匹配实际数据库实体结构
-- ========================================

-- 1. 确保检查人员用户存在
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, user_type, status, created_at, updated_at) VALUES
(2, 'inspector_zhang', '$2a$10$pCnP67YHuIiQozl2tXJSEeUUPoC4P0N4G9a9I1QOq1SvfadRLvxya', '张建国', '13999001234', 'INSPECTOR', 1, NOW(), NOW()),
(3, 'inspector_li', '$2a$10$pCnP67YHuIiQozl2tXJSEeUUPoC4P0N4G9a9I1QOq1SvfadRLvxya', '李明远', '13999005678', 'INSPECTOR', 1, NOW(), NOW()),
(4, 'inspector_wang', '$2a$10$pCnP67YHuIiQozl2tXJSEeUUPoC4P0N4G9a9I1QOq1SvfadRLvxya', '王秀芳', '13999009012', 'INSPECTOR', 1, NOW(), NOW());

-- 2. 确保企业数据充足
INSERT IGNORE INTO enterprise (id, credit_code, enterprise_name, legal_person, phone, email, industry, area, address, business_scope, license_url, status, user_id, created_at, updated_at) VALUES
(1, '91652901MA7XXXXX1', '阿克苏市天山食品有限责任公司', '陈志明', '13999001001', 'tianshan@aksu.com', '食品生产', '阿克苏市', '阿克苏市解放中路128号', '食品生产加工与销售', '/uploads/license/tianshan.jpg', 1, 5, NOW(), NOW()),
(2, '91652902MA7XXXXX2', '库车市兴隆商贸有限公司', '马文龙', '13999001002', 'xinglong@kuqa.com', '商贸流通', '库车市', '库车市文化路56号', '日用百货批发零售', '/uploads/license/xinglong.jpg', 1, 5, NOW(), NOW()),
(3, '91652903MA7XXXXX3', '温宿县恒力建材有限公司', '刘强', '13999001003', 'hengli@wsx.gov.cn', '建筑材料', '温宿县', '温宿县工业园区A区12号', '建材生产销售', '/uploads/license/hengli.jpg', 1, 5, NOW(), NOW()),
(4, '91652904MA7XXXXX4', '沙雅县绿洲农产品有限公司', '买买提·艾力', '13999001004', 'lvzhou@shaya.com', '农产品', '沙雅县', '沙雅县农业园区3号', '农产品种植加工', '/uploads/license/lvzhou.jpg', 1, 5, NOW(), NOW()),
(5, '91652905MA7XXXXX5', '拜城县天源化工有限公司', '赵国栋', '13999001005', 'tianyuan@baicheng.com', '化工', '拜城县', '拜城县化工园区B区8号', '化工产品生产', '/uploads/license/tianyuan.jpg', 1, 5, NOW(), NOW()),
(6, '91652901MA7XXXXX6', '阿克苏市宏远建材有限公司', '孙立伟', '13999001006', 'hongyuan@aksu.com', '建筑材料', '阿克苏市', '阿克苏市建材路99号', '建材生产加工', '/uploads/license/hongyuan.jpg', 1, 5, NOW(), NOW()),
(7, '91652907MA7XXXXX7', '新和县顺达物流有限公司', '阿卜杜拉·穆萨', '13999001007', 'shunda@xinhe.com', '物流运输', '新和县', '新和县物流园区1号', '道路货物运输', '/uploads/license/shunda.jpg', 1, 5, NOW(), NOW());

-- 3. 检查任务
INSERT IGNORE INTO inspection_task (id, task_no, title, description, task_type, status, start_date, end_date, actual_start_time, actual_end_time, created_by, check_items, created_at, updated_at) VALUES
(1, 'TASK20260620001', '阿克苏市食品生产企业专项检查', '对阿克苏市辖区食品生产企业进行专项安全检查', 'SPECIAL', 'COMPLETED', '2026-06-20 08:00:00', '2026-06-20 18:00:00', '2026-06-20 08:30:00', '2026-06-20 16:00:00', 'admin', '["食品生产许可证有效性","生产车间卫生状况","原材料采购台账","从业人员健康证明"]', NOW(), NOW()),
(2, 'TASK20260618001', '库车商贸流通企业安全检查', '对库车市商贸流通企业进行消防安全检查', 'ROUTINE', 'COMPLETED', '2026-06-18 08:00:00', '2026-06-18 18:00:00', '2026-06-18 09:00:00', '2026-06-18 15:00:00', 'admin', '["营业执照有效性","消防安全设施","经营场所卫生","商品保质期管理"]', NOW(), NOW()),
(3, 'TASK20260615001', '温宿县特种设备使用企业检查', '对温宿县特种设备使用企业进行安全检查', 'SPECIAL', 'COMPLETED', '2026-06-15 08:00:00', '2026-06-15 18:00:00', '2026-06-15 08:00:00', '2026-06-15 17:00:00', 'admin', '["特种设备使用登记证","设备定期检验报告","操作人员资质","安全防护装置"]', NOW(), NOW()),
(4, 'TASK20260625001', '沙雅县农产品加工企业检查', '对沙雅县农产品加工企业进行安全检查', 'ROUTINE', 'IN_PROGRESS', '2026-06-25 08:00:00', '2026-06-25 18:00:00', '2026-06-25 09:00:00', NULL, 'admin', '["生产许可证","卫生标准执行","原料来源追溯","产品检验报告"]', NOW(), NOW()),
(5, 'TASK20260610001', '拜城县化工企业安全生产检查', '对拜城县化工企业进行安全生产专项检查', 'SPECIAL', 'COMPLETED', '2026-06-10 08:00:00', '2026-06-10 18:00:00', '2026-06-10 08:00:00', '2026-06-10 16:30:00', 'admin', '["安全生产许可证","危化品储存管理","应急预案备案","安全标识标牌"]', NOW(), NOW()),
(6, 'TASK20260608001', '阿克苏市建筑材料企业检查', '对阿克苏市建筑材料企业进行常规检查', 'ROUTINE', 'COMPLETED', '2026-06-08 08:00:00', '2026-06-08 18:00:00', '2026-06-08 09:00:00', '2026-06-08 14:00:00', 'admin', '["建材质量检测报告","生产许可证","环保设施运行","堆场管理"]', NOW(), NOW()),
(7, 'TASK20260612001', '新和县物流运输企业检查', '对新和县物流运输企业进行安全检查', 'ROUTINE', 'COMPLETED', '2026-06-12 08:00:00', '2026-06-12 18:00:00', '2026-06-12 09:00:00', '2026-06-12 16:00:00', 'admin', '["运输车辆资质","驾驶员从业资格","GPS监控平台","货物装载规范"]', NOW(), NOW());

-- 4. 任务-企业关联
INSERT IGNORE INTO task_enterprise (id, task_id, enterprise_id, status, inspection_result, created_at) VALUES
(1, 1, 1, 'COMPLETED', '不合格-4项问题', NOW()),
(2, 2, 2, 'COMPLETED', '基本合格-3项问题', NOW()),
(3, 3, 3, 'COMPLETED', '不合格-4项严重问题', NOW()),
(4, 4, 4, 'PENDING', NULL, NOW()),
(5, 5, 5, 'COMPLETED', '不合格-5项严重问题', NOW()),
(6, 6, 6, 'COMPLETED', '合格-2项轻微问题', NOW()),
(7, 7, 7, 'COMPLETED', '基本合格-3项问题', NOW());

-- 5. 任务-检查人员关联
INSERT IGNORE INTO task_inspector (id, task_id, user_id, status, claim_time, complete_time, created_at) VALUES
(1, 1, 2, 'COMPLETED', '2026-06-20 08:30:00', '2026-06-20 16:00:00', NOW()),
(2, 2, 3, 'COMPLETED', '2026-06-18 09:00:00', '2026-06-18 15:00:00', NOW()),
(3, 3, 2, 'COMPLETED', '2026-06-15 08:00:00', '2026-06-15 17:00:00', NOW()),
(4, 4, 4, 'IN_PROGRESS', '2026-06-25 09:00:00', NULL, NOW()),
(5, 5, 3, 'COMPLETED', '2026-06-10 08:00:00', '2026-06-10 16:30:00', NOW()),
(6, 6, 2, 'COMPLETED', '2026-06-08 09:00:00', '2026-06-08 14:00:00', NOW()),
(7, 7, 4, 'COMPLETED', '2026-06-12 09:00:00', '2026-06-12 16:00:00', NOW());

-- 6. 现场检查记录（含图片/视频/文字/检查类型/附件）
INSERT IGNORE INTO inspection_record (id, task_id, enterprise_id, inspector_id, status, form_data, check_type, attachment_urls, issues, evidence_images, evidence_videos, summary, inspection_time, inspector_name, enterprise_name, created_at, updated_at) VALUES
(1, 1, 1, 2, 'SUBMITTED',
'{"checkItems":["食品生产许可证有效性","生产车间卫生状况","原材料采购台账","从业人员健康证明"],"result":"不合格","score":62}',
'["食品生产科","食品生产许可证检查","生产车间卫生检查"]',
'["/uploads/inspection/2026/06/食品许可证_20260620.pdf","/uploads/inspection/2026/06/卫生检查表_20260620.pdf"]',
'1. 生产车间地面有积水，排水不畅；\n2. 冷库温度记录不完整，6月15日-17日缺失；\n3. 从业人员健康证3张已过期；\n4. 食品添加剂使用台账记录不规范',
'["/uploads/inspection/2026/06/img_车间地面_20260620_001.jpg","/uploads/inspection/2026/06/img_冷库温度记录_20260620_002.jpg","/uploads/inspection/2026/06/img_过期健康证_20260620_003.jpg","/uploads/inspection/2026/06/img_添加剂台账_20260620_004.jpg"]',
'["/uploads/inspection/2026/06/vid_车间巡检_20260620.mp4","/uploads/inspection/2026/06/vid_冷库检查_20260620.mp4"]',
'对阿克苏市天山食品有限责任公司进行食品安全专项检查，发现4项问题，整体评分62分（不合格）。主要问题集中在车间卫生管理和人员资质方面，需限期整改。',
'2026-06-20 10:30:00', '张建国', '阿克苏市天山食品有限责任公司', NOW(), NOW()),

(2, 2, 2, 3, 'SUBMITTED',
'{"checkItems":["营业执照有效性","消防安全设施","经营场所卫生","商品保质期管理"],"result":"基本合格","score":78}',
'["商贸流通科","商贸企业安全检查","消防设施检查"]',
'["/uploads/inspection/2026/06/营业执照_20260618.pdf"]',
'1. 消防通道有杂物堆放，影响通行；\n2. 部分商品未按规定离地存放；\n3. 仓库区域灭火器2个已过期',
'["/uploads/inspection/2026/06/img_消防通道_20260618_001.jpg","/uploads/inspection/2026/06/img_商品存放_20260618_002.jpg","/uploads/inspection/2026/06/img_灭火器过期_20260618_003.jpg"]',
'["/uploads/inspection/2026/06/vid_卖场巡检_20260618.mp4"]',
'对库车市兴隆商贸有限公司进行商贸流通安全检查，发现3项问题，整体评分78分（基本合格）。消防安全隐患需重点关注。',
'2026-06-18 14:00:00', '李明远', '库车市兴隆商贸有限公司', NOW(), NOW()),

(3, 3, 3, 2, 'SUBMITTED',
'{"checkItems":["特种设备使用登记证","设备定期检验报告","操作人员资质","安全防护装置"],"result":"不合格","score":55}',
'["特种设备科","特种设备使用登记检查","安全防护装置检查"]',
'["/uploads/inspection/2026/06/设备清单_20260615.pdf","/uploads/inspection/2026/06/检验报告_20260615.pdf"]',
'1. 2台叉车使用登记证已过期，未及时更新；\n2. 压力容器安全阀校验报告缺失；\n3. 特种设备操作人员1人无有效资质证书；\n4. 起重机械限位装置失灵',
'["/uploads/inspection/2026/06/img_叉车登记证_20260615_001.jpg","/uploads/inspection/2026/06/img_压力容器_20260615_002.jpg","/uploads/inspection/2026/06/img_限位装置_20260615_003.jpg","/uploads/inspection/2026/06/img_操作证缺失_20260615_004.jpg","/uploads/inspection/2026/06/img_车间全景_20260615_005.jpg"]',
'["/uploads/inspection/2026/06/vid_叉车检查_20260615.mp4","/uploads/inspection/2026/06/vid_压力容器检查_20260615.mp4","/uploads/inspection/2026/06/vid_起重机械_20260615.mp4"]',
'对温宿县恒力建材有限公司进行特种设备安全检查，发现4项严重问题，整体评分55分（不合格）。存在较大安全隐患，需立即整改。',
'2026-06-15 09:00:00', '张建国', '温宿县恒力建材有限公司', NOW(), NOW()),

(4, 5, 5, 3, 'SUBMITTED',
'{"checkItems":["安全生产许可证","危化品储存管理","应急预案备案","安全标识标牌"],"result":"不合格","score":48}',
'["化工科","危化品储存管理检查","应急预案备案检查"]',
'["/uploads/inspection/2026/06/安全生产许可证_20260610.pdf","/uploads/inspection/2026/06/危化品清单_20260610.pdf"]',
'1. 危化品仓库未设置防泄漏设施；\n2. 部分化学品混放，存在反应风险；\n3. 应急预案未按规定备案；\n4. 仓库区域安全标识缺失严重；\n5. 未配备应急物资柜',
'["/uploads/inspection/2026/06/img_危化品仓库_20260610_001.jpg","/uploads/inspection/2026/06/img_化学品混放_20260610_002.jpg","/uploads/inspection/2026/06/img_标识缺失_20260610_003.jpg","/uploads/inspection/2026/06/img_缺少应急柜_20260610_004.jpg"]',
'["/uploads/inspection/2026/06/vid_仓库全貌_20260610.mp4","/uploads/inspection/2026/06/vid_化学品存放_20260610.mp4"]',
'对拜城县天源化工有限公司进行安全生产专项检查，发现5项严重问题，整体评分48分（不合格）。危化品管理存在重大风险，需立即停业整改。',
'2026-06-10 08:30:00', '李明远', '拜城县天源化工有限公司', NOW(), NOW()),

(5, 6, 6, 2, 'SUBMITTED',
'{"checkItems":["建材质量检测报告","生产许可证","环保设施运行","堆场管理"],"result":"合格","score":88}',
'["建筑材料科","建材质量检测检查","环保设施运行检查"]',
'["/uploads/inspection/2026/06/质量检测报告_20260608.pdf"]',
'1. 堆场部分区域防尘网覆盖不完整；\n2. 环保在线监测设备校验记录过期1周',
'["/uploads/inspection/2026/06/img_堆场防尘_20260608_001.jpg","/uploads/inspection/2026/06/img_监测设备_20260608_002.jpg"]',
'[]',
'对阿克苏市宏远建材有限公司进行建材生产检查，发现2项轻微问题，整体评分88分（合格）。企业总体管理规范，需注意防尘和设备校验。',
'2026-06-08 11:00:00', '张建国', '阿克苏市宏远建材有限公司', NOW(), NOW()),

(6, 7, 7, 4, 'SUBMITTED',
'{"checkItems":["运输车辆资质","驾驶员从业资格","GPS监控平台","货物装载规范"],"result":"基本合格","score":75}',
'["物流运输科","运输车辆资质检查","GPS监控平台检查"]',
'["/uploads/inspection/2026/06/车辆清单_20260612.pdf"]',
'1. 2辆运输车辆年检即将到期（7月到期）；\n2. GPS监控平台1路摄像头故障；\n3. 货物装载记录部分缺失',
'["/uploads/inspection/2026/06/img_车辆检查_20260612_001.jpg","/uploads/inspection/2026/06/img_GPS平台_20260612_002.jpg"]',
'["/uploads/inspection/2026/06/vid_车辆巡检_20260612.mp4"]',
'对新和县顺达物流有限公司进行运输安全检查，发现3项问题，整体评分75分（基本合格）。车辆管理和监控系统需完善。',
'2026-06-12 15:30:00', '王秀芳', '新和县顺达物流有限公司', NOW(), NOW());

-- 7. 整改通知
INSERT IGNORE INTO rectification_notice (id, inspection_record_id, enterprise_id, inspector_id, notice_no, issues, requirements, deadline, status, attachment_url, created_at, updated_at) VALUES
(1, 1, 1, 2, 'ZG20260620001', '1.生产车间地面有积水排水不畅；2.冷库温度记录不完整；3.从业人员健康证3张已过期；4.食品添加剂使用台账记录不规范', '1.修缮车间排水系统；2.完善冷库温度记录机制；3.立即安排过期人员重新体检；4.按规范重新登记添加剂台账。请于2026年7月5日前完成整改并提交整改报告。', '2026-07-05 23:59:59', 'ISSUED', '/uploads/notice/ZG20260620001.pdf', NOW(), NOW()),
(2, 2, 2, 3, 'ZG20260618001', '1.消防通道有杂物堆放影响通行；2.部分商品未按规定离地存放；3.仓库区域灭火器2个已过期', '1.立即清理消防通道并设置通道标识；2.所有商品按规定离地存放；3.更换过期灭火器并完成全库区检查。请于2026年7月3日前完成整改并提交整改报告。', '2026-07-03 23:59:59', 'ISSUED', '/uploads/notice/ZG20260618001.pdf', NOW(), NOW()),
(3, 3, 3, 2, 'ZG20260615001', '1.2台叉车使用登记证已过期；2.压力容器安全阀校验报告缺失；3.特种设备操作人员1人无有效资质证书；4.起重机械限位装置失灵', '1.立即重新办理叉车使用登记证；2.安排安全阀校验；3.无证人员停止操作并安排培训取证；4.更换限位装置。请于2026年6月30日前完成整改并提交整改报告。', '2026-06-30 23:59:59', 'ISSUED', '/uploads/notice/ZG20260615001.pdf', NOW(), NOW()),
(4, 4, 5, 3, 'ZG20260610001', '1.危化品仓库未设置防泄漏设施；2.部分化学品混放存在反应风险；3.应急预案未按规定备案；4.仓库区域安全标识缺失严重；5.未配备应急物资柜', '1.立即停业整改；2.安装防泄漏设施；3.分类存放化学品；4.完成应急预案备案；5.补全安全标识；6.配备应急物资柜。请于2026年6月25日前完成整改并提交整改报告。', '2026-06-25 23:59:59', 'ISSUED', '/uploads/notice/ZG20260610001.pdf', NOW(), NOW()),
(5, 6, 7, 4, 'ZG20260612001', '1.2辆运输车辆年检即将到期；2.GPS监控平台1路摄像头故障；3.货物装载记录部分缺失', '1.及时完成车辆年检；2.修复GPS监控平台摄像头；3.完善货物装载记录。请于2026年7月10日前完成整改并提交整改报告。', '2026-07-10 23:59:59', 'ISSUED', '/uploads/notice/ZG20260612001.pdf', NOW(), NOW());

-- 8. 整改反馈（企业提交 - 含图片/视频/附件）
INSERT IGNORE INTO rectification_feedback (id, notice_id, enterprise_id, rectify_measures, evidence_images, evidence_videos, remark, check_type, attachment_urls, status, created_at) VALUES
(1, 1, 1, '已完成全部4项问题整改：1.车间地面排水系统已修缮完毕，新安装3个排水口；2.冷库温度记录系统已更换为自动记录仪，数据实时上传；3.3名过期健康证人员已重新体检并取得新证；4.食品添加剂台账已按新模板重新登记。详见附件照片和视频。',
'["/uploads/feedback/1/车间排水改造.jpg","/uploads/feedback/1/冷库自动记录仪.jpg","/uploads/feedback/1/新健康证.jpg","/uploads/feedback/1/添加剂台账.jpg"]',
'["/uploads/feedback/1/车间改造视频.mp4"]',
'整改费用合计约2.3万元，其中排水修缮1.2万、温度记录仪0.8万、体检费0.3万。',
'食品生产科',
'["/uploads/feedback/1/整改报告.pdf","/uploads/feedback/1/排水工程验收单.pdf"]',
'SUBMITTED', NOW()),

(2, 2, 2, '已完成全部3项问题整改：1.消防通道杂物已全部清理，设置通道标识和禁停线；2.所有商品已按规定离地15cm存放，加装货架；3.2个过期灭火器已更换为新品，并完成全库区8个灭火器检查。',
'["/uploads/feedback/2/消防通道清理.jpg","/uploads/feedback/2/商品离地存放.jpg","/uploads/feedback/2/新灭火器.jpg","/uploads/feedback/2/全库区灭火器检查记录.jpg"]',
'[]',
'整改费用合计约0.6万元，其中灭火器更换0.2万、货架加装0.3万、标识制作0.1万。',
'商贸流通科',
'["/uploads/feedback/2/整改报告.pdf"]',
'SUBMITTED', NOW()),

(3, 3, 3, '部分整改完成：1.2台叉车已重新办理使用登记证（附证）；2.压力容器安全阀已送校，预计7月5日取回；3.操作人员已报名培训，预计7月中旬取证；4.起重机械限位装置已更换新件并通过测试。',
'["/uploads/feedback/3/叉车新登记证.jpg","/uploads/feedback/3/限位装置更换.jpg","/uploads/feedback/3/安全阀送校回执.jpg"]',
'["/uploads/feedback/3/限位装置测试.mp4"]',
'安全阀校验和操作人员取证尚需时间，预计7月15日前全部完成。限位装置已更换并测试通过。',
'特种设备科',
'["/uploads/feedback/3/整改进度报告.pdf","/uploads/feedback/3/培训报名确认.pdf"]',
'SUBMITTED', NOW());

-- 9. 企业联系人
INSERT IGNORE INTO enterprise_contact (id, enterprise_id, contact_name, contact_phone, position, email, is_primary, created_at, updated_at) VALUES
(1, 1, '陈志明', '13999001001', '总经理', 'tianshan@aksu.com', 1, NOW(), NOW()),
(2, 1, '李小红', '13999001101', '质量负责人', 'xiaohong@aksu.com', 0, NOW(), NOW()),
(3, 2, '马文龙', '13999001002', '总经理', 'xinglong@kuqa.com', 1, NOW(), NOW()),
(4, 3, '刘强', '13999001003', '总经理', 'hengli@wsx.gov.cn', 1, NOW(), NOW()),
(5, 4, '买买提·艾力', '13999001004', '总经理', 'lvzhou@shaya.com', 1, NOW(), NOW()),
(6, 5, '赵国栋', '13999001005', '总经理', 'tianyuan@baicheng.com', 1, NOW(), NOW()),
(7, 6, '孙立伟', '13999001006', '总经理', 'hongyuan@aksu.com', 1, NOW(), NOW()),
(8, 7, '阿卜杜拉·穆萨', '13999001007', '总经理', 'shunda@xinhe.com', 1, NOW(), NOW());

-- 10. 消息通知
INSERT INTO message (user_type, user_id, msg_type, title, content, is_read, related_id, related_type, link, created_at) VALUES
('INSPECTOR', 2, 'TASK', '新检查任务分配', '您有一个新的检查任务：阿克苏市食品生产企业专项检查，计划日期2026-06-20', 0, 1, 'INSPECTION_TASK', '/inspection/1', NOW()),
('INSPECTOR', 2, 'NOTIFICATION', '企业已提交整改反馈', '阿克苏市天山食品有限责任公司已提交食品安全整改反馈，请及时审核', 0, 1, 'RECTIFICATION_FEEDBACK', '/rectification/1', NOW()),
('ENTERPRISE', 5, 'RECTIFICATION', '收到整改通知', '您收到一份整改通知：食品安全整改通知，请于2026-07-05前完成整改', 0, 1, 'RECTIFICATION_NOTICE', '/rectification/1', NOW()),
('INSPECTOR', 3, 'NOTIFICATION', '企业已提交整改反馈', '库车市兴隆商贸有限公司已提交消防安全隐患整改反馈，请及时审核', 0, 2, 'RECTIFICATION_FEEDBACK', '/rectification/2', NOW()),
('ENTERPRISE', 2, 'RECTIFICATION', '收到整改通知', '您收到一份整改通知：消防安全隐患整改通知，请于2026-07-03前完成整改', 0, 2, 'RECTIFICATION_NOTICE', '/rectification/2', NOW()),
('INSPECTOR', 2, 'NOTIFICATION', '检查报告已生成', '您2026-06-20的检查报告已自动生成，请查看并确认', 1, 1, 'INSPECTION_RECORD', '/inspection/1', NOW()),
('ENTERPRISE', 3, 'RECTIFICATION', '收到整改通知', '您收到一份整改通知：特种设备安全隐患整改通知，请于2026-06-30前完成整改', 0, 3, 'RECTIFICATION_NOTICE', '/rectification/3', NOW()),
('ENTERPRISE', 5, 'RECTIFICATION', '收到整改通知（紧急）', '您收到一份紧急整改通知：安全生产重大隐患整改通知，要求立即停业整改，请于2026-06-25前完成', 0, 4, 'RECTIFICATION_NOTICE', '/rectification/4', NOW());
