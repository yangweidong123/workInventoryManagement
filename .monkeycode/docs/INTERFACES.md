# 接口定义

## 概述

前后端通过 RESTful API 通信，使用 JSON 格式传输数据。

### 基础信息

- 基础路径: `/api`
- 数据格式: `application/json`
- 编码: `UTF-8`

### 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200 成功，其他失败 |
| message | string | 提示信息 |
| data | object | 返回数据 |

### 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

---

## 库存模块接口

### 1. 商品列表

**请求**
```
GET /api/inventory
```

**Query 参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | int | 否 | 当前页，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| styleNo | string | 否 | 款号（模糊查询） |
| name | string | 否 | 品名（模糊查询） |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "styleNo": "STY001",
        "name": "黄金手镯",
        "sizeMm": "56圈口",
        "weightKg": 15.5,
        "boxSpec": 1,
        "priceExclTax": 8500.00,
        "guidePrice": 12000.00,
        "minPrice": 10000.00,
        "quantity": 10,
        "goodsValue": 85000.00,
        "totalWeight": 155.00
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

### 2. 新增商品

**请求**
```
POST /api/inventory
```

**请求体**
```json
{
  "styleNo": "STY001",
  "name": "黄金手镯",
  "image": "data:image/jpeg;base64,...",
  "sizeMm": "56圈口",
  "weightKg": 15.5,
  "boxSpec": 1,
  "priceExclTax": 8500.00,
  "guidePrice": 12000.00,
  "minPrice": 10000.00,
  "quantity": 10
}
```

**校验规则**
- styleNo: 必填，最大 50 字符，唯一
- name: 必填，最大 100 字符
- priceExclTax/guidePrice/minPrice: 必填，必须为正数
- quantity: 必填，必须为整数

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "styleNo": "STY001"
  }
}
```

### 3. 修改商品

**请求**
```
PUT /api/inventory/{id}
```

**请求体**
```json
{
  "name": "黄金手镯（升级版）",
  "image": "data:image/jpeg;base64,...",
  "sizeMm": "56圈口",
  "weightKg": 16.0,
  "boxSpec": 1,
  "priceExclTax": 9000.00,
  "guidePrice": 12500.00,
  "minPrice": 10500.00,
  "quantity": 8
}
```

**说明**: 款号不允许修改

### 4. 删除商品

**请求**
```
DELETE /api/inventory/{id}
```

**响应示例**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 5. 批量导入

**请求**
```
POST /api/inventory/import
Content-Type: multipart/form-data
```

**Form 参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | Excel 文件 (.xlsx) |

**Excel 模板格式**

| 款号 | 品名 | 产品尺寸 | 参考裸重(KG) | 箱规 | 不含税拿货价 | 市场指导价 | 市场最低控价 | 数量 | 图片(Base64) |
|------|------|----------|--------------|------|-------------|-----------|-------------|------|-------------|

**响应示例**
```json
{
  "code": 200,
  "message": "导入成功 980 条，失败 20 条",
  "data": {
    "successCount": 980,
    "failCount": 20,
    "errors": [
      {
        "row": 5,
        "message": "款号 STY005 已存在"
      }
    ]
  }
}
```

### 6. 获取导入模板

**请求**
```
GET /api/inventory/template
```

**响应**: 返回 Excel 文件流

---

## 套餐模块接口

### 1. 套餐列表

**请求**
```
GET /api/package
```

**Query 参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | int | 否 | 当前页，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| name | string | 否 | 套餐名称（模糊查询） |

### 2. 新增套餐

**请求**
```
POST /api/package
```

**请求体**
```json
{
  "name": "情侣套餐",
  "totalPrice": 20000.00,
  "items": [
    {
      "inventoryId": 1,
      "quantity": 1
    },
    {
      "inventoryId": 2,
      "quantity": 1
    }
  ]
}
```

**校验规则**
- 同一套餐内商品不能重复
- 套餐总价不能低于成本价

### 3. 修改套餐

**请求**
```
PUT /api/package/{id}
```

**请求体**
```json
{
  "name": "情侣套餐（促销）",
  "totalPrice": 18000.00,
  "items": [
    {
      "inventoryId": 1,
      "quantity": 1
    },
    {
      "inventoryId": 2,
      "quantity": 2
    }
  ]
}
```

### 4. 删除套餐

**请求**
```
DELETE /api/package/{id}
```

### 5. 计算毛利率

**请求**
```
POST /api/package/calculate-profit
```

**请求体**
```json
{
  "totalPrice": 20000.00,
  "items": [
    {
      "inventoryId": 1,
      "quantity": 1
    }
  ]
}
```

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "costPrice": 16000.00,
    "totalPrice": 20000.00,
    "profitRate": 25.00
  }
}
```

### 6. 套餐详情

**请求**
```
GET /api/package/{id}
```

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "情侣套餐",
    "totalPrice": 20000.00,
    "costPrice": 16000.00,
    "profitRate": 25.00,
    "items": [
      {
        "id": 1,
        "inventoryId": 1,
        "inventoryName": "黄金手镯",
        "styleNo": "STY001",
        "priceExclTax": 8500.00,
        "quantity": 1,
        "subtotal": 8500.00
      }
    ]
  }
}
```

---

## 错误码定义

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 数据冲突（如款号重复） |
| 500 | 服务器内部错误 |

---

## 图片上传说明

### Base64 方式

将图片转为 Base64 字符串，直接存储到数据库字段。

```json
{
  "image": "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
}
```

### MinIO 方式

上传图片到 MinIO 对象存储，返回 URL。

**请求**
```
POST /api/upload/image
Content-Type: multipart/form-data
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 图片文件 |

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "http://minio:9000/inventory/xxx.jpg"
  }
}
```
