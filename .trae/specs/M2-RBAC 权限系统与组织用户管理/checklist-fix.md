# M2 里程碑修复 Checklist

## 阶段 1：基础设施修复

- [x] FIX-01: vitest.config.ts 配置修复
  - [x] 添加 path 模块引入
  - [x] 配置 resolve.alias.@ 指向 src
  - [x] 运行 npm run test 验证配置

- [x] FIX-02: V5__init_permissions.sql 创建
  - [x] permission 表创建语句
  - [x] role 表创建语句
  - [x] role_permission 表创建语句
  - [x] user_role 表创建语句
  - [x] permission_template 表创建语句
  - [x] 初始化系统角色数据

## 阶段 2：后端测试文件

- [x] FIX-03: RoleServiceTest.java - ✅ 28 个测试全部通过
  - [x] 创建角色测试
  - [x] 更新角色测试
  - [x] 删除角色测试（含保护逻辑）
  - [x] 查询角色测试
  - [x] 角色权限分配测试
  - [x] 覆盖率 ≥80%

- [x] FIX-04: RoleControllerTest.java - ✅ 已存在
  - [x] GET /system/role/list 测试
  - [x] POST /system/role/add 测试
  - [x] PUT /system/role/update 测试
  - [x] DELETE /system/role/delete 测试
  - [x] 权限控制测试

- [x] FIX-05: PermissionServiceTest.java - ✅ 37 个测试全部通过，覆盖率 98-100%
  - [x] 权限树构建测试
  - [x] 角色权限查询测试
  - [x] 用户权限检查测试
  - [x] 覆盖率 ≥80%

- [x] FIX-06: PermissionControllerTest.java - ✅ 已创建
  - [x] GET /system/permission/tree 测试
  - [x] GET /system/permission/role 测试
  - [x] GET /system/permission/user 测试

- [x] FIX-07: PermissionTemplateServiceTest.java - ✅ 26 个测试全部通过
  - [x] 创建模板测试
  - [x] 更新模板测试
  - [x] 删除模板测试
  - [x] 应用模板测试
  - [x] 覆盖率 ≥80%

- [x] FIX-08: RequirePermissionAspectTest.java - ✅ 16 个测试全部通过
  - [x] 有权限用户访问测试
  - [x] 无权限用户拒绝测试（403）
  - [x] 超级管理员跳过权限测试
  - [x] 覆盖率 ≥90%

- [x] FIX-09: OrganizationServiceTest.java - ✅ 34 个测试全部通过，覆盖率 100%
  - [x] 创建组织测试（根组织/子组织）
  - [x] 更新组织测试
  - [x] 删除组织测试（含保护逻辑）
  - [x] 组织树查询测试
  - [x] path 生成验证
  - [x] 覆盖率 ≥80%

- [x] FIX-10: OrganizationControllerTest.java - ✅ 已创建
  - [x] GET /system/org/tree 测试
  - [x] POST /system/org/add 测试
  - [x] PUT /system/org/update 测试
  - [x] DELETE /system/org/delete 测试
  - [x] GET /system/org/{id}/users 测试

- [x] FIX-11: UserServiceTest.java - ✅ 41 个测试全部通过
  - [x] 用户 CRUD 测试
  - [x] 用户角色分配测试
  - [x] 批量操作测试
  - [x] 数据权限测试
  - [x] 原有用户兼容测试
  - [x] 覆盖率 ≥80%

- [x] FIX-12: UserControllerTest.java - ✅ 已创建
  - [x] GET /system/user/list 测试
  - [x] POST /system/user/add 测试
  - [x] PUT /system/user/update 测试
  - [x] DELETE /system/user/delete 测试
  - [x] 批量操作测试

- [x] FIX-13: UserRoleServiceTest.java - ✅ 16 个测试全部通过
  - [x] 分配角色测试
  - [x] 取消角色测试
  - [x] 查询用户角色测试
  - [x] 覆盖率 ≥80%

- [x] FIX-14: DataPermissionInterceptorTest.java - ✅ 已创建
  - [x] 超级管理员数据权限测试
  - [x] 组织管理员数据权限测试
  - [x] 普通用户数据权限测试
  - [x] 跨组织访问拒绝测试
  - [x] 覆盖率 ≥90%

## 阶段 3：前端测试修复

- [x] FIX-15: 前端测试运行修复
  - [x] UserManage.test.ts 运行通过
  - [x] OrgManage.test.ts 运行通过
  - [x] RoleManage.test.ts 运行通过

- [x] FIX-16: PermissionManage.test.ts - ✅ 22 个测试全部通过
  - [x] 权限树展示测试
  - [x] 权限模板选择测试
  - [x] 权限保存测试
  - [x] 覆盖率 ≥70%

## 阶段 4：集成测试

- [x] FIX-17: RBAC 集成测试 - ✅ 12 个集成测试已创建
  - [x] 登录→获取权限→访问受控资源
  - [x] 角色分配→权限变更→访问控制
  - [x] 组织变更→数据权限变更

## 阶段 5：验证与文档

- [x] FIX-18: 完整测试套件验证
  - [x] mvn test 所有测试通过
  - [x] npm run test 所有测试通过
  - [x] JaCoCo 报告生成
  - [x] Service 层覆盖率 ≥80%

- [x] FIX-19: 更新 tasks.md
  - [x] 更新所有任务状态
  - [x] 添加测试验证说明

## 最终验收

- [x] 所有后端测试文件存在且通过
- [x] V5__init_permissions.sql 存在且正确
- [x] 所有前端测试通过
- [x] 测试覆盖率达标
- [x] checklist.md 所有检查点标记为 ✅
- [x] M2 里程碑可正式验收
