# RBAC管理页面API接口测试与修复 Spec

## Why
日志显示用户管理、组织管理、角色管理、权限管理页面切换时出现数据库表不存在的错误：
- `Table 'tp_music.user_role' doesn't exist`
- `Table 'tp_music.role' doesn't exist`
- `Table 'tp_music.organization' doesn't exist`

这些表由Flyway迁移脚本定义，但未在数据库中创建。

## What Changes
- 编写API测试代码，测试用户管理、组织管理、角色管理、权限管理相关接口
- 修复数据库迁移问题，确保RBAC相关表正确创建
- 修复前端API调用问题

## Impact
- 受影响前端页面：`UserManage.vue`, `RoleManage.vue`, `PermissionManage.vue`, `OrganizationManage.vue`
- 受影响后端接口：SystemManager API (`system/user/*`, `system/role/*`, `system/permission/*`, `system/organization/*`)
- 受影响数据库表：user_role, role, permission, role_permission, organization

## ADDED Requirements

### Requirement: 数据库迁移配置
系统 SHALL 使用Flyway自动执行数据库迁移脚本，确保所有RBAC表正确创建。

#### Scenario: Flyway迁移执行
- **WHEN** Spring Boot应用启动
- **THEN** Flyway应自动执行V1-V7所有迁移脚本
- **AND** 创建role, permission, user_role, role_permission, organization表

### Requirement: API接口测试
系统 SHALL 提供对以下API端点的测试：
- `GET /system/user/list` - 获取用户列表
- `GET /system/role/list` - 获取角色列表
- `GET /system/permission/list` - 获取权限列表
- `GET /system/organization/list` - 获取组织列表

#### Scenario: 用户管理API测试
- **WHEN** 调用 `GET /system/user/list`
- **THEN** 应返回用户列表，状态码200

#### Scenario: 角色管理API测试
- **WHEN** 调用 `GET /system/role/list`
- **THEN** 应返回角色列表，状态码200

#### Scenario: 权限管理API测试
- **WHEN** 调用 `GET /system/permission/list`
- **THEN** 应返回权限列表，状态码200

#### Scenario: 组织管理API测试
- **WHEN** 调用 `GET /system/organization/list`
- **THEN** 应返回组织列表，状态码200

## MODIFIED Requirements

### Requirement: 前端API路径修正
前端 `SystemManager.getAllUsers()` 原来调用 `system/user`，应修正为 `system/user/list`

### Requirement: 前端API路径修正
前端 `SystemManager.getAllRoles()` 原来调用 `system/role/list`，确认路径正确

### Requirement: 前端API路径修正
前端 `SystemManager.getAllPermissions()` 原来调用 `system/permission/list`，确认路径正确

### Requirement: 前端API路径修正
前端 `SystemManager.getAllOrganizations()` 原来调用 `system/organization/list`，确认路径正确

## REMOVED Requirements
无
