-- ============================================
-- 系统管理模块 - 数据库变更脚本
-- 执行时间: 2026-04-12
-- ============================================

USE inventory;

-- 1. 更新 sys_user 表 - 添加扩展字段
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS phone VARCHAR(20) COMMENT '手机号' AFTER nickname;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS email VARCHAR(100) COMMENT '邮箱' AFTER phone;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS is_admin TINYINT DEFAULT 0 COMMENT '是否管理员（0否 1是）' AFTER status;

-- 2. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统内置（1=是）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID（0=根）',
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    type CHAR DEFAULT '2' COMMENT '类型（1=目录 2=菜单 3=按钮）',
    path VARCHAR(255) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    permission VARCHAR(100) COMMENT '权限标识',
    status CHAR DEFAULT '1' COMMENT '状态（0=禁用 1=启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 5. 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 6. 审计日志表
CREATE TABLE IF NOT EXISTS sys_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(50) COMMENT '操作类型',
    content TEXT COMMENT '操作内容',
    method VARCHAR(200) COMMENT '请求方法',
    ip VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '浏览器信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_operation (operation),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入预定义角色
INSERT INTO sys_role (name, code, description, is_system) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 1),
('系统管理员', 'ADMIN', '拥有除角色管理外的所有权限', 1),
('操作员', 'OPERATOR', '只能操作业务功能', 1),
('查看者', 'VIEWER', '只能查看数据', 1);

-- 插入预定义菜单
INSERT INTO sys_menu (parent_id, name, type, path, component, icon, sort, permission, status) VALUES
(0, '系统管理', '1', '/system', NULL, 'el-icon-setting', 100, NULL, '1'),
(1, '用户管理', '2', '/system/user', 'system/user/UserList', 'el-icon-user', 1, 'system:user:list', '1'),
(1, '角色管理', '2', '/system/role', 'system/role/RoleList', 'el-icon-postcard', 2, 'system:role:list', '1'),
(1, '菜单管理', '2', '/system/menu', 'system/menu/MenuList', 'el-icon-menu', 3, 'system:menu:list', '1'),
(1, '审计日志', '2', '/system/audit', 'system/audit/AuditLogList', 'el-icon-document', 4, 'system:audit:list', '1'),
(1, '系统监控', '2', '/system/monitor', 'system/monitor/SystemMonitor', 'el-icon-monitor', 5, 'system:monitor:view', '1'),
(0, '商品管理', '1', '/inventory', NULL, 'el-icon-goods', 1, NULL, '1'),
(0, '套餐管理', '1', '/package', NULL, 'el-icon-present', 2, NULL, '1'),
(0, '统计报表', '1', '/stats', NULL, 'el-icon-data-analysis', 3, NULL, '1'),
(0, '平台对接', '1', '/platform', NULL, 'el-icon-connection', 4, NULL, '1');

-- 为超级管理员分配所有菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE status = '1';

-- 为系统管理员分配菜单（除角色管理外）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu WHERE status = '1' AND id != 2;

-- 为操作员分配菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu WHERE status = '1' AND type != '1' AND name NOT IN ('用户管理', '角色管理', '菜单管理', '审计日志', '系统监控');

-- 为查看者分配只读菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 4, id FROM sys_menu WHERE status = '1' AND type IN ('2');

-- 将admin用户设置为管理员
UPDATE sys_user SET is_admin = 1 WHERE username = 'admin';

-- 为admin用户分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT id, 1 FROM sys_user WHERE username = 'admin';
