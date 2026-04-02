# Tasks

- [ ] Task 1: 审计当前用户注册与新增逻辑
  - [ ] SubTask 1.1: 检查 `UserServiceImpl.addUser` 和 `registry` 逻辑，确认角色分配切入点
  - [ ] SubTask 1.2: 确认数据库中“普通用户”角色的 ID 与 Code（预期为 `USER`）

- [ ] Task 2: 实现强制默认角色分配逻辑
  - [ ] SubTask 2.1: 在 `UserServiceImpl` 中增加默认角色分配逻辑，确保事务一致性
  - [ ] SubTask 2.2: 修复后台管理新增用户时可能遗漏的角色分配逻辑

- [ ] Task 3: 编写数据修复脚本
  - [ ] SubTask 3.1: 编写 Flyway 迁移脚本，为存量无角色用户分配默认 `USER` 角色
  - [ ] SubTask 3.2: 验证 SQL 脚本的幂等性

- [ ] Task 4: 编写并执行自动化测试
  - [ ] SubTask 4.1: 编写单元测试验证新注册用户自动关联 `USER` 角色
  - [ ] SubTask 4.2: 编写集成测试验证存量用户修复逻辑
  - [ ] SubTask 4.3: 验证在角色缺失情况下的异常处理

- [ ] Task 5: 验证与交付
  - [ ] SubTask 5.1: 运行所有相关测试并确保通过
  - [ ] SubTask 5.2: 手动测试用户注册流程，核对数据库记录

# Task Dependencies
- Task 2 依赖 Task 1
- Task 4 依赖 Task 2 和 Task 3
- Task 5 依赖 Task 4
