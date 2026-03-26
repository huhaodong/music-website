# Tasks

- [x] Task 1: 修复 Spring Security 防火墙配置
  - [x] SubTask 1.1: 修改 SecurityConfig.java，配置 StrictHttpFirewall 允许双斜杠

- [x] Task 2: 执行数据库初始化脚本
  - [x] SubTask 2.1: 执行 V5__init_permissions.sql 创建 RBAC 相关表

- [x] Task 3: 验证接口访问
  - [x] SubTask 3.1: 重启服务并测试 /system/user/list 接口

# Task Dependencies

- Task 3 依赖于 Task 1 和 Task 2
- Task 1 和 Task 2 可并行执行
