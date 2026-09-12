-- Step 2: Insert 10 Bureaus (level=3) under each county/city
INSERT INTO org_structure (name, parent_id, level, org_type, sort_order, status, org_code, org_path) VALUES
('阿克苏地区市场监督管理局', 1, 3, 'GOVERNMENT', 1, 1, 'ORG-REGION', '/1/1/'),
('阿克苏市市场监督管理局', 2, 3, 'GOVERNMENT', 2, 1, 'ORG-AKS', '/1/2/'),
('库车市市场监督管理局', 3, 3, 'GOVERNMENT', 3, 1, 'ORG-KUCHE', '/1/3/'),
('温宿县市场监督管理局', 4, 3, 'GOVERNMENT', 4, 1, 'ORG-WENSU', '/1/4/'),
('拜城县市场监督管理局', 5, 3, 'GOVERNMENT', 5, 1, 'ORG-BAICHENG', '/1/5/'),
('新和县市场监督管理局', 6, 3, 'GOVERNMENT', 6, 1, 'ORG-XINHE', '/1/6/'),
('沙雅县市场监督管理局', 7, 3, 'GOVERNMENT', 7, 1, 'ORG-SHAYA', '/1/7/'),
('乌什县市场监督管理局', 8, 3, 'GOVERNMENT', 8, 1, 'ORG-WUSHI', '/1/8/'),
('阿瓦提县市场监督管理局', 9, 3, 'GOVERNMENT', 9, 1, 'ORG-AWATI', '/1/9/'),
('柯坪县市场监督管理局', 10, 3, 'GOVERNMENT', 10, 1, 'ORG-KEPING', '/1/10/');