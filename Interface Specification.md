# API 接口规范

本文档旨在定义项目API的统一请求和响应格式，确保前后端协作的一致性和高效性。

## 1. 数据请求规则

### 1.1. 基础URL

所有API的基础路径为 `/api`。

### 1.2. 请求方法

API遵循RESTful设计原则，使用标准的HTTP方法：

-   **GET**: 获取资源。
-   **POST**: 创建新资源。
-   **PUT**: 完全更新现有资源。
-   **PATCH**: 部分更新现有资源。
-   **DELETE**: 删除资源。

### 1.3. 通用请求参数

#### 1.3.1. 分页请求 (GET)

适用于返回资源列表的接口。

-   `page` (`integer`): 请求的页码，从1开始。默认为`1`。
-   `pageSize` (`integer`): 每页返回的数据量。默认为`10`。
-   `search` (`string`, 可选): 用于模糊搜索的关键词。
-   `sortField` (`string`, 可选): 用于排序的字段名。
-   `sortOrder` (`string`, 可选): 排序顺序，可选值为 `asc` (升序) 或 `desc` (降序)。

**示例：**
```http
GET /api/users?page=1&pageSize=10&search=John&sortField=createdAt&sortOrder=desc
```

#### 1.3.2. 创建资源 (POST)

请求体 (Request Body) 应包含要创建的资源数据，格式为 `application/json`。

**示例：**
```http
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### 1.3.3. 更新资源 (PUT / PATCH)

-   **PUT**: 用于完全替换资源。请求体应包含资源的**完整**数据。
-   **PATCH**: 用于部分更新资源。请求体应**仅包含**要修改的字段。

**示例 (PUT):**
```http
PUT /api/users/123
Content-Type: application/json

{
  "name": "Johnathan Doe",
  "email": "johnathan.doe@example.com"
}
```

#### 1.3.4. 删除资源 (DELETE)

通过URL路径中的资源ID指定要删除的资源。

**示例：**
```http
DELETE /api/users/123
```

#### 1.3.5. 查询资源 (GET)

-   **获取列表**: `GET /api/{resource}`
-   **获取单个资源**: `GET /api/{resource}/{id}`

**示例：**
```http
GET /api/users
GET /api/users/123
```

## 2. 数据返回规则

### 2.1. 通用返回结构

所有API响应都应遵循以下基本结构，以确保客户端能够统一处理：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

-   `code` (`integer`): **业务状态码**，用于表示业务逻辑的处理结果。
-   `message` (`string`): 对操作结果的文本描述，便于调试和用户提示。
-   `data` (`object` | `array` | `null`): 实际返回的数据载荷。

### 2.2. 业务状态码 (`code`)

-   `200`: **请求成功** - 服务器已成功处理了请求。
-   `400`: **客户端请求错误** - 服务器无法理解请求，例如参数无效、格式错误等。
-   `401`: **未授权** - 请求需要用户认证。
-   `403`: **禁止访问** - 服务器理解请求，但拒绝执行。用户虽已认证但无权访问该资源。
-   `404`: **资源未找到** - 服务器找不到请求的资源。
-   `500`: **服务器内部错误** - 服务器在执行请求时发生了意外的错误。

### 2.3. 成功响应 (`code: 200`)

#### 2.3.1. 返回对象 (Object)

当查询单个资源或成功创建/修改资源时，`data` 字段应为一个**对象**。

**示例:**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
}
```

#### 2.3.2. 返回列表 (List/Array)

当查询非分页的资源列表时，`data` 字段应为一个**数组**。

**示例:**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    { "id": 1, "name": "John Doe" },
    { "id": 2, "name": "Jane Smith" }
  ]
}
```

#### 2.3.3. 返回分页数据 (Paginated List)

对于分页查询，`data` 字段应为一个包含**列表**和**分页信息**的对象。

**示例:**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "items": [
      { "id": 1, "name": "John Doe" },
      { "id": 2, "name": "Jane Smith" }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

#### 2.3.4. 返回树形数据 (Tree Data)

对于树形结构数据，`data` 字段应为一个**数组**，每个节点通过 `children` 字段嵌套子节点。

**示例:**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "父节点1",
      "children": [
        { "id": 3, "name": "子节点1.1", "children": [] }
      ]
    },
    {
      "id": 2,
      "name": "父节点2",
      "children": []
    }
  ]
}
```

#### 2.3.5. 操作成功 (无返回数据)

对于删除等不需要返回具体数据的操作，`data` 字段可以为 `null`。

**示例:**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```
#### 2.3.6. 返回json key值
- 所有接口返回key值将驼峰修改为：小写 使用_分割

### 2.4. 失败响应

当请求失败时，`data` 字段通常为 `null`，`message` 字段应包含清晰、具体的错误信息。

**示例 (404 Not Found):**
```json
{
  "code": 404,
  "message": "用户ID为 '123' 的资源未找到",
  "data": null
}
```

**示例 (400 Bad Request):**
```json
{
  "code": 400,
  "message": "无效的参数: 'email' 格式不正确",
  "data": null
}
```
