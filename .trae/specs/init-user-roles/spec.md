# 数据库用户角色初始化 Spec

## Why

当前数据库中 `consumer` 表的用户没有与 `role` 表建立关联，导致用户无法根据角色获取相应权限。需要为所有现有用户初始化角色关联。

## What Changes

- 为 `consumer` 表中的所有用户初始化 `user_role` 记录
- 管理员用户（admin）分配超级管理员角色 (role_id=1)
- 普通用户分配普通用户角色 (role_id=3)

## Impact

- Affected code: `user_role` 表数据
- 权限系统: 用户将根据角色获得相应权限

## ADDED Requirements

### Requirement: 用户角色初始化

系统 SHALL 为所有现有用户分配默认角色

#### Scenario: 管理员用户
- **WHEN** 用户类型为 `admin`
- **THEN** 分配角色 `SUPER_ADMIN` (role_id=1)

#### Scenario: 普通用户
- **WHEN** 用户类型为 `consumer`
- **THEN** 分配角色 `USER` (role_id=3)
