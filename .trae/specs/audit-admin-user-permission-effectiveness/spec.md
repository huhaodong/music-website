# 管理员用户新增权限审计与修复 Spec

## Why
超级管理员账号 `admin` 在执行“添加用户”时出现“权限不足”，说明权限分配、权限编码或权限生效链路存在不一致。该问题会阻断核心后台管理能力，并影响权限管理模块可信度。

## What Changes
- 审计 `admin` 账号的角色分配与权限分配全链路（`user_role`、`role_permission`、`permission`）。
- 审计用户管理接口权限注解与数据库权限编码的一致性。
- 审计权限切面在 `admin/consumer` 两类用户上的识别与权限聚合行为。
- 增加权限模块测试代码（正向/反向/生效性回归），覆盖“添加用户”及关联高风险接口。
- 修复权限赋予不生效问题，并提供可重复验证步骤。
- **BREAKING**：若发现历史权限编码冲突，统一到单一主编码并保留兼容映射，可能影响旧脚本或旧角色配置。

## Impact
- Affected specs: RBAC 权限校验、角色权限分配、用户管理接口鉴权、生效性回归测试
- Affected code: `music-server` 的 `PermissionAspect`、`UserController`、权限/角色相关 service&mapper、SQL 初始化脚本、权限测试代码

## ADDED Requirements
### Requirement: 超级管理员添加用户权限必须生效
系统 SHALL 在超级管理员拥有目标权限时允许调用“添加用户”接口，并在缺失权限时返回明确拒绝结果。

#### Scenario: 有权限成功添加用户
- **WHEN** `admin` 账号拥有用户新增所需权限并调用 `/system/user/add`
- **THEN** 接口返回成功且用户被创建

#### Scenario: 无权限被拒绝
- **WHEN** 账号缺失用户新增所需权限并调用 `/system/user/add`
- **THEN** 接口返回权限不足（非成功状态）且不会创建用户

### Requirement: 权限赋予操作必须可验证生效
系统 SHALL 能证明“在权限管理模块中赋权后”目标账号获得对应接口访问能力。

#### Scenario: 赋权后生效
- **WHEN** 通过角色权限关系为目标账号新增某权限并重新鉴权
- **THEN** 目标账号访问对应受保护接口由拒绝变为通过

## MODIFIED Requirements
### Requirement: 权限编码一致性
系统中控制器注解权限码与数据库 `permission.code` 必须保持可追溯一致；当存在历史编码（如旧码/新码）时，必须定义兼容策略并由测试覆盖。

## REMOVED Requirements
### Requirement: 无
**Reason**: 本变更聚焦审计、修复与验证，不移除既有业务能力。  
**Migration**: 不适用。
