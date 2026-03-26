# 后台管理页面显示问题修复 Spec

## Why
后台管理页面（用户管理、组织管理、角色管理、权限管理）无法正常显示内容，需要诊断并修复问题。

## What Changes
- 修复前端 API 路径与后端 Controller 路径不匹配问题
- 前端 `SystemManager` 中的管理接口需要添加 `/system` 前缀以匹配后端路由

## Impact
- 受影响的功能：用户管理、组织管理、角色管理、权限管理页面
- 受影响代码：`music-manage/src/api/index.ts`

## ADDED Requirements
### Requirement: API 路径匹配
系统管理 API 路径必须与后端 Controller 的 `@RequestMapping` 注解一致。

#### Scenario: 用户管理 API 调用
- **WHEN** 前端调用 `SystemManager.getAllUsers()`
- **THEN** 实际请求路径应为 `GET /system/user`
- **AND** 后端 `UserController` 应响应此请求

#### Scenario: 角色管理 API 调用
- **WHEN** 前端调用 `SystemManager.getAllRoles()`
- **THEN** 实际请求路径应为 `GET /system/role/list`
- **AND** 后端 `RoleController` 应响应此请求

#### Scenario: 组织管理 API 调用
- **WHEN** 前端调用 `SystemManager.getOrganizationTree()`
- **THEN** 实际请求路径应为 `GET /system/organization/tree`
- **AND** 后端 `OrganizationController` 应响应此请求

#### Scenario: 权限管理 API 调用
- **WHEN** 前端调用 `SystemManager.getPermissionTree()`
- **THEN** 实际请求路径应为 `GET /system/permission/tree`
- **AND** 后端 `PermissionController` 应响应此请求
