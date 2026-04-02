# 强制默认用户角色 Spec

## Why
目前系统中新注册用户可能没有关联任何角色信息，这会导致 RBAC 权限校验失效或用户无法正常使用受限功能。为了保证系统的安全性与规范性，必须确保每个用户至少拥有一个角色。

## What Changes
- 修改用户注册/新增逻辑，确保新创建的 `consumer` 用户默认关联“普通用户”角色（若未指定角色）。
- **BREAKING**: 注册接口将强制执行角色关联逻辑，若“普通用户”角色不存在，将抛出异常。
- 增加数据修复脚本或逻辑，为当前存量且无角色的用户统一分配“普通用户”角色。

## Impact
- Affected specs: 用户注册流程、RBAC 权限校验
- Affected code: `UserServiceImpl`, `UserController`, `UserRoleMapper`, 数据库迁移脚本

## ADDED Requirements
### Requirement: 强制默认角色
系统 SHALL 在用户创建时（包括前台注册与后台新增），如果请求未显式提供角色信息，则自动分配 code 为 `USER` 的“普通用户”角色。

#### Scenario: 注册时自动分配角色
- **WHEN** 用户通过注册接口创建账号
- **THEN** 系统在 `user_role` 表中自动插入一条该用户与 `USER` 角色的关联记录

#### Scenario: 存量无角色用户修复
- **WHEN** 执行修复脚本或系统启动初始化
- **THEN** 所有在 `user_role` 中无记录的用户被统一分配 `USER` 角色

## MODIFIED Requirements
### Requirement: 用户创建原子性
[用户创建操作必须与角色分配操作处于同一事务中，确保不会出现创建了用户但角色分配失败的情况]

## REMOVED Requirements
### Requirement: 允许无角色用户
**Reason**: 违反 RBAC 安全规范，导致权限管理混乱。
**Migration**: 所有新旧用户均需通过 `user_role` 表关联至少一个角色。
