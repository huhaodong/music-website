# M2 里程碑修复任务清单

## 问题概述

根据 checklist 验证，M2 里程碑存在以下关键问题需要修复：
1. 所有后端测试文件缺失（13 个测试文件）
2. 数据库迁移文件 V5__init_permissions.sql 缺失
3. 前端测试配置错误（vitest.config.ts 缺少路径别名）
4. 前端测试文件缺失（PermissionManage.test.ts）
5. 测试覆盖率 0%，违反 TDD 流程

## 修复任务

### 阶段 1：基础设施修复（优先级：高）

- [ ] **FIX-01**: 修复 vitest.config.ts 配置
  - 添加路径别名 @ 指向 src 目录
  - 添加 path 模块引入
  - 验证配置后运行 npm run test

- [ ] **FIX-02**: 创建 V5__init_permissions.sql
  - 创建权限表（permission）
  - 创建角色表（role）
  - 创建角色权限关联表（role_permission）
  - 创建用户角色关联表（user_role）
  - 创建权限模板表（permission_template）
  - 初始化系统内置角色和数据

### 阶段 2：后端测试文件创建（优先级：高）

- [ ] **FIX-03**: RoleService 测试
  - 创建 RoleServiceTest.java
  - 测试用例：创建角色、更新角色、删除角色、查询角色
  - 测试用例：系统角色保护、角色权限分配
  - 目标覆盖率：≥80%

- [ ] **FIX-04**: RoleController 测试
  - 创建 RoleControllerTest.java
  - MockMvc 测试所有 REST API
  - 测试权限控制

- [ ] **FIX-05**: PermissionService 测试
  - 创建 PermissionServiceTest.java
  - 测试用例：权限树构建、角色权限查询、用户权限检查
  - 目标覆盖率：≥80%

- [ ] **FIX-06**: PermissionController 测试
  - 创建 PermissionControllerTest.java
  - MockMvc 测试所有 REST API

- [ ] **FIX-07**: PermissionTemplateService 测试
  - 创建 PermissionTemplateServiceTest.java
  - 测试用例：模板 CRUD、模板应用
  - 目标覆盖率：≥80%

- [ ] **FIX-08**: RequirePermission 注解测试
  - 创建 RequirePermissionAspectTest.java
  - 测试用例：有权限访问、无权限拒绝、超管跳过
  - 目标覆盖率：≥90%

- [ ] **FIX-09**: OrganizationService 测试
  - 创建 OrganizationServiceTest.java
  - 测试用例：组织 CRUD、组织树查询、path 生成
  - 测试用例：删除保护（有子组织/用户）
  - 目标覆盖率：≥80%

- [ ] **FIX-10**: OrganizationController 测试
  - 创建 OrganizationControllerTest.java
  - MockMvc 测试所有 REST API

- [ ] **FIX-11**: UserService 测试
  - 创建 UserServiceTest.java
  - 测试用例：用户 CRUD、角色分配、批量操作
  - 测试用例：数据权限、原有用户兼容
  - 目标覆盖率：≥80%

- [ ] **FIX-12**: UserController 测试
  - 创建 UserControllerTest.java
  - MockMvc 测试所有 REST API

- [ ] **FIX-13**: UserRoleService 测试
  - 创建 UserRoleServiceTest.java
  - 测试用例：用户角色分配、取消分配、查询
  - 目标覆盖率：≥80%

- [ ] **FIX-14**: DataPermissionInterceptor 测试
  - 创建 DataPermissionInterceptorTest.java
  - 测试用例：超管不限制、组织管理员限制、普通用户限制
  - 目标覆盖率：≥90%

### 阶段 3：前端测试修复（优先级：中）

- [ ] **FIX-15**: 修复前端测试运行
  - 验证 vitest.config.ts 修复后测试可运行
  - 修复 UserManage.test.ts 运行问题
  - 修复 OrgManage.test.ts 运行问题
  - 修复 RoleManage.test.ts 运行问题

- [ ] **FIX-16**: 创建 PermissionManage.test.ts
  - 创建权限管理页面测试
  - 测试用例：权限树展示、模板选择、权限保存
  - 目标覆盖率：≥70%

### 阶段 4：集成测试（优先级：中）

- [ ] **FIX-17**: RBAC 集成测试
  - 创建集成测试类
  - 测试场景：登录→获取权限→访问受控资源
  - 测试场景：角色分配→权限变更→访问控制
  - 测试场景：组织变更→数据权限变更

### 阶段 5：验证与文档（优先级：高）

- [ ] **FIX-18**: 运行完整测试套件
  - 运行 mvn test 验证所有后端测试通过
  - 运行 npm run test 验证所有前端测试通过
  - 生成 JaCoCo 覆盖率报告
  - 确保 Service 层覆盖率 ≥80%

- [ ] **FIX-19**: 更新 tasks.md 状态
  - 将所有任务标记为真实完成（有测试验证）
  - 更新任务描述反映实际实现

## 任务依赖

```
FIX-01 → FIX-15, FIX-16 (前端测试依赖配置修复)
FIX-02 → FIX-03~FIX-14 (测试依赖数据库表)
FIX-03~FIX-14 → FIX-18 (验证依赖所有测试文件)
FIX-15, FIX-16 → FIX-18 (验证依赖前端测试)
FIX-17 → FIX-18 (集成测试最后运行)
```

## 验收标准

1. ✅ 所有 13 个后端测试文件创建完成
2. ✅ V5__init_permissions.sql 创建完成
3. ✅ vitest.config.ts 配置修复
4. ✅ PermissionManage.test.ts 创建完成
5. ✅ mvn test 所有测试通过
6. ✅ npm run test 所有测试通过
7. ✅ JaCoCo 报告显示 Service 层覆盖率 ≥80%
8. ✅ checklist.md 所有检查点标记为 ✅
