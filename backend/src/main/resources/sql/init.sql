CREATE DATABASE IF NOT EXISTS inventory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE inventory;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    status CHAR(1) DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

INSERT INTO sys_user (username, password, nickname, status) VALUES 
('admin', '123456', '管理员', '1');

CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_no VARCHAR(50) NOT NULL UNIQUE COMMENT '款号',
    name VARCHAR(100) NOT NULL COMMENT '品名',
    size_mm VARCHAR(50) COMMENT '产品尺寸',
    weight_kg DECIMAL(10,3) COMMENT '参考裸重(KG)',
    box_spec INT DEFAULT 1 COMMENT '箱规',
    price_excl_tax DECIMAL(10,2) NOT NULL COMMENT '不含税拿货价',
    guide_price DECIMAL(10,2) NOT NULL COMMENT '市场指导价',
    min_price DECIMAL(10,2) NOT NULL COMMENT '市场最低控价',
    package_price DECIMAL(10,2) COMMENT '套餐金额',
    quantity INT NOT NULL DEFAULT 0 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_style_no (style_no),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE IF NOT EXISTS inventory_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inventory_id BIGINT NOT NULL COMMENT '商品ID',
    image_url TEXT NOT NULL COMMENT '图片地址',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inventory_id) REFERENCES inventory(id) ON DELETE CASCADE,
    INDEX idx_inventory_id (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

CREATE TABLE IF NOT EXISTS package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    total_price DECIMAL(10,2) NOT NULL COMMENT '套餐总价',
    cost_price DECIMAL(10,2) NOT NULL COMMENT '成本总价',
    profit_rate DECIMAL(5,2) NOT NULL COMMENT '毛利率',
    sold_quantity INT DEFAULT 0 COMMENT '已售数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐表';

CREATE TABLE IF NOT EXISTS package_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    inventory_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    FOREIGN KEY (package_id) REFERENCES package(id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES inventory(id) ON DELETE CASCADE,
    UNIQUE KEY uk_package_inventory (package_id, inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐明细表';

CREATE TABLE IF NOT EXISTS inventory_out (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inventory_id BIGINT NOT NULL COMMENT '商品ID',
    style_no VARCHAR(50) COMMENT '款号（冗余字段）',
    name VARCHAR(100) COMMENT '品名（冗余字段）',
    quantity INT NOT NULL COMMENT '出库数量',
    price DECIMAL(10,2) COMMENT '出库单价',
    total_amount DECIMAL(10,2) COMMENT '出库总金额',
    out_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
    operator VARCHAR(50) COMMENT '操作人',
    remark VARCHAR(255) COMMENT '备注',
    INDEX idx_inventory_id (inventory_id),
    INDEX idx_out_time (out_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品出库记录表';

CREATE TABLE IF NOT EXISTS daily_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stat_date DATE NOT NULL COMMENT '统计日期',
    inventory_in_count INT DEFAULT 0 COMMENT '商品入库数量',
    inventory_in_amount DECIMAL(15,2) DEFAULT 0 COMMENT '商品入库金额',
    inventory_out_count INT DEFAULT 0 COMMENT '商品出库数量',
    inventory_out_amount DECIMAL(15,2) DEFAULT 0 COMMENT '商品出库金额',
    package_sold_count INT DEFAULT 0 COMMENT '套餐销售数量',
    package_sold_amount DECIMAL(15,2) DEFAULT 0 COMMENT '套餐销售金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计表';

CREATE TABLE IF NOT EXISTS package_sold_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    package_name VARCHAR(100) COMMENT '套餐名称（冗余字段）',
    quantity INT NOT NULL COMMENT '销售数量',
    unit_price DECIMAL(10,2) COMMENT '套餐单价',
    total_amount DECIMAL(15,2) COMMENT '销售总金额',
    sold_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
    operator VARCHAR(50) COMMENT '操作人',
    INDEX idx_package_id (package_id),
    INDEX idx_sold_time (sold_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐销售记录表';
