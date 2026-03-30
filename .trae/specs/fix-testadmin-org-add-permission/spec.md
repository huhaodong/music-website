# testadmin 用户添加组织权限问题修复 Spec

## Why

用户 testadmin 在使用组织管理功能时，添加组织操作失败，提示"权限不足"。需要排查并修复权限检查逻辑问题。

## What Changes

1. **检查 testadmin 用户的角色分配情况**
2. **检查 PermissionAspect 权限检查逻辑是否存在 bug**
3. **检查权限编码是否一致（org:add）**
4. **修复发现的问题**

## Impact

- 受影响的功能：组织管理 - 添加组织
- 受影响代码：
  - `music-server/src/main/java/com/example/yin/aspect/PermissionAspect.java` - 权限检查切面
  - `music-server/src/main/java/com/example/yin/controller/OrganizationController.java` - 组织控制器
  - 数据库 `user_role` 表 - 用户角色关联

## 问题分析

### 已知信息

1. **testadmin 用户信息**：
   - 用户 ID: 88
   - 用户类型: consumer（在 consumer 表中）
   - 用户状态: 正常（status=1）

2. **错误信息**：
   - `java.lang.RuntimeException: 权限不足`
   - 发生在 `PermissionAspect.checkPermission()`

3. **OrganizationController 配置**：
   - `addOrganization` 方法标注了 `@RequirePermission("org:add")`

### 可能的问题原因

1. testadmin 用户没有被分配包含 `org:add` 权限的角色
2. PermissionAspect 的 `getUserTypeFromAuthorities()` 方法硬编码返回 `"consumer"`，但实际用户类型判断逻辑可能有问题
3. 权限编码不一致（不同脚本定义的 org:add 权限代码不同）

## ADDED Requirements

### Requirement: testadmin 用户添加组织权限验证

系统应允许 testadmin 用户成功添加组织（如果该用户被分配了相应角色）。

#### Scenario: testadmin 有 org:add 权限
- **WHEN** testadmin 用户调用添加组织 API
- **THEN** 应返回成功结果

#### Scenario: testadmin 无 org:add 权限
- **WHEN** testadmin 用户调用添加组织 API
- **THEN** 应返回明确的错误信息"权限不足"

## MODIFIED Requirements

### Requirement: 权限检查逻辑修复

`PermissionAspect.getUserTypeFromAuthorities()` 方法应正确识别用户类型，而不是硬编码返回。

- **检查**: 验证该方法是否正确从 Spring Security Context 获取用户类型
- **检查**: 验证用户类型（consumer/admin）与 user_role 表中的 user_type 字段是否匹配
