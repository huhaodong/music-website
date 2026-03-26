# M2: RBAC权限系统与组织用户管理 Spec

## Why
M2 需要实现完整的RBAC权限控制体系、组织结构管理和用户管理重构，将原有的 consumer + admin 双轨用户体系统一为单一 user 体系，并实现基于组织和角色的数据权限隔离。

## What Changes
- RBAC权限模型：角色/权限/模板的完整 CRUD 和分配机制
- 组织结构管理：树形组织架构的 CRUD
- 用户管理重构：consumer + admin 统一为 user 体系，支持角色分配
- 数据权限隔离：基于组织和角色的数据可见性控制
- 自定义 @RequirePermission 注解实现方法级权限控制
- 权限模板管理：支持权限批量分配的模板功能

**关键约束：权限系统必须向后兼容——未分配角色的用户默认拥有"普通用户"权限。**

## Impact
- Affected specs: 新增RBAC权限功能、组织管理功能、用户管理重构
- Affected code: 后端 RBAC/Organization/User 模块、前端 admin 管理页面

## ADDED Requirements

### Requirement: 角色管理 (Role)
系统必须提供角色的完整 CRUD 功能

#### Scenario: 创建角色
- **WHEN** 管理员创建新角色（名称、编码、描述）
- **THEN** 角色成功创建并返回角色ID

#### Scenario: 创建重复编码角色
- **WHEN** 创建角色时使用已存在的角色编码
- **THEN** 抛出重复异常，创建失败

#### Scenario: 删除有关联用户的角色
- **WHEN** 删除已被用户分配的角色
- **THEN** 拒绝删除，提示存在关联用户

#### Scenario: 删除系统内置角色
- **WHEN** 删除SUPER_ADMIN等系统内置角色
- **THEN** 拒绝删除，保护系统内置角色

### Requirement: 权限管理 (Permission)
系统必须提供权限树查询和角色权限分配功能

#### Scenario: 获取权限树
- **WHEN** 请求权限树结构
- **THEN** 返回完整的树形权限结构，包含菜单和按钮权限

#### Scenario: 获取角色权限
- **WHEN** 查询某角色的所有权限
- **THEN** 返回该角色关联的所有权限编码列表

#### Scenario: 检查用户权限
- **WHEN** 使用 hasPermission 检查用户是否有某权限
- **THEN** 有权限返回true，无权限返回false

### Requirement: 权限模板 (PermissionTemplate)
系统必须提供权限模板的CRUD和批量应用功能

#### Scenario: 创建权限模板
- **WHEN** 创建包含权限ID列表的模板
- **THEN** 模板成功保存，包含的权限ID以JSON形式存储

#### Scenario: 应用权限模板
- **WHEN** 为角色应用权限模板
- **THEN** 模板中的所有权限被批量分配给目标角色

### Requirement: @RequirePermission 注解
系统必须在方法级别实现权限控制

#### Scenario: 有权限用户访问
- **WHEN** 用户拥有访问方法所需的权限
- **THEN** 方法正常执行

#### Scenario: 无权限用户访问
- **WHEN** 用户不拥有访问方法所需的权限
- **THEN** 返回403 Forbidden

#### Scenario: 超级管理员访问
- **WHEN** 超级管理员访问任意方法
- **THEN** 跳过权限检查，方法正常执行

### Requirement: 组织管理 (Organization)
系统必须提供树形组织结构的CRUD

#### Scenario: 创建根组织
- **WHEN** 创建顶级组织
- **THEN** level=1，path=/id/

#### Scenario: 创建子组织
- **WHEN** 在某组织下创建子组织
- **THEN** 子组织level=父级+1，path=父级path + id/

#### Scenario: 删除有子组织的组织
- **WHEN** 删除存在子组织的组织
- **THEN** 拒绝删除，提示存在子组织

#### Scenario: 删除有关联用户的组织
- **WHEN** 删除已分配用户的组织
- **THEN** 拒绝删除，提示存在关联用户

#### Scenario: 移动组织
- **WHEN** 将组织移动到其他父组织下
- **THEN** path和level正确更新

### Requirement: 用户管理重构 (User)
系统必须统一 consumer 和 admin 为单一用户体系

#### Scenario: 用户关联默认角色
- **WHEN** 创建新用户时未指定角色
- **THEN** 自动分配"普通用户"角色

#### Scenario: 用户角色分配
- **WHEN** 为用户分配角色
- **THEN** 建立用户-角色关联关系

#### Scenario: 批量启用/禁用用户
- **WHEN** 勾选多个用户执行批量操作
- **THEN** 所有选中用户状态同时更新

#### Scenario: 按组织和角色筛选用户
- **WHEN** 查询用户列表时指定组织和角色条件
- **THEN** 只返回满足条件的用户分页数据

### Requirement: 数据权限隔离 (DataPermission)
系统必须基于用户角色实现数据可见性控制

#### Scenario: 超级管理员数据范围
- **WHEN** 超级管理员查询数据
- **THEN** 不限制数据范围，可查看所有数据

#### Scenario: 组织管理员数据范围
- **WHEN** 组织管理员查询数据
- **THEN** 只能查看本组织及下级组织的数据

#### Scenario: 普通用户数据范围
- **WHEN** 普通用户查询数据
- **THEN** 只能查看个人相关的数据

#### Scenario: 跨组织访问
- **WHEN** 用户尝试访问无权限的组织数据
- **THEN** 拒绝访问，返回无权限提示

## MODIFIED Requirements
- 原 consumer 表用户自动获得默认角色（向后兼容）
- 原 admin 表用户自动获得默认角色（向后兼容）

## REMOVED Requirements
无

## Task Dependencies
- M2-01~M2-08 依赖 M1 全部验收通过
- M2-09~M2-11 依赖 M2-01~M2-08
- M2-12~M2-16 依赖 M2-09~M2-11
- M2-17~M2-20 依赖 M2-12~M2-16
