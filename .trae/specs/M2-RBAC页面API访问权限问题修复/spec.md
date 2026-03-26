# RBAC 页面 API 访问权限问题修复 Spec

## Why
用户管理、组织管理、角色管理、权限管理页面访问时报错"获取用户列表失败"和"获取角色列表失败"。

**根本原因**：
1. 后端 `PermissionAspect` 检查发现用户未登录（session 中没有用户信息）
2. 部分请求 URL 包含 "//" 双斜杠被安全防火墙拦截

## What Changes
- 确保 RBAC 页面在用户登录后才能访问
- 修复前端 API 请求路径，避免 "//" 双斜杠问题
- 优化权限检查逻辑，提供更友好的错误提示

## Impact
- 受影响的功能：用户管理、组织管理、角色管理、权限管理页面的 API 访问
- 受影响代码：
  - `music-manage/src/api/index.ts` - API 路径配置
  - `music-manage/src/api/request.ts` - 请求配置
  - `music-server/src/main/java/com/example/yin/controller/*Controller.java` - 权限注解

## ADDED Requirements
### Requirement: 用户登录验证
RBAC 管理页面必须在用户登录后才能访问。

#### Scenario: 未登录用户访问 RBAC 页面
- **WHEN** 未登录用户访问 `/Home/user`
- **THEN** 应返回友好的错误提示"请先登录"
- **AND** 前端应显示登录提示或跳转到登录页

#### Scenario: 已登录用户访问 RBAC 页面
- **WHEN** 已登录用户访问 `/Home/user`
- **THEN** 应正常返回用户列表数据
- **AND** 前端应正常显示数据

## MODIFIED Requirements
### Requirement: API 路径修复
前端 API 请求路径不能包含 "//" 双斜杠。

- **检查**: 确保 `baseURL` 不以 "/" 结尾
- **检查**: 确保 API 路径不以 "/" 开头
