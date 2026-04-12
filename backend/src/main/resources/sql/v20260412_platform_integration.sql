-- ============================================
-- 电商平台库存对接功能 - 数据库变更脚本
-- 执行时间: 2026-04-12
-- ============================================

USE inventory;

-- 1. 平台配置表
CREATE TABLE IF NOT EXISTS platform_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    platform VARCHAR(20) NOT NULL COMMENT '平台标识（XIAOHONGSHU/DOUYIN/KUAISHOU/JD/TAOBAO）',
    shop_name VARCHAR(100) COMMENT '店铺名称',
    app_key VARCHAR(255) COMMENT 'API AppKey（加密存储）',
    app_secret VARCHAR(255) COMMENT 'API AppSecret（加密存储）',
    access_token VARCHAR(255) COMMENT 'Access Token（加密存储）',
    refresh_token VARCHAR(255) COMMENT 'Refresh Token（加密存储）',
    token_expire_time DATETIME COMMENT 'Token过期时间',
    webhook_url VARCHAR(255) COMMENT 'Webhook回调地址',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用（0禁用/1启用）',
    sync_mode VARCHAR(20) DEFAULT 'IMMEDIATE' COMMENT '同步模式（IMMEDIATE即时/SCHEDULED定时）',
    sync_interval INT DEFAULT 5 COMMENT '同步周期（分钟）',
    last_sync_time DATETIME COMMENT '最后同步时间',
    last_heartbeat DATETIME COMMENT '最后心跳时间',
    status VARCHAR(20) DEFAULT 'DISCONNECTED' COMMENT '连接状态（CONNECTED/DISCONNECTED/ERROR）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_platform (platform),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台配置表';

-- 2. SKU映射表
CREATE TABLE IF NOT EXISTS sku_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    inventory_id BIGINT NOT NULL COMMENT '系统商品ID',
    platform VARCHAR(20) NOT NULL COMMENT '平台标识',
    platform_sku VARCHAR(100) NOT NULL COMMENT '平台商品SKU',
    platform_product_id VARCHAR(100) COMMENT '平台商品ID',
    last_sync_quantity INT DEFAULT 0 COMMENT '最后同步数量',
    last_sync_time DATETIME COMMENT '最后同步时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_platform_sku (platform, platform_sku),
    INDEX idx_inventory_id (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU映射表';

-- 3. 同步日志表
CREATE TABLE IF NOT EXISTS sync_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    platform VARCHAR(20) NOT NULL COMMENT '平台标识',
    order_id VARCHAR(100) COMMENT '平台订单号',
    inventory_id BIGINT COMMENT '系统商品ID',
    platform_sku VARCHAR(100) COMMENT '平台SKU',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型（SALE_OUT销售出库/REFUND_IN退货入库）',
    quantity_change INT NOT NULL COMMENT '变动数量（正数入库/负数出库）',
    quantity_before INT COMMENT '变动前库存',
    quantity_after INT COMMENT '变动后库存',
    api_request_id VARCHAR(100) COMMENT 'API请求ID',
    sync_status VARCHAR(20) DEFAULT 'SUCCESS' COMMENT '同步状态（SUCCESS成功/FAILED失败/PENDING处理中/RETRYING重试中）',
    error_message TEXT COMMENT '错误信息',
    raw_request TEXT COMMENT '原始请求数据',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_platform (platform),
    INDEX idx_order_id (order_id),
    INDEX idx_inventory_id (inventory_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_sync_status (sync_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步日志表';

-- 4. 平台订单表
CREATE TABLE IF NOT EXISTS platform_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    platform VARCHAR(20) NOT NULL COMMENT '平台标识',
    order_id VARCHAR(100) NOT NULL COMMENT '平台订单号',
    sku_mapping_id BIGINT COMMENT 'SKU映射ID',
    order_type VARCHAR(20) NOT NULL COMMENT '订单类型（SALE销售/REFUND退货）',
    quantity INT NOT NULL COMMENT '商品数量',
    amount DECIMAL(15,2) COMMENT '订单金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态',
    paid_time DATETIME COMMENT '支付时间',
    refund_time DATETIME COMMENT '退款时间',
    processed TINYINT DEFAULT 0 COMMENT '是否已处理（0未处理/1已处理）',
    raw_data TEXT COMMENT '原始JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_platform_order (platform, order_id),
    INDEX idx_sku_mapping_id (sku_mapping_id),
    INDEX idx_order_type (order_type),
    INDEX idx_processed (processed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台订单表';

-- 初始数据：添加示例平台配置（可选）
-- INSERT INTO platform_config (platform, shop_name, status) VALUES 
-- ('XIAOHONGSHU', '示例小红书店铺', 'DISCONNECTED'),
-- ('DOUYIN', '示例抖音店铺', 'DISCONNECTED'),
-- ('KUAISHOU', '示例快手店铺', 'DISCONNECTED'),
-- ('JD', '示例京东店铺', 'DISCONNECTED'),
-- ('TAOBAO', '示例淘宝店铺', 'DISCONNECTED');
