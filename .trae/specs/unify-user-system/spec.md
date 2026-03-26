# 用户体系整合 Spec

## Why

当前系统存在两套用户体系：
- `admin` 表：后台管理用户
- `consumer` 表：前台曲库用户

两套体系独立登录，管理员无法通过角色权限控制是否允许登录后台。需要整合为单一用户表，通过角色/权限控制后台访问。

## What Changes

### 核心变更

1. **用户表合并** - 将 `admin` 表数据迁移到 `consumer` 表
2. **登录入口统一** - 前台和后台使用同一登录接口，通过 `userType` 区分
3. **后台访问权限控制** - 新增 `system:admin:login` 权限控制后台登录

### 后台登录权限

| 权限编码 | 权限名称 | 说明 |
|----------|----------|------|
| system:admin:login | 后台登录权限 | 允许登录后台管理系统 |

拥有此权限的用户可以登录后台管理系统，不拥有此权限的用户只能登录前台曲库。

## Impact

- Affected specs: M1-JWT鉴权体系搭建, M2-RBAC权限系统与组织用户管理
- Affected code:
  - 数据库 `admin` 表 → 迁移到 `consumer` 表
  - `AuthController` - 统一登录入口
  - `ConsumerServiceImpl` - 合并后的用户服务
  - 权限表 - 新增 `system:admin:login` 权限

## ADDED Requirements

### Requirement: 后台登录权限控制

系统 SHALL 通过 `system:admin:login` 权限控制后台管理系统访问

#### Scenario: 用户登录后台
- **WHEN** 用户尝试登录后台管理系统
- **THEN** 系统验证用户是否拥有 `system:admin:login` 权限
- **IF** 拥有权限 **THEN** 允许登录
- **IF** 不拥有权限 **THEN** 拒绝登录并提示"无后台访问权限"

#### Scenario: 权限检查节点
- **WHEN** 用户登录请求到达 `/auth/login` 且 `userType=admin`
- **THEN** 检查用户是否拥有 `system:admin:login` 权限

## MODIFIED Requirements

### Requirement: 统一登录入口

原 `admin` 表用户和 `consumer` 表用户统一使用 `/auth/login` 接口

#### Scenario: 前台用户登录
- **WHEN** 调用 `/auth/login` 且 `userType=consumer`
- **THEN** 使用 `consumer` 表验证

#### Scenario: 后台用户登录
- **WHEN** 调用 `/auth/login` 且 `userType=admin`
- **THEN** 使用 `consumer` 表验证，并检查 `system:admin:login` 权限

## 数据库变更

### 迁移步骤
1. 将 `admin` 表数据迁移到 `consumer` 表
2. 为原管理员用户分配 `system:admin:login` 权限
3. 保留 `admin` 表作为备份（可后续删除）

### 新增权限
```sql
INSERT INTO permission (name, code, type, sort, status) VALUES
('后台登录权限', 'system:admin:login', 1, 100, 1);
```
