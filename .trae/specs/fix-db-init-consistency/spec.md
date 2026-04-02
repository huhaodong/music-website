# 项目数据库初始化一致性修复

## Why
项目使用两套数据库初始化配置（MySQL+Flyway 和 H2+SQL），但两者的数据不一致，导致使用 git 迁移项目后，使用 H2 数据库时角色权限数据缺失。

## What Changes
- 修复 `data-h2.sql` 中的角色和权限数据，使其与 Flyway 迁移脚本 `V5__init_default_data.sql` 一致
- 修复 `schema-h2.sql` 中缺失的字段（与 MySQL schema 不一致）
- 统一 user_role 表的数据初始化逻辑

## Impact
- Affected specs: launch-project
- Affected code:
  - `music-server/src/main/resources/schema-h2.sql`
  - `music-server/src/main/resources/data-h2.sql`

## 当前问题分析

### schema-h2.sql 问题
1. `consumer` 表缺少 `status`, `org_id`, `last_login_time` 字段（与 MySQL V2 迁移不一致）
2. `user_role` 表结构与 V4 迁移脚本不完全一致

### data-h2.sql 问题
1. 角色 ID 不一致：
   - H2: id=3 是 USER (code='USER')
   - MySQL V5: id=2 是 USER (code='USER')
2. 权限数据不一致：
   - H2 有 38 个权限
   - MySQL V5 只有部分权限
3. `user_role` 数据缺少 SINGER 角色

### 根本原因
- `spring.flyway.enabled=false` - Flyway 被禁用
- `spring.sql.init.mode=always` - 使用 H2 SQL 初始化
- 但 H2 SQL 与 MySQL Flyway 脚本数据不一致

## ADDED Requirements

### Requirement: H2 数据库初始化数据完整性
H2 初始化脚本必须包含与 MySQL Flyway 迁移相同的基础数据

#### Scenario: 角色初始化
- **WHEN** 应用使用 H2 数据库启动
- **THEN** role 表包含: SUPER_ADMIN(id=1), USER(id=2), SINGER(id=3)
- **AND** permission 表包含歌曲、歌手、歌单、评论相关权限
- **AND** role_permission 表正确关联角色和权限

#### Scenario: 用户角色初始化
- **WHEN** 应用使用 H2 数据库启动
- **THEN** 所有 consumer 用户都有对应的 user_role 记录
- **AND** 测试用户分配了 USER 角色

## MODIFIED Requirements

### Requirement: schema-h2.sql 完整性
修复 `schema-h2.sql` 中的 consumer 表结构

## REMOVED Requirements
无

## Task Dependencies
无前置依赖
