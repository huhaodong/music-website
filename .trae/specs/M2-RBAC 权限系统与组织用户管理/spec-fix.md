# M2 里程碑修复 Spec

## Why

M2 里程碑（RBAC 权限系统与组织用户管理）的代码实现已完成，但严重违反 TDD 流程：
- 所有后端测试文件缺失（13 个测试文件）
- 数据库迁移文件 V5__init_permissions.sql 缺失
- 前端测试配置错误导致测试无法运行
- 测试覆盖率 0%，无法验证功能正确性

根据项目规则（project_rules.md），必须遵循 TDD 流程并确保测试覆盖率 ≥60%。

## What Changes

### 新增内容
- **后端测试文件**（13 个）：
  - RoleServiceTest, RoleControllerTest
  - PermissionServiceTest, PermissionControllerTest
  - PermissionTemplateServiceTest
  - RequirePermissionAspectTest
  - OrganizationServiceTest, OrganizationControllerTest
  - UserServiceTest, UserControllerTest
  - UserRoleServiceTest
  - DataPermissionInterceptorTest
  
- **数据库迁移文件**：
  - V5__init_permissions.sql（权限表、角色表、关联表、模板表）

- **前端测试文件**：
  - PermissionManage.test.ts
  - vitest.config.ts 配置修复

- **集成测试**：
  - RBAC 集成测试类

### 修改内容
- **vitest.config.ts**：添加路径别名配置
- **tasks.md**：更新任务状态为真实完成

## Impact

### 受影响的规范
- M2 里程碑验收标准（checklist.md 所有检查点）
- TDD 开发流程合规性
- 测试覆盖率要求（Service 层 ≥80%）

### 受影响的代码
- **测试目录**：`music-server/src/test/java/com/example/yin/`
- **SQL 目录**：`music-server/sql/`
- **前端测试目录**：`music-manage/tests/`
- **配置文件**：`music-manage/vitest.config.ts`

## ADDED Requirements

### Requirement 1: 后端单元测试
系统 SHALL 为所有 RBAC 相关 Service 和 Controller 提供完整的单元测试：
- Service 层测试覆盖率 ≥80%
- Controller 层使用 MockMvc 测试所有 REST API
- 数据权限拦截器测试覆盖率 ≥90%

#### Scenario: RoleService 测试
- **WHEN** 执行 RoleServiceTest
- **THEN** 测试创建、更新、删除、查询角色功能
- **THEN** 验证系统角色保护逻辑
- **THEN** 测试覆盖率达到 ≥80%

### Requirement 2: 数据库迁移
系统 SHALL 提供 V5__init_permissions.sql 初始化脚本：
- 创建 permission 表（权限定义）
- 创建 role 表（角色定义）
- 创建 role_permission 表（角色权限关联）
- 创建 user_role 表（用户角色关联）
- 创建 permission_template 表（权限模板）
- 初始化系统内置角色（超级管理员、组织管理员、普通用户）

### Requirement 3: 前端测试配置
系统 SHALL 正确配置 vitest 测试环境：
- 配置路径别名 @ 指向 src 目录
- 所有前端组件测试可正常运行
- 测试覆盖率 ≥70%

### Requirement 4: 集成测试
系统 SHALL 提供 RBAC 集成测试：
- 测试登录→获取权限→访问受控资源全链路
- 测试角色分配→权限变更→访问控制
- 测试组织变更→数据权限变更

## MODIFIED Requirements

### Requirement: M2 任务完成标志
**原要求**：代码实现完成即标记为完成  
**修改后**：代码实现 + 完整测试覆盖 + 测试通过 + 覆盖率达标 才标记为完成

## REMOVED Requirements

无

## Migration

### 测试执行
1. 后端：`cd music-server && mvn test`
2. 前端：`cd music-manage && npm run test`
3. 覆盖率：`mvn test jacoco:report`

### 数据库迁移
1. 执行 V5__init_permissions.sql
2. 验证表结构和初始数据
