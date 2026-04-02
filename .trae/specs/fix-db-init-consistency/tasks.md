# 数据库初始化一致性修复任务列表

## 任务 1: 修复 schema-h2.sql

- [x] 1.1: 检查 consumer 表字段完整性
  - 确保包含 `status`, `org_id`, `last_login_time` 字段
  - 结果: schema-h2.sql 中 consumer 表已包含这些字段，无需修改

## 任务 2: 修复 data-h2.sql 角色数据

- [x] 2.1: 修正角色数据使与 V5__init_default_data.sql 一致
  - SUPER_ADMIN (id=1)
  - USER (id=2)
  - SINGER (id=3)

- [x] 2.2: 修正权限数据
  - 确保包含系统管理、用户管理、角色管理、权限管理权限
  - 确保包含歌曲、歌手、歌单、评论相关权限

- [x] 2.3: 修正 role_permission 关联数据
  - SUPER_ADMIN 拥有所有权限
  - USER 拥有基本权限（歌曲列表、歌手列表等）

## 任务 3: 添加用户角色初始数据

- [x] 3.1: 添加 SINGER 角色的 role_permission
- [x] 3.2: 添加用户角色关联数据
  - 确保测试用户有 USER 角色
  - 确保管理员用户有相应角色

## 任务 4: 编译验证

- [x] 4.1: 运行 mvn compile 验证代码无错误
- [x] 4.2: 检查 H2 数据库初始化是否正常
  - H2 模式启动成功，找到 USER 角色 (id=2)
  - MySQL 模式启动成功，找到 USER 角色 (id=3, code=user)

## 任务 5: 修复 pom.xml H2 依赖 scope

- [x] 5.1: 将 H2 依赖的 scope 从 test 改为 runtime
  - 修改 pom.xml 第50行

## Task Dependencies
- [2.x] 依赖 [1.x] - 已完成
- [3.x] 依赖 [2.x] - 已完成
- [4.x] 依赖 [3.x] - 已完成
- [5.x] 独立任务 - 已完成
