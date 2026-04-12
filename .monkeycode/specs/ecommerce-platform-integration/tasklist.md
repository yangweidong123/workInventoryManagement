# 电商平台库存对接 - 实施计划

## 阶段1: 数据库与实体层

- [ ] 1. 创建数据库表
  - [ ] 1.1 创建 platform_config 表（平台配置）
  - [ ] 1.2 创建 sku_mapping 表（SKU映射）
  - [ ] 1.3 创建 sync_log 表（同步日志）
  - [ ] 1.4 创建 platform_order 表（平台订单）
  - [ ] 1.5 更新 init.sql 并提供用户手动执行

- [ ] 2. 创建实体类
  - [ ] 2.1 创建 PlatformConfig 实体
  - [ ] 2.2 创建 SkuMapping 实体
  - [ ] 2.3 创建 SyncLog 实体
  - [ ] 2.4 创建 PlatformOrder 实体

## 阶段2: 枚举与DTO

- [ ] 3. 创建枚举类
  - [ ] 3.1 创建 PlatformType 枚举（小红书/抖音/快手/京东/淘宝）
  - [ ] 3.2 创建 OrderType 枚举（销售订单/退货订单）
  - [ ] 3.3 创建 OrderStatus 枚举（待处理/已支付/已发货等）
  - [ ] 3.4 创建 SyncMode 枚举（即时同步/定时同步）
  - [ ] 3.5 创建 SyncStatus 枚举（成功/失败/处理中/重试中）

- [ ] 4. 创建DTO类
  - [ ] 4.1 创建 StandardOrder 标准化订单格式
  - [ ] 4.2 创建 PlatformCredential 平台凭证
  - [ ] 4.3 创建 SyncResult 同步结果

## 阶段3: Mapper层

- [ ] 5. 创建Mapper接口
  - [ ] 5.1 创建 PlatformConfigMapper
  - [ ] 5.2 创建 SkuMappingMapper
  - [ ] 5.3 创建 SyncLogMapper
  - [ ] 5.4 创建 PlatformOrderMapper

- [ ] 6. 创建Mapper XML
  - [ ] 6.1 创建 PlatformConfigMapper.xml
  - [ ] 6.2 创建 SkuMappingMapper.xml
  - [ ] 6.3 创建 SyncLogMapper.xml
  - [ ] 6.4 创建 PlatformOrderMapper.xml

## 阶段4: 平台适配器层

- [ ] 7. 创建适配器接口与基础类
  - [ ] 7.1 创建 PlatformAdapter 接口
  - [ ] 7.2 创建 AbstractPlatformAdapter 抽象类
  - [ ] 7.3 创建订单标准化处理器 OrderNormalizer

- [ ] 8. 实现各平台适配器（仅Mock实现）
  - [ ] 8.1 实现 XiaohongshuAdapter（小红书）
  - [ ] 8.2 实现 DouyinAdapter（抖音）
  - [ ] 8.3 实现 KuaishouAdapter（快手）
  - [ ] 8.4 实现 JdAdapter（京东）
  - [ ] 8.5 实现 TaobaoAdapter（淘宝）

## 阶段5: 核心服务层

- [ ] 9. 实现服务接口与实现
  - [ ] 9.1 创建 PlatformConfigService 接口与实现
  - [ ] 9.2 创建 SkuMappingService 接口与实现
  - [ ] 9.3 创建 OrderProcessService 接口与实现（订单处理）
  - [ ] 9.4 创建 InventorySyncService 接口与实现（库存同步）
  - [ ] 9.5 创建 SyncLogService 接口与实现（日志查询）

- [ ] 10. 实现工具类
  - [ ] 10.1 创建 CryptoUtil 加密工具类
  - [ ] 10.2 创建 RetryUtil 重试工具类

## 阶段6: Controller层

- [ ] 11. 创建Controller
  - [ ] 11.1 创建 PlatformConfigController（平台配置管理）
  - [ ] 11.2 创建 SkuMappingController（SKU映射管理）
  - [ ] 11.3 创建 SyncLogController（同步日志查询）
  - [ ] 11.4 创建 WebhookController（接收平台Webhook回调）

## 阶段7: 调度器与配置

- [ ] 12. 创建调度器与配置
  - [ ] 12.1 创建 PlatformPollingScheduler（轮询调度器）
  - [ ] 12.2 创建 WebhookConfig（Webhook路由配置）
  - [ ] 12.3 配置Spring定时任务启用

## 阶段8: 前端页面

- [ ] 13. 前端API封装
  - [ ] 13.1 创建 platform.js API模块
  - [ ] 13.2 创建 sku-mapping.js API模块
  - [ ] 13.3 创建 sync-log.js API模块

- [ ] 14. 前端页面组件
  - [ ] 14.1 创建 PlatformConfigList.vue（平台配置列表）
  - [ ] 14.2 创建 PlatformConfigForm.vue（平台配置表单）
  - [ ] 14.3 创建 SkuMappingList.vue（SKU映射管理）
  - [ ] 14.4 创建 SyncLogList.vue（同步日志查询）
  - [ ] 14.5 创建 MonitorDashboard.vue（监控面板）

- [ ] 15. 前端路由配置
  - [ ] 15.1 添加平台管理相关路由

## 检查点

- [ ] 确保数据库表创建成功
- [ ] 确保后端服务启动正常
- [ ] 确保前端页面可访问

## 注意事项

1. 数据库变更需要用户提供SQL脚本手动执行
2. 各平台适配器初期为Mock实现，实际API对接需后续完成
3. 前端使用Element Plus组件库（现有项目技术栈）
