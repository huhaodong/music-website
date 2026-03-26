# M2-RBAC权限系统与组织用户管理 Tasks

## 1. 角色权限后端（Day 1-4）

- [x] **M2-01**: 权限树数据初始化脚本
  - 编写 V5__init_permissions.sql 初始化脚本
  - 先写验证测试确保脚本正确

- [x] **M2-02**: RoleService（角色CRUD）
  - 实现角色创建、查询、更新、删除
  - TDD流程: RoleServiceTest

- [x] **M2-03**: RoleController（角色API）
  - 实现角色管理 RESTful API
  - TDD流程: RoleControllerTest

- [x] **M2-04**: PermissionService（权限查询/树构建）
  - 实现权限树构建、角色权限查询、用户权限检查
  - TDD流程: PermissionServiceTest

- [x] **M2-05**: PermissionController（权限API）
  - 实现权限管理 RESTful API
  - TDD流程: PermissionControllerTest

- [x] **M2-06**: 角色-权限分配逻辑
  - 实现角色权限关联的分配和取消
  - TDD流程: RolePermissionTest

- [x] **M2-07**: 权限模板管理
  - 实现 PermissionTemplateService
  - TDD流程: TemplateTest

- [x] **M2-08**: 自定义 @RequirePermission 注解
  - 实现注解和 AOP 切面
  - TDD流程: AnnotationTest

## 2. 组织结构管理（Day 4-6）

- [x] **M2-09**: OrganizationService（组织树CRUD）
  - 实现组织创建、查询、更新、删除
  - TDD流程: OrgServiceTest

- [x] **M2-10**: OrganizationController（组织API）
  - 实现组织管理 RESTful API
  - TDD流程: OrgControllerTest

- [x] **M2-11**: 组织树递归查询优化
  - 优化树形查询SQL性能
  - TDD流程: 性能测试

## 3. 用户管理重构（Day 6-8）

- [x] **M2-12**: UserService 重构（合并admin/consumer逻辑）
  - 统一用户服务逻辑
  - TDD流程: UserServiceTest

- [x] **M2-13**: UserController 重构
  - 统一用户管理 RESTful API
  - TDD流程: UserControllerTest

- [x] **M2-14**: 用户-角色分配逻辑
  - 实现用户角色关联的分配和取消
  - TDD流程: UserRoleTest

- [x] **M2-15**: 用户批量操作（启用/禁用/分配角色）
  - 实现批量用户操作API
  - TDD流程: BatchTest

- [x] **M2-16**: 数据权限拦截器
  - 实现 DataPermissionInterceptor
  - TDD流程: DataPermissionTest

## 4. 后台管理前端（Day 8-10）

- [x] **M2-17**: 用户管理页面（UserManage.vue）
  - 用户列表、搜索、筛选、批量操作
  - 组件测试: UserManage.test.ts

- [x] **M2-18**: 组织管理页面（OrgManage.vue）
  - 组织树展示、CRUD操作
  - 组件测试: OrgManage.test.ts

- [x] **M2-19**: 角色管理页面（RoleManage.vue）
  - 角色列表、权限分配
  - 组件测试: RoleManage.test.ts

- [x] **M2-20**: 权限管理页面（PermissionManage.vue）
  - 权限树展示、模板管理
  - 组件测试

## Task Dependencies
- [M2-01] depends on [M1 completed]
- [M2-02~M2-08] depends on [M2-01]
- [M2-09~M2-11] depends on [M2-02~M2-08]
- [M2-12~M2-16] depends on [M2-09~M2-11]
- [M2-17~M2-20] depends on [M2-12~M2-16]