# Tasks

- [x] Task 1: 在 Organization.java 实体类中添加 code 字段
  - [x] SubTask 1.1: 添加 private String code; 字段及 getter/setter
  - [x] SubTask 1.2: 确保 Lombok @Data 注解正常工作

- [x] Task 2: 在 OrganizationRequest.java 请求类中添加 code 字段
  - [x] SubTask 2.1: 添加 private String code; 字段

- [x] Task 3: 验证修复
  - [x] SubTask 3.1: 重新编译项目
  - [ ] SubTask 3.2: 测试添加组织功能

# Task Dependencies
- Task 2 依赖 Task 1（请求类可能需要参考实体类结构）
- Task 3 依赖 Task 1 和 Task 2
