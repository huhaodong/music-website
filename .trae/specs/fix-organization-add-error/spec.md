# 修复 Organization 添加失败问题 Spec

## Why
添加组织时返回 500 错误，日志显示 `Field 'code' doesn't have a default value`。数据库 organization 表中存在 code 字段，但实体类 Organization.java 中缺少该字段定义。

## What Changes
- 在 Organization.java 实体类中添加 code 字段
- 在 OrganizationRequest.java 请求类中添加 code 字段

## Impact
- Affected specs: 组织管理功能
- Affected code:
  - `music-server/src/main/java/com/example/yin/model/domain/Organization.java`
  - `music-server/src/main/java/com/example/yin/model/request/OrganizationRequest.java`

## ADDED Requirements
### Requirement: Organization 实体完整字段
系统应当在 Organization 实体类中包含数据库表的所有字段，确保 CRUD 操作正常。

#### Scenario: 添加组织
- **WHEN** 用户提交添加组织请求
- **THEN** 系统成功插入数据并返回成功响应

## MODIFIED Requirements
### Requirement: OrganizationRequest 包含 code 字段
OrganizationRequest 作为请求体需要包含所有可写入的字段。

## REMOVED Requirements
无

## 根因分析
1. 数据库 organization 表实际包含 `code` 字段（可能通过其他迁移添加）
2. 实体类 Organization.java 缺少 `code` 字段定义
3. MyBatis-Plus 自动插入时未指定 code 字段值
4. MySQL 因 code 字段无默认值而报错
