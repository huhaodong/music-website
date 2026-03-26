# Tasks

- [ ] Task 1: 数据库迁移
  - [ ] SubTask 1.1: 查询 admin 表现有管理员数据
  - [ ] SubTask 1.2: 将 admin 数据迁移到 consumer 表
  - [ ] SubTask 1.3: 在 consumer 表中为管理员设置标识（如 userType='admin'）
  - [ ] SubTask 1.4: 迁移后验证数据完整性

- [ ] Task 2: 新增后台登录权限
  - [ ] SubTask 2.1: 在 permission 表中新增 system:admin:login 权限
  - [ ] SubTask 2.2: 为迁移后的管理员用户分配该权限

- [ ] Task 3: 后端 - 修改 AuthController
  - [ ] SubTask 3.1: 修改 admin 登录逻辑，改为查询 consumer 表
  - [ ] SubTask 3.2: 添加 system:admin:login 权限检查
  - [ ] SubTask 3.3: 支持通过 consumer 表的 userType 区分用户类型

- [ ] Task 4: 后端 - 修改 ConsumerServiceImpl
  - [ ] SubTask 4.1: 添加根据 userType 查询用户的方法
  - [ ] SubTask 4.2: 确保管理员登录使用 consumer 表

- [ ] Task 5: 验证测试
  - [ ] SubTask 5.1: 验证管理员用户可正常登录后台
  - [ ] SubTask 5.2: 验证普通用户（无 system:admin:login 权限）无法登录后台
  - [ ] SubTask 5.3: 验证前台用户登录不受影响

# Task Dependencies

- Task 2, 3, 4 依赖于 Task 1
- Task 5 依赖于 Task 2, 3, 4
