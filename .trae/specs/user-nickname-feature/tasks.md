# Tasks

- [x] Task 1: 数据库添加 nickname 字段
  - [x] SubTask 1.1: 创建数据库迁移脚本 V7__add_nickname_to_consumer.sql
  - [x] SubTask 1.2: 执行迁移脚本添加 nickname 字段

- [x] Task 2: 后端修改 - Consumer 模型添加 nickname
  - [x] SubTask 2.1: ConsumerRequest 添加 nickname 字段
  - [x] SubTask 2.2: ConsumerServiceImpl.addUser() 添加默认昵称生成逻辑
  - [x] SubTask 2.3: ConsumerServiceImpl.updateUserMsg() 支持更新昵称

- [x] Task 3: 前端修改 - Personal.vue 展示昵称
  - [x] SubTask 3.1: 修改模板展示昵称和用户名
  - [x] SubTask 3.2: 添加获取/更新昵称的数据处理

# Task Dependencies
- Task 2 依赖 Task 1（需要先有 nickname 字段）
- Task 3 依赖 Task 2（需要后端支持 nickname）
