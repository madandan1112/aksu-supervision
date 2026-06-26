-- 组织架构表
CREATE TABLE IF NOT EXISTS org_structure (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '组织名称',
    parent_id BIGINT COMMENT '父级ID',
    level INT NOT NULL COMMENT '层级：1地区,2县市,3乡镇街道,4单位,5科室',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：1启用,0禁用',
    description VARCHAR(500) COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES org_structure(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织架构表';

-- 插入阿克苏地区根节点
INSERT INTO org_structure (name, parent_id, level, sort_order, status, description) VALUES
('阿克苏地区', NULL, 1, 0, 1, '阿克苏地区市场监督管理局');

-- 插入7县2市
INSERT INTO org_structure (name, parent_id, level, sort_order, status, description) VALUES
('阿克苏市', 1, 2, 1, 1, NULL),
('库车市', 1, 2, 2, 1, NULL),
('温宿县', 1, 2, 3, 1, NULL),
('拜城县', 1, 2, 4, 1, NULL),
('新和县', 1, 2, 5, 1, NULL),
('沙雅县', 1, 2, 6, 1, NULL),
('乌什县', 1, 2, 7, 1, NULL),
('阿瓦提县', 1, 2, 8, 1, NULL),
('柯坪县', 1, 2, 9, 1, NULL);
