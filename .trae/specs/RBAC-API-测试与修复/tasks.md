# Tasks

## 第一阶段：数据库迁移问题修复

- [ ] Task 1: 配置Flyway数据库迁移
  - [ ] SubTask 1.1: 在application.properties中添加Flyway配置
  - [ ] SubTask 1.2: 验证V4和V3迁移脚本正确创建表
  - [ ] SubTask 1.3: 运行迁移创建缺失的表

## 第二阶段：API测试代码编写

- [ ] Task 2: 编写用户管理API测试
  - [ ] SubTask 2.1: 完善UserManage.test.ts测试用例
  - [ ] SubTask 2.2: 验证getAllUsers API路径和响应

- [ ] Task 3: 编写角色管理API测试
  - [ ] SubTask 3.1: 完善RoleManage.test.ts测试用例
  - [ ] SubTask 3.2: 验证getAllRoles API路径和响应

- [ ] Task 4: 编写权限管理API测试
  - [ ] SubTask 4.1: 完善PermissionManage.test.ts测试用例
  - [ ] SubTask 4.2: 验证getAllPermissions API路径和响应

- [ ] Task 5: 编写组织管理API测试
  - [ ] SubTask 5.1: 创建OrganizationManage.test.ts测试文件
  - [ ] SubTask 5.2: 验证getAllOrganizations API路径和响应

## 第三阶段：代码修复

- [ ] Task 6: 修复前端API路径问题
  - [ ] SubTask 6.1: 检查并修复SystemManager中的API路径
  - [ ] SubTask 6.2: 验证前端与后端API路径一致性

## Task Dependencies
- Task 1必须在Task 2-5之前完成，因为测试需要数据库表存在
- Task 6可以在Task 1完成后进行
