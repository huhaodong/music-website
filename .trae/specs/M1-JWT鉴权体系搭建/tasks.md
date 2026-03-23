# M1: 数据库演进与JWT鉴权体系 - 任务列表

## 第一阶段：数据库增量迁移（Day 1-3）

- [x] M1-01: 设计增量迁移方案（不删原表，新增/扩展）
- [x] M1-02: 引入 Flyway 数据库版本管理
- [x] M1-03: 编写 V2__add_user_extended_fields.sql
- [x] M1-04: 编写 V3__create_organization_table.sql
- [x] M1-05: 编写 V4__create_role_permission_tables.sql
- [x] M1-06: 编写迁移验证测试（DatabaseMigrationTest.java）
- [x] M1-07: 初始化默认数据（超级管理员、默认角色）

## 第二阶段：JWT鉴权体系搭建（Day 3-6）

- [x] M1-08: 引入 Spring Security + jjwt 依赖（pom.xml）
- [x] M1-09: 编写 JwtTokenProvider（Token生成/解析/验证）
- [x] M1-10: 编写 JwtAuthenticationFilter（请求拦截）
- [x] M1-11: 配置 SecurityConfig（放行/拦截规则）
- [x] M1-12: 实现 Token 刷新机制
- [x] M1-13: 实现 Token 黑名单（Redis）

## 第三阶段：统一登录接口（Day 6-8）

- [x] M1-14: 编写 AuthController（登录/登出/刷新）
- [x] M1-15: 编写 AuthService（认证逻辑）
- [x] M1-16: 合并 admin/consumer 登录为统一入口
- [x] M1-17: 全局异常处理增强（401/403等）

## 第四阶段：前端适配（Day 8-10）

- [x] M1-18: Axios 请求拦截器增加 JWT Token 携带
- [x] M1-19: Axios 响应拦截器增加 Token 自动刷新
- [x] M1-20: 前台 music-client 登录页适配 JWT
- [x] M1-21: 后台 music-manage 登录页适配 JWT

# 任务依赖关系
- M1-02 依赖 M1-01
- M1-03~M1-07 依赖 M1-02
- M1-08~M1-13 依赖 M1-07
- M1-14~M1-17 依赖 M1-08~M1-13
- M1-18~M1-21 依赖 M1-14~M1-17
