# Tasks

## 1. [Done] 修复前端 API 路径配置
- [x] 1.1: 修改 `SystemManager.getAllUsers()` 路径为 `system/user`
- [x] 1.2: 修改 `SystemManager.getAllRoles()` 路径为 `system/role/list`
- [x] 1.3: 修改 `SystemManager.getPermissionTree()` 路径为 `system/permission/tree`
- [x] 1.4: 修改 `SystemManager.getOrganizationTree()` 路径为 `system/organization/tree`
- [x] 1.5: 修改其他相关 API 路径添加 `/system` 前缀

## 2. [Done] 验证修复
- [x] 2.1: 测试后端 API 是否正确响应（需要先登录获取 token）
- [x] 2.2: 验证前端页面是否正常显示数据
- [x] 2.3: 测试所有管理页面的 CRUD 操作

## 3. [Done] 页面功能测试
- [x] 3.1: 用户管理页面 - 添加、编辑、删除用户
- [x] 3.2: 角色管理页面 - 添加、编辑、删除角色
- [x] 3.3: 组织管理页面 - 添加、编辑、删除组织
- [x] 3.4: 权限管理页面 - 添加、编辑、删除权限

# Task Dependencies
- Task 2 依赖 Task 1 完成
- Task 3 依赖 Task 2 完成
