# 系统管理模块 - 实施计划

## 阶段1: 数据库与实体层

- [ ] 1. 创建数据库表SQL脚本
  - [ ] 1.1 创建 sys_role 表（角色表）
  - [ ] 1.2 创建 sys_user_role 表（用户角色关联）
  - [ ] 1.3 创建 sys_menu 表（菜单表）
  - [ ] 1.4 创建 sys_role_menu 表（角色菜单关联）
  - [ ] 1.5 创建 sys_audit_log 表（审计日志表）
  - [ ] 1.6 创建 sys_user 扩展字段（phone, email, is_admin）

- [ ] 2. 创建实体类
  - [ ] 2.1 创建 Role 实体
  - [ ] 2.2 创建 UserRole 实体
  - [ ] 2.3 创建 Menu 实体
  - [ ] 2.4 创建 RoleMenu 实体
  - [ ] 2.5 创建 AuditLog 实体
  - [ ] 2.6 更新 User 实体（添加字段）

## 阶段2: Mapper层

- [ ] 3. 创建Mapper接口
  - [ ] 3.1 创建 RoleMapper（含分页）
  - [ ] 3.2 创建 UserRoleMapper
  - [ ] 3.3 创建 MenuMapper（含树形查询）
  - [ ] 3.4 创建 RoleMenuMapper
  - [ ] 3.5 创建 AuditLogMapper（含分页和导出）

- [ ] 4. 创建Mapper XML
  - [ ] 4.1 创建 RoleMapper.xml
  - [ ] 4.2 创建 MenuMapper.xml
  - [ ] 4.3 创建 AuditLogMapper.xml

## 阶段3: 服务层

- [ ] 5. 创建服务接口与实现
  - [ ] 5.1 创建 RoleService 接口与实现
  - [ ] 5.2 创建 MenuService 接口与实现
  - [ ] 5.3 创建 AuditLogService 接口与实现
  - [ ] 5.4 创建 MonitorService 接口与实现
  - [ ] 5.5 更新 UserService（添加角色分配、重置密码）
  - [ ] 5.6 创建系统初始化服务（初始化角色和菜单）

## 阶段4: 权限与安全

- [ ] 6. 创建安全组件
  - [ ] 6.1 创建 RequirePermission 注解
  - [ ] 6.2 创建 PermissionInterceptor 权限拦截器
  - [ ] 6.3 创建 AuditAspect 审计日志切面
  - [ ] 6.4 创建 MetricsUtil 监控指标工具类
  - [ ] 6.5 更新 SecurityConfig（配置权限规则）

## 阶段5: Controller层

- [ ] 7. 创建Controller
  - [ ] 7.1 创建 UserController（用户管理，含角色分配）
  - [ ] 7.2 创建 RoleController（角色管理，含权限分配）
  - [ ] 7.3 创建 MenuController（菜单管理）
  - [ ] 7.4 创建 AuditLogController（审计日志）
  - [ ] 7.5 创建 MonitorController（系统监控）

## 阶段6: 前端页面

- [ ] 8. 前端API封装
  - [ ] 8.1 创建 user.js API模块
  - [ ] 8.2 创建 role.js API模块
  - [ ] 8.3 创建 menu.js API模块
  - [ ] 8.4 创建 audit.js API模块
  - [ ] 8.5 创建 monitor.js API模块

- [ ] 9. 前端页面组件
  - [ ] 9.1 创建 UserList.vue（用户列表）
  - [ ] 9.2 创建 UserForm.vue（用户表单）
  - [ ] 9.3 创建 RoleList.vue（角色列表）
  - [ ] 9.4 创建 RoleForm.vue（角色表单+权限分配）
  - [ ] 9.5 创建 MenuList.vue（菜单树）
  - [ ] 9.6 创建 AuditLogList.vue（审计日志）
  - [ ] 9.7 创建 SystemMonitor.vue（系统监控）

- [ ] 10. 前端路由与菜单
  - [ ] 10.1 添加系统管理路由
  - [ ] 10.2 更新侧边栏菜单（系统管理分组）

## 检查点

- [ ] 确保数据库表创建成功
- [ ] 确保后端服务启动正常
- [ ] 确保前端页面可访问
- [ ] 确保权限控制生效

## 注意事项

1. 数据库变更需要用户提供SQL脚本手动执行
2. 现有 sys_user 表需要添加 phone、email、is_admin 字段
3. 前端使用 Element Plus 组件库
