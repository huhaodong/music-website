# M1: 数据库演进与JWT鉴权体系

> **里程碑编号**: M1
> **预计周期**: 第2-3周（10个工作日）
> **前置依赖**: M0 全部验收通过
> **核心目标**: 增量新建数据表，实现JWT认证替换Session，统一登录接口
> **开发分支**: `feature/auth-jwt`

---

## 1. 目标概述

M1 聚焦于两件事：
1. **数据库增量演进**：不破坏原有表结构，通过新增表和新增字段实现数据模型扩展
2. **JWT鉴权替换**：将原有的Session认证替换为JWT Token认证，实现统一登录入口

**关键约束：M1 结束时，原有的前端页面仍然可以正常使用（通过JWT登录）。**

---

## 2. 开发任务清单

### 2.1 数据库增量迁移（Day 1-3）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M1-01 | 设计增量迁移方案（不删原表，新增/扩展） | - | 迁移方案文档 | 0.5天 |
| M1-02 | 引入 Flyway 数据库版本管理 | - | Flyway配置 + 基线脚本 | 0.5天 |
| M1-03 | 编写 V2__add_user_extended_fields.sql | 先写验证SQL测试 | 迁移脚本 | 0.5天 |
| M1-04 | 编写 V3__create_organization_table.sql | 先写验证SQL测试 | 迁移脚本 | 0.25天 |
| M1-05 | 编写 V4__create_role_permission_tables.sql | 先写验证SQL测试 | 迁移脚本 | 0.25天 |
| M1-06 | 编写迁移验证测试（表结构、约束、索引） | RED→GREEN | MigrationTest.java | 0.5天 |
| M1-07 | 初始化默认数据（超级管理员、默认角色） | - | data.sql | 0.5天 |

#### 数据库变更详情

```sql
-- V2: 扩展 consumer 表（不重命名，保持兼容）
ALTER TABLE consumer ADD COLUMN org_id INT DEFAULT NULL;
ALTER TABLE consumer ADD COLUMN status TINYINT DEFAULT 1 COMMENT '1:启用 0:禁用';
ALTER TABLE consumer ADD COLUMN last_login_time DATETIME DEFAULT NULL;

-- V3: 新增组织表
CREATE TABLE organization (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    parent_id INT DEFAULT NULL,
    level INT DEFAULT 1,
    path VARCHAR(500) DEFAULT '/',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- V4: 新增角色权限表
CREATE TABLE role ( ... );
CREATE TABLE permission ( ... );
CREATE TABLE role_permission ( ... );
CREATE TABLE user_role ( ... );
```

### 2.2 JWT鉴权体系搭建（Day 3-6）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M1-08 | 引入 Spring Security + jjwt 依赖 | - | pom.xml 更新 | 0.25天 |
| M1-09 | 编写 JwtTokenProvider（Token生成/解析/验证） | RED→GREEN: JwtTokenProviderTest | JwtTokenProvider.java | 1天 |
| M1-10 | 编写 JwtAuthenticationFilter（请求拦截） | RED→GREEN: FilterTest | JwtAuthenticationFilter.java | 0.5天 |
| M1-11 | 配置 SecurityConfig（放行/拦截规则） | RED→GREEN: SecurityConfigTest | SecurityConfig.java | 0.5天 |
| M1-12 | 实现 Token 刷新机制 | RED→GREEN: TokenRefreshTest | RefreshToken逻辑 | 0.5天 |
| M1-13 | 实现 Token 黑名单（Redis） | RED→GREEN: BlacklistTest | TokenBlacklist逻辑 | 0.5天 |

### 2.3 统一登录接口（Day 6-8）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M1-14 | 编写 AuthController（登录/登出/刷新） | RED→GREEN: AuthControllerTest | AuthController.java | 1天 |
| M1-15 | 编写 AuthService（认证逻辑） | RED→GREEN: AuthServiceTest | AuthService.java | 0.5天 |
| M1-16 | 合并 admin/consumer 登录为统一入口 | RED→GREEN: 多角色登录测试 | 统一 /auth/login | 0.5天 |
| M1-17 | 全局异常处理增强（401/403等） | RED→GREEN: ExceptionHandlerTest | GlobalExceptionHandler改造 | 0.5天 |

### 2.4 前端适配（Day 8-10）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M1-18 | Axios 请求拦截器增加 JWT Token 携带 | 前端工具函数测试 | request.ts 改造 | 0.5天 |
| M1-19 | Axios 响应拦截器增加 Token 自动刷新 | 前端工具函数测试 | response interceptor | 0.5天 |
| M1-20 | 前台 music-client 登录页适配 JWT | 组件测试 | Login.vue 改造 | 0.5天 |
| M1-21 | 后台 music-manage 登录页适配 JWT | 组件测试 | Login.vue 改造 | 0.5天 |

---

## 3. TDD 测试用例规划

### 3.1 后端单元测试

```
test/
├── auth/
│   ├── JwtTokenProviderTest.java
│   │   ├── 生成Token_应包含正确的用户信息
│   │   ├── 生成Token_应设置正确的过期时间
│   │   ├── 解析有效Token_应返回用户信息
│   │   ├── 解析过期Token_应抛出ExpiredTokenException
│   │   ├── 解析篡改Token_应抛出InvalidTokenException
│   │   └── 刷新Token_应返回新的AccessToken
│   ├── AuthServiceTest.java
│   │   ├── 正确凭证登录_应返回Token对
│   │   ├── 错误密码登录_应抛出AuthenticationException
│   │   ├── 不存在用户登录_应抛出UserNotFoundException
│   │   ├── 已禁用用户登录_应抛出AccountDisabledException
│   │   ├── 登出_应将Token加入黑名单
│   │   └── 管理员登录_应携带管理员角色信息
│   ├── AuthControllerTest.java (MockMvc)
│   │   ├── POST /auth/login 正确凭证_返回200和Token
│   │   ├── POST /auth/login 错误凭证_返回401
│   │   ├── POST /auth/logout 有效Token_返回200
│   │   ├── POST /auth/refresh 有效RefreshToken_返回新Token
│   │   ├── POST /auth/refresh 过期RefreshToken_返回401
│   │   └── GET /auth/info 有效Token_返回用户信息
│   └── JwtAuthenticationFilterTest.java
│       ├── 请求携带有效Token_应通过认证
│       ├── 请求携带过期Token_应返回401
│       ├── 请求无Token_访问受保护资源_应返回401
│       ├── 请求无Token_访问公开资源_应通过
│       └── 请求携带黑名单Token_应返回401
├── migration/
│   └── DatabaseMigrationTest.java
│       ├── 迁移后_consumer表应包含新字段
│       ├── 迁移后_organization表应存在
│       ├── 迁移后_role表应存在
│       ├── 迁移后_permission表应存在
│       └── 迁移后_默认管理员角色应存在
```

### 3.2 前端测试

```
tests/
├── utils/
│   └── auth.test.ts
│       ├── getToken 无Token时_应返回null
│       ├── setToken 应正确存储Token
│       ├── removeToken 应清除Token
│       ├── isTokenExpired 未过期Token_应返回false
│       └── isTokenExpired 已过期Token_应返回true
├── interceptors/
│   └── request.test.ts
│       ├── 请求拦截器_有Token时应添加Authorization头
│       ├── 请求拦截器_无Token时不添加Authorization头
│       └── 响应拦截器_收到401时应尝试刷新Token
```

---

## 4. 测试验收清单

### 4.1 数据库迁移验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M1-V01 | Flyway 迁移成功 | 所有迁移脚本按序执行无错误 | `flyway info` 查看状态 | ☐ |
| M1-V02 | consumer 表扩展成功 | 新增字段存在且有默认值 | `DESC consumer` | ☐ |
| M1-V03 | organization 表创建成功 | 表结构正确 | `DESC organization` | ☐ |
| M1-V04 | role 表创建成功 | 表结构正确 | `DESC role` | ☐ |
| M1-V05 | permission 表创建成功 | 表结构正确 | `DESC permission` | ☐ |
| M1-V06 | 默认数据初始化成功 | 超级管理员和默认角色存在 | `SELECT` 验证 | ☐ |
| M1-V07 | 原有数据完整 | 原 consumer/song/singer 数据未丢失 | 数据量对比 | ☐ |
| M1-V08 | 数据库迁移测试通过 | DatabaseMigrationTest 全通过 | `mvn test` | ☐ |

### 4.2 JWT鉴权验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M1-V09 | Token 生成正确 | JWT 包含 userId/username/roles，2h 过期 | 单元测试 + jwt.io 解析 | ☐ |
| M1-V10 | Token 解析正确 | 有效 Token 可正确提取用户信息 | JwtTokenProviderTest | ☐ |
| M1-V11 | 过期 Token 拒绝 | 过期 Token 返回 401 | 等待 Token 过期后请求 | ☐ |
| M1-V12 | 篡改 Token 拒绝 | 修改 Token 内容后请求返回 401 | 手动篡改 Token 测试 | ☐ |
| M1-V13 | Token 刷新可用 | Refresh Token 可获取新 Access Token | POST /auth/refresh | ☐ |
| M1-V14 | Token 黑名单生效 | 登出后的 Token 无法使用 | 登出后用旧 Token 请求 | ☐ |

### 4.3 统一登录验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M1-V15 | 统一登录接口可用 | POST /auth/login 正常返回 Token | Postman/curl 测试 | ☐ |
| M1-V16 | 原管理员可登录 | 使用原 admin 凭证通过统一接口登录 | 登录验证 | ☐ |
| M1-V17 | 原用户可登录 | 使用原 consumer 凭证通过统一接口登录 | 登录验证 | ☐ |
| M1-V18 | 登出接口可用 | POST /auth/logout 正常登出 | 登出后验证 Token 失效 | ☐ |
| M1-V19 | 用户信息接口可用 | GET /auth/info 返回当前用户信息和角色 | 携带 Token 请求 | ☐ |
| M1-V20 | 全局异常处理正确 | 401/403 返回标准错误格式 | 各异常场景验证 | ☐ |

### 4.4 前端适配验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M1-V21 | 前台登录正常 | music-client 使用 JWT 登录成功 | 浏览器登录 | ☐ |
| M1-V22 | 后台登录正常 | music-manage 使用 JWT 登录成功 | 浏览器登录 | ☐ |
| M1-V23 | Token 自动携带 | 登录后请求自动携带 Authorization 头 | 浏览器 DevTools 查看 | ☐ |
| M1-V24 | Token 自动刷新 | Access Token 过期时自动刷新 | 等待过期后操作 | ☐ |
| M1-V25 | 原有功能不受影响 | 登录后歌曲播放/后台管理等功能正常 | 冒烟测试 | ☐ |

### 4.5 测试覆盖率验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M1-V26 | 后端鉴权模块测试通过 | 所有 Auth 相关测试绿灯 | `mvn test` | ☐ |
| M1-V27 | 后端鉴权模块覆盖率 | AuthService/JwtTokenProvider ≥ 80% | JaCoCo 报告 | ☐ |
| M1-V28 | 前端 auth 工具测试通过 | Token 管理工具函数测试绿灯 | `npm run test` | ☐ |

---

## 5. 里程碑完成标志

**M1 完成的充要条件：**

- [ ] 数据库增量迁移全部成功，原有数据完整
- [ ] JWT Token 生成/解析/刷新/黑名单功能正常
- [ ] 统一登录接口 `/auth/login` 可用
- [ ] 前台 + 后台 均可通过 JWT 正常登录和使用
- [ ] 原有功能（播放/管理）不受影响
- [ ] 所有后端鉴权测试通过，覆盖率 ≥ 80%
- [ ] 所有前端 auth 测试通过

**进入 M2 的前提：M1 全部验收项通过。**
