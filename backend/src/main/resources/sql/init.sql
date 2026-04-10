CREATE DATABASE IF NOT EXISTS inventory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE inventory;

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
