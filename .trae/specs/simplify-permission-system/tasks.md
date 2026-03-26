# Tasks

- [ ] Task 1: 数据库权限数据重构
  - [ ] SubTask 1.1: 清空现有权限数据
  - [ ] SubTask 1.2: 批量插入新的原子权限数据

- [ ] Task 2: 前端 - 权限管理页面重构
  - [ ] SubTask 2.1: 移除树形结构，改用权限列表
  - [ ] SubTask 2.2: 按模块分组显示权限
  - [ ] SubTask 2.3: 移除权限模板功能

- [ ] Task 3: 前端 - 角色管理增加权限勾选
  - [ ] SubTask 3.1: 角色编辑对话框增加权限列表勾选
  - [ ] SubTask 3.2: 按模块分组显示权限，支持全选/取消

- [ ] Task 4: 后端 - PermissionServiceImpl 简化
  - [ ] SubTask 4.1: 移除树形结构相关逻辑
  - [ ] SubTask 4.2: 保留基础的增删改查

- [ ] Task 5: 后端 - 权限模板功能移除
  - [ ] SubTask 5.1: 移除 PermissionTemplateController

- [ ] Task 6: 验证测试
  - [ ] SubTask 6.1: 验证权限列表显示正确
  - [ ] SubTask 6.2: 验证角色权限分配功能正常

# Task Dependencies

- Task 2, 3, 4, 5 依赖于 Task 1
- Task 6 依赖于 Task 2, 3, 4
