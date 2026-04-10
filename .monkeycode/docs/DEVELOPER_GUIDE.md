# 开发指南

## 环境要求

### 前端环境

- Node.js >= 18.0.0
- npm >= 9.0.0 或 pnpm >= 8.0.0

### 后端环境

- JDK >= 1.8
- Maven >= 3.8
- MySQL >= 8.0
- MinIO (可选)

### 开发工具

- IDE: VS Code (前端) / IntelliJ IDEA (后端)
- Git
- MySQL Workbench (数据库管理)

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd inventory-management
```

### 2. 后端启动

```bash
cd backend

# 创建数据库
mysql -u root -p < src/main/resources/sql/init.sql

# 修改配置
vim src/main/resources/application.yml

# 启动后端
mvn spring-boot:run
```

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 4. 访问应用

- 前端: http://localhost:5173
- 后端 API: http://localhost:8080/api

## 项目结构说明

### 前端目录结构

```
frontend/
├── src/
│   ├── api/                    # API 接口封装
│   │   ├── inventory.js       # 库存模块接口
│   │   └── package.js         # 套餐模块接口
│   │
│   ├── components/             # 公共组件
│   │   ├── ImageUpload.vue     # 图片上传组件
│   │   └── ProfitCalculator.vue # 毛利率计算组件
│   │
│   ├── views/                 # 页面视图
│   │   ├── inventory/
│   │   │   ├── List.vue       # 商品列表页
│   │   │   ├── Form.vue       # 商品表单页
│   │   │   └── Import.vue     # 批量导入页
│   │   │
│   │   └── package/
│   │       ├── List.vue       # 套餐列表页
│   │       └── Form.vue       # 套餐表单页
│   │
│   ├── router/
│   │   └── index.js           # 路由配置
│   │
│   ├── stores/                # 状态管理
│   │   └── inventory.js       # 库存状态
│   │
│   ├── utils/                 # 工具函数
│   │   └── request.js        # Axios 封装
│   │
│   └── App.vue
│
├── vite.config.js            # Vite 配置
└── package.json
```

### 后端目录结构

```
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/inventory/
│       │       │
│       │       ├── InventoryApplication.java
│       │       │
│       │       ├── controller/
│       │       │   ├── InventoryController.java
│       │       │   └── PackageController.java
│       │       │
│       │       ├── service/
│       │       │   ├── InventoryService.java
│       │       │   ├── InventoryServiceImpl.java
│       │       │   ├── PackageService.java
│       │       │   └── PackageServiceImpl.java
│       │       │
│       │       ├── mapper/
│       │       │   ├── InventoryMapper.java
│       │       │   └── PackageMapper.java
│       │       │
│       │       ├── entity/
│       │       │   ├── Inventory.java
│       │       │   ├── Package.java
│       │       │   └── PackageItem.java
│       │       │
│       │       ├── dto/
│       │       │   ├── InventoryDTO.java
│       │       │   ├── PackageDTO.java
│       │       │   └── ImportResultDTO.java
│       │       │
│       │       └── config/
│       │           ├── CorsConfig.java
│       │           └── MybatisPlusConfig.java
│       │
│       └── resources/
│           ├── application.yml
│           └── sql/
│               └── init.sql
│
└── pom.xml
```

## 开发规范

### 前端规范

#### 组件命名

- 组件文件使用 PascalCase 命名
- 公共组件放在 `components/` 目录
- 业务组件放在对应模块目录下

#### API 封装

```javascript
// api/inventory.js
import request from '@/utils/request'

export function list(params) {
  return request({
    url: '/inventory',
    method: 'get',
    params
  })
}

export function create(data) {
  return request({
    url: '/inventory',
    method: 'post',
    data
  })
}
```

#### 状态管理

使用 Pinia 进行状态管理。

```javascript
// stores/inventory.js
import { defineStore } from 'pinia'
import { list } from '@/api/inventory'

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    items: [],
    total: 0
  }),
  actions: {
    async fetchList(params) {
      const res = await list(params)
      this.items = res.data.records
      this.total = res.data.total
    }
  }
})
```

### 后端规范

#### 包命名

- `com.inventory.controller` - 控制层
- `com.inventory.service` - 业务层接口
- `com.inventory.service.impl` - 业务层实现
- `com.inventory.mapper` - 数据访问层
- `com.inventory.entity` - 实体类
- `com.inventory.dto` - 数据传输对象

#### Controller 规范

```java
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public Result<Page<Inventory>> list(InventoryQuery query) {
        return Result.success(inventoryService.page(query));
    }

    @PostMapping
    public Result<Long> create(@RequestBody @Valid InventoryDTO dto) {
        return Result.success(inventoryService.create(dto));
    }
}
```

#### Service 规范

```java
public interface InventoryService {
    Page<Inventory> page(InventoryQuery query);
    Long create(InventoryDTO dto);
    void update(Long id, InventoryDTO dto);
    void delete(Long id);
}

@Service
public class InventoryServiceImpl implements InventoryService {
    
    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public Page<Inventory> page(InventoryQuery query) {
        return inventoryMapper.selectPage(query);
    }
}
```

## 常见任务

### 添加新字段

1. 前端：在 `entity` 或 `dto` 中添加字段
2. 后端：在 Entity 类中添加字段，更新 Mapper
3. 数据库：执行 ALTER TABLE 语句

### 添加新接口

1. 后端：Controller 添加方法 -> Service 添加接口和实现 -> Mapper 添加 SQL
2. 前端：在 `api/` 目录添加接口封装
3. 文档：更新 INTERFACES.md

### 添加新模块

1. 创建模块目录结构
2. 添加路由配置
3. 添加 API 接口
4. 更新文档

## 构建与发布

### 前端构建

```bash
# 开发环境构建
npm run build:dev

# 生产环境构建
npm run build:prod

# 预览构建结果
npm run preview
```

### 后端构建

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/inventory.jar

# Docker 构建
docker build -t inventory-backend:latest .
```

### 环境配置

#### 开发环境 (application-dev.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/inventory?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: inventory
```

#### 生产环境 (application-prod.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:3306/inventory?useSSL=true&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
minio:
  endpoint: ${MINIO_ENDPOINT}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket: inventory
```

## 数据库初始化

执行 `src/main/resources/sql/init.sql` 初始化数据库：

```sql
CREATE DATABASE IF NOT EXISTS inventory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE inventory;

CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    image TEXT,
    size_mm VARCHAR(50),
    weight_kg DECIMAL(10,3),
    box_spec INT DEFAULT 1,
    price_excl_tax DECIMAL(10,2) NOT NULL,
    guide_price DECIMAL(10,2) NOT NULL,
    min_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_style_no (style_no),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2) NOT NULL,
    profit_rate DECIMAL(5,2) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE package_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    package_id BIGINT NOT NULL,
    inventory_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (package_id) REFERENCES package(id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES inventory(id) ON DELETE CASCADE,
    UNIQUE KEY uk_package_inventory (package_id, inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
