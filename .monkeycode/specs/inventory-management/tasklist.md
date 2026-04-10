# 需求实施计划

- [x] 1. 初始化项目结构
   - 创建前端项目 (Vue 3 + Vite)
   - 创建后端项目 (Spring Boot)
   - 配置 Git 子模块结构

- [x] 2. 后端基础设施搭建
   - [x] 2.1 配置 pom.xml 依赖
      - Spring Boot 2.7.18
      - MyBatis-Plus 3.5+
      - MySQL Driver
      - EasyExcel
      - MinIO SDK

   - [x] 2.2 创建应用配置
      - application.yml
      - 数据库连接配置
      - 文件上传配置

   - [x] 2.3 配置跨域和 Web 配置
     - CorsConfig
     - WebMvcConfig

   - [x] 2.4 集成 MyBatis-Plus
     - MybatisPlusConfig
     - 配置分页插件

- [x] 3. 后端数据模型实现
   - [x] 3.1 创建实体类
     - Inventory 实体 (商品)
     - InventoryImage 实体 (商品图片)
     - Package 实体 (套餐)
     - PackageItem 实体 (套餐明细)

   - [x] 3.2 创建 DTO 类
     - InventoryDTO / InventoryQuery
     - InventoryImageDTO
     - PackageDTO / PackageQuery
     - ImportResultDTO
     - 统一响应 Result 类

   - [x] 3.3 创建 Mapper 接口
     - InventoryMapper
     - InventoryImageMapper
     - PackageMapper
     - PackageItemMapper

- [x] 4. 后端库存模块实现
   - [x] 4.1 实现 InventoryService
     - 分页查询商品列表
     - 新增商品 (含图片)
     - 修改商品 (含图片整体替换)
     - 删除商品 (级联删除图片)
     - 根据 ID 查询商品详情

   - [x] 4.2 实现图片管理
     - 上传商品图片
     - 删除商品图片
     - 设置商品封面

   - [x] 4.3 实现 Excel 导入
     - 生成导入模板
     - 批量导入商品 (支持多图片)
     - 款号唯一性校验
     - 返回导入结果

   - [x] 4.4 实现 InventoryController
     - GET /api/inventory - 商品列表
     - POST /api/inventory - 新增商品
     - PUT /api/inventory/{id} - 修改商品
     - DELETE /api/inventory/{id} - 删除商品
     - POST /api/inventory/import - 批量导入
     - GET /api/inventory/template - 获取模板
     - POST /api/inventory/{id}/images - 上传图片
     - DELETE /api/inventory/{id}/images/{imageId} - 删除图片
     - PUT /api/inventory/{id}/images/{imageId}/cover - 设置封面

- [x] 5. 后端套餐模块实现
   - [x] 5.1 实现 PackageService
     - 分页查询套餐列表
     - 新增套餐 (含明细)
     - 修改套餐 (含明细整体替换)
     - 删除套餐 (级联删除明细)
     - 查询套餐详情 (含商品信息)
     - 计算毛利率

   - [x] 5.2 实现 PackageController
     - GET /api/package - 套餐列表
     - POST /api/package - 新增套餐
     - PUT /api/package/{id} - 修改套餐
     - DELETE /api/package/{id} - 删除套餐
     - GET /api/package/{id} - 套餐详情
     - POST /api/package/calculate-profit - 计算毛利率

- [x] 6. 检查点 - 后端功能验证
   - 启动后端服务
   - 测试库存 CRUD 接口
   - 测试套餐 CRUD 接口
   - 测试 Excel 导入功能

- [x] 7. 前端基础设施搭建
   - [x] 7.1 初始化 Vue 3 项目
     - Vite 配置
     - Element Plus 按需引入
     - 路由配置
     - Axios 封装

   - [x] 7.2 创建公共组件
     - ImageUpload 图片上传组件
     - ProfitCalculator 毛利率计算组件
     - 统一响应处理

- [x] 8. 前端库存模块实现
   - [x] 8.1 实现 API 封装
     - inventory.js - 库存接口

   - [x] 8.2 实现页面组件
     - InventoryList.vue - 商品列表页
     - InventoryForm.vue - 商品表单页 (新增/编辑)
     - ImportDialog.vue - 批量导入对话框

   - [x] 8.3 实现状态管理
     - inventory store

- [x] 9. 前端套餐模块实现
   - [x] 9.1 实现 API 封装
     - package.js - 套餐接口

   - [x] 9.2 实现页面组件
     - PackageList.vue - 套餐列表页
     - PackageForm.vue - 套餐表单页 (新增/编辑)
     - PackageDetail.vue - 套餐详情

   - [x] 9.3 实现状态管理
     - package store

- [x] 10. 检查点 - 前端功能验证
    - 启动前端服务
    - 测试商品列表页
    - 测试商品表单
    - 测试套餐列表页
    - 测试套餐表单
    - 测试毛利率计算

- [x] 11. 项目文档完善
    - 更新 README.md
    - 补充运行说明
    - 添加构建指南
