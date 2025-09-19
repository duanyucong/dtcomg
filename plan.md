# 权限管理系统开发计划

本项目旨在 `dtcomg-system` 模块中构建一套完整的用户认证与权限管理（RBAC）系统。

**技术栈:**
- Spring Boot
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Springdoc OpenAPI

**核心实体:**
- 用户 (User)
- 角色 (Role)
- 权限 (Permission/Menu)
- 部门 (Department)

---

## 开发阶段

### Phase 1: 数据库设计

根据需求，设计以下数据表：

1.  **`sys_user` (用户表)**
    -   `user_id` (主键), `dept_id`, `user_name`, `nick_name`, `password`, `email`, `phone_number`, `status` (启用/禁用), `create_time`, `update_time`, etc.
2.  **`sys_role` (角色表)**
    -   `role_id` (主键), `role_name`, `role_key` (权限字符), `status`, `create_time`, `update_time`, etc.
3.  **`sys_menu` (菜单/权限表)**
    -   `menu_id` (主键), `parent_id`, `menu_name`, `path` (路由地址), `component` (组件路径), `perms` (权限标识), `menu_type` (M-目录, C-菜单, F-按钮), `icon`, `order_num`, `status`, `create_time`, `update_time`, etc.
4.  **`sys_dept` (部门表)**
    -   `dept_id` (主键), `parent_id`, `dept_name`, `order_num`, `leader`, `status`, `create_time`, `update_time`, etc.
5.  **`sys_user_role` (用户角色关联表)**
    -   `user_id`, `role_id`
6.  **`sys_role_menu` (角色菜单关联表)**
    -   `role_id`, `menu_id`
7.  **`sys_role_dept` (角色部门关联表)**
    -   `role_id`, `dept_id` (用于数据权限)

### Phase 2: 项目配置

1.  **添加依赖:** 在 `dtcomg-system/pom.xml` 中添加 `spring-boot-starter-security`, `mybatis-plus-boot-starter`, `mysql-connector-java`, `jjwt` (for JWT), `springdoc-openapi-starter-webmvc-ui`。
2.  **配置 application.yml:**
    -   配置数据库连接信息 (DataSource)。
    -   配置 MyBatis-Plus (e.g., mapper-locations)。
    -   配置 Jackson 全局属性，将JSON输出key转换为 `snake_case`。
    -   `spring.jackson.property-naming-strategy=SNAKE_CASE`
3.  **创建通用返回对象:** 创建 `R` 类或 `ApiResult` 类，严格遵循 `Interface Specification.md` 中定义的 `{code, message, data}` 结构。
4.  **创建分页对象:** 创建 `PageResult` 类，用于封装分页数据，遵循 `{items, total, page, pageSize}` 结构。

### Phase 3: 领域模型与Mapper层

1.  **创建包结构:**
    -   `com.dtcomg.system.domain` (实体类)
    -   `com.dtcomg.system.mapper` (MyBatis Mapper接口)
2.  **创建实体类 (PO):** 根据 Phase 1 的表设计，创建 `SysUser`, `SysRole`, `SysMenu`, `SysDept` 等实体类。
3.  **创建 Mapper 接口:** 创建 `SysUserMapper`, `SysRoleMapper`, 等接口，继承自 MyBatis-Plus 的 `BaseMapper`。

### Phase 4: Service层

1.  **创建包结构:**
    -   `com.dtcomg.system.service` (Service接口)
    -   `com.dtcomg.system.service.impl` (Service实现)
2.  **创建 Service:** 创建 `ISysUserService`, `ISysRoleService` 等接口及其实现类。
3.  **实现核心业务逻辑:**
    -   用户、角色、菜单、部门的CRUD操作。
    -   用户分配角色。
    -   角色分配菜单权限。

### Phase 5: Spring Security 与 JWT 集成

1.  **创建 `security` 包:** `com.dtcomg.system.security`
2.  **实现 `UserDetailsServiceImpl`:** 实现 `UserDetailsService` 接口，重写 `loadUserByUsername` 方法，用于从数据库加载用户信息。
3.  **创建 `JwtAuthenticationTokenFilter`:** 创建JWT过滤器，用于在每个请求中解析Token，验证用户身份并将用户信息存入 `SecurityContextHolder`。
4.  **创建 `JwtUtils`:** 工具类，用于生成、解析和验证JWT。
5.  **配置 `SecurityConfig`:**
    -   使用 Component-based a pproach 配置 `SecurityFilterChain`。
    -   配置 `PasswordEncoder` (使用 `BCryptPasswordEncoder`)。
    -   配置认证入口点 `AuthenticationEntryPoint` (处理未登录访问)。
    -   配置访问拒绝处理器 `AccessDeniedHandler` (处理无权限访问)。
    -   配置HTTP security rules，放行登录等匿名接口，保护其他接口。
    -   将 `JwtAuthenticationTokenFilter` 添加到过滤器链中。

### Phase 6: Controller (API) 层

1.  **创建 `controller` 包:** `com.dtcomg.system.controller`
2.  **创建 `BaseController`:** 创建一个基础控制器，提供获取分页对象等通用方法。
3.  **创建 `SysLoginController`:**
    -   `POST /api/login`: 用户登录，成功后返回JWT。
    -   `GET /api/getInfo`: 获取当前登录用户的详细信息和权限列表。
    -   `POST /api/logout`: 用户登出。
4.  **创建实体管理Controller:**
    -   `SysUserController`, `SysRoleController`, `SysMenuController`, `SysDeptController`。
    -   严格按照 `Interface Specification.md` 和 RESTful 风格实现增、删、改、查（列表/分页）、详情接口。
    -   使用 `@PreAuthorize` 注解配合权限标识（如 `@PreAuthorize("@ss.hasPermi('system:user:list')")`）进行接口级别的权限控制。

### Phase 7: 全局配置

1.  **全局异常处理:** 创建 `@RestControllerAdvice`，统一处理业务异常、参数校验异常等，返回规范的失败响应。
2.  **数据权限:** (可选高级功能) 使用AOP + 自定义注解实现基于部门的数据权限过滤。
3.  **API文档:** 使用 `@Operation` 等注解为Controller接口添加 OpenAPI/Swagger 文档说明。

---
