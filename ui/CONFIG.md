# 前端配置说明

## 环境配置

### 开发环境
默认使用本地后端接口：http://127.0.0.1:8080

### 生产环境
默认使用远程后端接口：http://117.72.145.103:8080

## 配置文件

### 1. 环境变量文件

#### 开发环境 (.env)
```
VITE_API_BASE_URL=http://127.0.0.1:8080
```

#### 生产环境 (.env.production)
```
VITE_API_BASE_URL=http://117.72.145.103:8080
```

### 2. 自定义配置

如果需要自定义API地址，可以创建以下文件：

- `.env.local` - 本地开发环境自定义配置
- `.env.production.local` - 生产环境自定义配置

### 3. 环境变量优先级

1. `.env.local` (最高优先级)
2. `.env.[mode].local`
3. `.env.[mode]`
4. `.env` (最低优先级)

## 使用说明

### 开发模式
```bash
# 使用本地接口
pnpm dev

# 或指定端口
pnpm dev --port 8081
```

### 生产模式
```bash
# 构建生产版本
pnpm build

# 预览生产版本
pnpm preview
```

### 切换接口地址

#### 方法1：修改环境变量文件
直接修改 `.env` 或 `.env.production` 文件中的 `VITE_API_BASE_URL`

#### 方法2：创建本地配置文件
创建 `.env.local` 文件并设置：
```
VITE_API_BASE_URL=http://your-custom-api.com:8080
```

#### 方法3：临时修改
在运行时设置环境变量：
```bash
# Windows
set VITE_API_BASE_URL=http://your-api.com:8080
pnpm dev

# Linux/Mac
VITE_API_BASE_URL=http://your-api.com:8080 pnpm dev
```

## 注意事项

1. 所有环境变量文件已添加到 `.gitignore`，不会被提交到版本控制
2. 生产环境请确保使用正确的远程接口地址
3. 开发环境默认使用本地接口，确保后端服务已启动