# 数据库初始化一致性修复验收清单

## schema-h2.sql 修复

- [x] consumer 表包含 status 字段
- [x] consumer 表包含 org_id 字段
- [x] consumer 表包含 last_login_time 字段

## data-h2.sql 修复

### 角色数据
- [x] SUPER_ADMIN (id=1, code='SUPER_ADMIN') 存在
- [x] USER (id=2, code='USER') 存在
- [x] SINGER (id=3, code='SINGER') 存在

### 权限数据
- [x] 系统管理权限存在
- [x] 用户管理权限存在
- [x] 角色管理权限存在
- [x] 歌曲管理权限存在
- [x] 歌手管理权限存在
- [x] 歌单管理权限存在
- [x] 评论管理权限存在

### 角色权限关联
- [x] SUPER_ADMIN 角色拥有所有权限
- [x] USER 角色拥有基本权限
- [x] SINGER 角色拥有歌手相关权限

### 用户角色关联
- [x] 测试用户 (id=1) 分配了 USER 角色
- [x] 管理员用户 (id=2) 分配了相应角色

## 编译验证

- [x] mvn compile 编译成功
- [x] application-h2.properties 配置正确

## 功能验证

- [x] 使用 H2 数据库启动后查询角色列表返回正确数据
- [x] 使用 H2 数据库启动后查询用户角色返回正确结果
- [x] 使用 MySQL 数据库启动后 DataInitRunner 正常工作

## 额外修复

- [x] pom.xml 中 H2 依赖 scope 从 test 改为 runtime
