# M2: RBAC权限系统与组织用户管理

> **里程碑编号**: M2
> **预计周期**: 第4-5周（10个工作日）
> **前置依赖**: M1 全部验收通过
> **核心目标**: 实现完整的RBAC权限控制体系、组织结构管理、用户管理重构
> **开发分支**: `feature/rbac-permission`

---

## 1. 目标概述

M2 构建系统的权限基础设施：
1. **RBAC 权限模型**：角色/权限/模板的完整 CRUD 和分配机制
2. **组织结构管理**：树形组织架构的 CRUD
3. **用户管理重构**：将 consumer + admin 统一为 user 体系，支持角色分配
4. **数据权限隔离**：基于组织和角色的数据可见性控制

**关键约束：权限系统必须向后兼容——未分配角色的用户默认拥有"普通用户"权限。**

---

## 2. 开发任务清单

### 2.1 角色权限后端（Day 1-4）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M2-01 | 权限树数据初始化脚本 | 先写验证测试 | V5__init_permissions.sql | 0.5天 |
| M2-02 | RoleService（角色CRUD） | RED→GREEN: RoleServiceTest | RoleService.java | 0.5天 |
| M2-03 | RoleController（角色API） | RED→GREEN: RoleControllerTest | RoleController.java | 0.5天 |
| M2-04 | PermissionService（权限查询/树构建） | RED→GREEN: PermissionServiceTest | PermissionService.java | 0.5天 |
| M2-05 | PermissionController（权限API） | RED→GREEN: PermissionControllerTest | PermissionController.java | 0.25天 |
| M2-06 | 角色-权限分配逻辑 | RED→GREEN: RolePermissionTest | 分配/取消分配逻辑 | 0.5天 |
| M2-07 | 权限模板管理 | RED→GREEN: TemplateTest | PermissionTemplateService | 0.5天 |
| M2-08 | 自定义 @RequirePermission 注解 | RED→GREEN: AnnotationTest | 注解 + AOP切面 | 0.75天 |

### 2.2 组织结构管理（Day 4-6）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M2-09 | OrganizationService（组织树CRUD） | RED→GREEN: OrgServiceTest | OrganizationService.java | 1天 |
| M2-10 | OrganizationController（组织API） | RED→GREEN: OrgControllerTest | OrganizationController.java | 0.5天 |
| M2-11 | 组织树递归查询优化 | RED→GREEN: 性能测试 | 优化后的树查询SQL | 0.5天 |

### 2.3 用户管理重构（Day 6-8）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M2-12 | UserService 重构（合并admin/consumer逻辑） | RED→GREEN: UserServiceTest | UserService.java | 1天 |
| M2-13 | UserController 重构 | RED→GREEN: UserControllerTest | UserController.java | 0.5天 |
| M2-14 | 用户-角色分配逻辑 | RED→GREEN: UserRoleTest | 分配/取消逻辑 | 0.5天 |
| M2-15 | 用户批量操作（启用/禁用/分配角色） | RED→GREEN: BatchTest | 批量操作API | 0.5天 |
| M2-16 | 数据权限拦截器 | RED→GREEN: DataPermissionTest | DataPermissionInterceptor | 0.5天 |

### 2.4 后台管理前端（Day 8-10）

| 编号 | 任务 | TDD流程 | 交付物 | 工时 |
|------|------|---------|--------|------|
| M2-17 | 用户管理页面（UserManage.vue） | 组件测试 | UserManage.vue | 0.5天 |
| M2-18 | 组织管理页面（OrgManage.vue） | 组件测试 | OrgManage.vue | 0.5天 |
| M2-19 | 角色管理页面（RoleManage.vue） | 组件测试 | RoleManage.vue | 0.5天 |
| M2-20 | 权限管理页面（PermissionManage.vue） | 组件测试 | PermissionManage.vue | 0.5天 |

---

## 3. TDD 测试用例规划

### 3.1 后端单元测试

```
test/
├── rbac/
│   ├── RoleServiceTest.java
│   │   ├── 创建角色_正常参数_应成功创建
│   │   ├── 创建角色_重复名称_应抛出异常
│   │   ├── 删除角色_有用户关联时_应拒绝删除
│   │   ├── 删除角色_系统内置角色_应拒绝删除
│   │   ├── 查询角色列表_应返回分页数据
│   │   └── 分配权限_应正确建立角色权限关联
│   ├── PermissionServiceTest.java
│   │   ├── 获取权限树_应返回正确的树形结构
│   │   ├── 获取角色权限_应返回该角色的所有权限编码
│   │   └── 检查用户权限_有权限时返回true_无权限时返回false
│   ├── PermissionTemplateServiceTest.java
│   │   ├── 创建模板_应保存权限JSON
│   │   ├── 应用模板_应为角色批量分配权限
│   │   └── 查询模板列表_应返回所有可用模板
│   ├── RequirePermissionAspectTest.java
│   │   ├── 有权限的用户_应允许访问
│   │   ├── 无权限的用户_应返回403
│   │   └── 超级管理员_应跳过权限检查
│   ├── RoleControllerTest.java (MockMvc)
│   │   ├── GET /api/role 应返回角色列表
│   │   ├── POST /api/role 有权限用户_应创建成功
│   │   ├── POST /api/role 无权限用户_应返回403
│   │   ├── PUT /api/role/{id} 应更新成功
│   │   ├── DELETE /api/role/{id} 应删除成功
│   │   └── POST /api/role/{id}/permissions 应分配权限成功
│   └── PermissionControllerTest.java (MockMvc)
│       ├── GET /api/permission/tree 应返回完整权限树
│       └── GET /api/permission/template 应返回模板列表
├── org/
│   ├── OrganizationServiceTest.java
│   │   ├── 创建组织_根节点_应设置level=1和path=/
│   │   ├── 创建组织_子节点_应正确设置level和path
│   │   ├── 删除组织_有子组织时_应拒绝删除
│   │   ├── 删除组织_有关联用户时_应拒绝删除
│   │   ├── 获取组织树_应返回正确的树形结构
│   │   └── 移动组织_应正确更新path
│   └── OrganizationControllerTest.java (MockMvc)
│       ├── GET /api/org/tree 应返回组织树
│       ├── POST /api/org 应创建组织
│       ├── PUT /api/org/{id} 应更新组织
│       ├── DELETE /api/org/{id} 应删除组织
│       └── GET /api/org/{id}/users 应返回组织下用户
├── user/
│   ├── UserServiceTest.java
│   │   ├── 查询用户列表_应支持按组织筛选
│   │   ├── 查询用户列表_应支持按角色筛选
│   │   ├── 创建用户_应关联默认角色
│   │   ├── 分配角色_应正确建立用户角色关联
│   │   ├── 批量启用_应更新状态为启用
│   │   ├── 批量禁用_应更新状态为禁用
│   │   └── 数据权限_普通用户只能查看本组织数据
│   └── DataPermissionInterceptorTest.java
│       ├── 超级管理员_不限制数据范围
│       ├── 组织管理员_限制为本组织及下级数据
│       └── 普通用户_限制为个人数据
```

### 3.2 前端测试

```
tests/
├── components/
│   ├── UserManage.test.ts
│   │   ├── 用户列表_应正确渲染用户数据
│   │   ├── 搜索框_输入关键字应触发搜索
│   │   ├── 角色筛选_选择角色应过滤列表
│   │   └── 批量操作_勾选用户后操作按钮可用
│   ├── OrgManage.test.ts
│   │   ├── 组织树_应正确渲染树形结构
│   │   ├── 点击节点_应展示组织详情
│   │   └── 添加子组织_应在正确位置插入
│   └── RoleManage.test.ts
│       ├── 角色列表_应正确渲染
│       ├── 权限分配_勾选权限后应可保存
│       └── 权限模板_应可选择并应用模板
├── utils/
│   └── permission.test.ts
│       ├── hasPermission 有权限时返回true
│       ├── hasPermission 无权限时返回false
│       └── checkRoutePermission 应正确判断路由权限
```

---

## 4. 测试验收清单

### 4.1 角色权限验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M2-V01 | 角色创建 | 可创建自定义角色，名称/编码/描述正确入库 | API + 数据库验证 | ☐ |
| M2-V02 | 角色编辑 | 可修改角色信息，修改立即生效 | 修改后查询验证 | ☐ |
| M2-V03 | 角色删除 | 无用户关联的角色可删除，有关联的拒绝 | 两种场景验证 | ☐ |
| M2-V04 | 系统角色保护 | 系统内置角色不可删除/编码不可修改 | 尝试删除/修改验证 | ☐ |
| M2-V05 | 权限树展示 | 完整的权限树可正确展示 | API返回验证 | ☐ |
| M2-V06 | 权限分配 | 角色可关联任意权限组合 | 分配后验证 | ☐ |
| M2-V07 | 权限模板 | 可创建/编辑/应用权限模板 | 模板CRUD验证 | ☐ |
| M2-V08 | @RequirePermission | 有权限可访问，无权限返回403 | 多角色验证 | ☐ |

### 4.2 组织结构验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M2-V09 | 组织树展示 | 树形结构正确展示所有层级 | API + 前端验证 | ☐ |
| M2-V10 | 创建组织 | 可创建根组织和子组织，path正确生成 | 创建后验证path | ☐ |
| M2-V11 | 编辑组织 | 修改名称/排序等信息正确更新 | 修改后验证 | ☐ |
| M2-V12 | 删除组织 | 有子组织或用户时拒绝删除 | 异常场景验证 | ☐ |
| M2-V13 | 组织下用户 | 可查看指定组织下的所有用户 | API查询验证 | ☐ |

### 4.3 用户管理验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M2-V14 | 用户列表 | 支持分页、组织筛选、角色筛选、关键字搜索 | 各筛选条件验证 | ☐ |
| M2-V15 | 创建用户 | 可创建用户并分配组织和角色 | 创建后验证 | ☐ |
| M2-V16 | 编辑用户 | 可修改用户信息、组织、角色 | 修改后验证 | ☐ |
| M2-V17 | 批量启用/禁用 | 勾选多个用户可批量操作 | 批量操作验证 | ☐ |
| M2-V18 | 角色分配 | 用户可被分配一个或多个角色 | 分配后验证 | ☐ |
| M2-V19 | 原有用户兼容 | 原consumer表用户自动获得默认角色 | 原用户登录验证 | ☐ |

### 4.4 数据隔离验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M2-V20 | 超级管理员 | 可看到所有组织和所有用户的数据 | 管理员账号登录验证 | ☐ |
| M2-V21 | 组织管理员 | 只能看到本组织及下级组织的数据 | 组织管理员登录验证 | ☐ |
| M2-V22 | 普通用户 | 只能看到自己相关的数据 | 普通用户登录验证 | ☐ |
| M2-V23 | 跨组织访问 | 访问无权限的组织数据应被拒绝 | 越权请求验证 | ☐ |

### 4.5 测试覆盖率验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M2-V24 | 后端 RBAC 模块测试 | 所有测试绿灯 | `mvn test` | ☐ |
| M2-V25 | 后端 RBAC 模块覆盖率 | Service层 ≥ 80% | JaCoCo 报告 | ☐ |
| M2-V26 | 前端权限组件测试 | 所有测试绿灯 | `npm run test` | ☐ |
| M2-V27 | 集成测试 | 登录→获取权限→访问受控资源 全链路通过 | 集成测试脚本 | ☐ |

---

## 5. 里程碑完成标志

**M2 完成的充要条件：**

- [ ] 角色 CRUD + 权限分配功能正常
- [ ] 组织结构树形管理功能正常
- [ ] 用户管理重构完成（统一用户体系）
- [ ] @RequirePermission 权限注解生效
- [ ] 数据隔离（超管/组织管理员/普通用户）生效
- [ ] 后台管理页面可正常使用
- [ ] 原有用户向后兼容（自动分配默认角色）
- [ ] 所有测试通过，核心 Service 覆盖率 ≥ 80%

**进入 M3 的前提：M2 全部验收项通过。**
