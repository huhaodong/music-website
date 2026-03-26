# Tasks

## 1. [Done] 修改路由配置
- [x] 1.1: 将 RBAC 路由从独立路由改回 Home 的子路由
- [x] 1.2: 更新路由路径为 `/Home/user`, `/Home/org`, `/Home/role`, `/Home/permission`
- [x] 1.3: 删除临时添加的重定向路由

## 2. [Done] 修改 UserManage.vue 布局
- [x] 2.1: 添加 `<el-breadcrumb>` 面包屑导航
- [x] 2.2: 用 `.container` 和 `.handle-box` 包裹内容
- [x] 2.3: 添加样式 `.crumbs`

## 3. [Done] 修改 OrgManage.vue 布局
- [x] 3.1: 添加 `<el-breadcrumb>` 面包屑导航
- [x] 3.2: 用 `.container` 和 `.handle-box` 包裹内容

## 4. [Done] 修改 RoleManage.vue 布局
- [x] 4.1: 添加 `<el-breadcrumb>` 面包屑导航
- [x] 4.2: 用 `.container` 和 `.handle-box` 包裹内容

## 5. [Done] 修改 PermissionManage.vue 布局
- [x] 5.1: 添加 `<el-breadcrumb>` 面包屑导航
- [x] 5.2: 用 `.container` 和 `.handle-box` 包裹内容

## 6. [Done] 修改菜单导航链接
- [x] 6.1: 更新 YinAside.vue 中的 RBAC 菜单链接为 `/Home/*` 路径

## 7. [Done] 验证测试
- [x] 7.1: 验证用户管理页面显示正确
- [x] 7.2: 验证组织管理页面显示正确
- [x] 7.3: 验证角色管理页面显示正确
- [x] 7.4: 验证权限管理页面显示正确

# Task Dependencies
- Task 1 完成后才能进行 Task 2-6
- Task 2-6 可以并行进行
- Task 7 依赖 Task 1-6 完成
