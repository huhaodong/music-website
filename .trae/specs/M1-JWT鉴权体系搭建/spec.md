# M1: 数据库演进与JWT鉴权体系 Spec

## Why
M1 需要实现数据库增量演进（新增 organization/role/permission 表）和 JWT 鉴权体系替换原有的 Session 认证，确保原有前端页面仍可通过 JWT 正常登录使用。

## What Changes
- 数据库增量迁移：consumer 表扩展字段、新增 organization/role/permission/user_role/role_permission 表
- 引入 Flyway 数据库版本管理
- 引入 Spring Security + jjwt 实现 JWT Token 认证
- 实现统一登录接口 `/auth/login`（合并 admin/consumer 登录）
- 实现 Token 生成/解析/刷新/黑名单功能
- 前端 Axios 拦截器适配 JWT Token 自动携带和刷新

## Impact
- Affected specs: 新增鉴权功能、数据库迁移功能
- Affected code: 后端 Security/Auth 模块、前端 request.ts、登录页面

## ADDED Requirements

### Requirement: JWT Token 管理
系统必须提供 JWT Token 生成、解析、刷新和黑名单功能

#### Scenario: 用户登录成功
- **WHEN** 用户使用正确凭证登录
- **THEN** 返回 accessToken 和 refreshToken，accessToken 有效期 2 小时

#### Scenario: Token 自动刷新
- **WHEN** accessToken 过期但 refreshToken 有效
- **THEN** 可通过 /auth/refresh 获取新的 accessToken

#### Scenario: Token 黑名单
- **WHEN** 用户登出
- **THEN** 当前 Token 加入黑名单，后续请求无法使用

### Requirement: 统一登录接口
系统必须提供统一的登录接口供所有用户使用

#### Scenario: 多角色统一登录
- **WHEN** admin 或 consumer 用户登录
- **THEN** 通过同一 `/auth/login` 接口获取 Token

### Requirement: 数据库迁移
数据库必须支持增量迁移，不破坏原有数据

#### Scenario: 增量迁移
- **WHEN** 执行 Flyway 迁移脚本
- **THEN** 新增表和字段，原有数据保持不变

## MODIFIED Requirements
无

## REMOVED Requirements
- 原有 Session-based 认证机制将被 JWT 替代

## Task Dependencies
- M1-01~M1-07 依赖 M0 完成
- M1-08~M1-17 依赖 M1-01~M1-07
- M1-18~M1-21 依赖 M1-14~M1-17
