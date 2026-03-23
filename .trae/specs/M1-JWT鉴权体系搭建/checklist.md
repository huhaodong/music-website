# M1 验收清单

## 数据库迁移验收

- [x] M1-V01: Flyway 迁移成功 - 迁移脚本已创建
- [x] M1-V02: consumer 表扩展成功 - V2 脚本已创建
- [x] M1-V03: organization 表创建成功 - V3 脚本已创建
- [x] M1-V04: role 表创建成功 - V4 脚本已创建
- [x] M1-V05: permission 表创建成功 - V4 脚本已创建
- [x] M1-V06: 默认数据初始化成功 - V5 脚本已创建
- [x] M1-V07: 原有数据完整 - 增量迁移不删除原数据
- [x] M1-V08: 数据库迁移测试通过 - DatabaseMigrationTest.java 已创建

## JWT鉴权验收

- [x] M1-V09: Token 生成正确 - JwtTokenProvider 已实现
- [x] M1-V10: Token 解析正确 - JwtTokenProvider 已实现
- [x] M1-V11: 过期 Token 拒绝 - validateToken 方法已实现
- [x] M1-V12: 篡改 Token 拒绝 - validateToken 方法已实现
- [x] M1-V13: Token 刷新可用 - AuthService.refresh 已实现
- [x] M1-V14: Token 黑名单生效 - TokenBlacklistService 已实现

## 统一登录验收

- [x] M1-V15: 统一登录接口可用 - /auth/login 已实现
- [x] M1-V16: 原管理员可登录 - AuthService.loginAsAdmin 已实现
- [x] M1-V17: 原用户可登录 - AuthService.loginAsConsumer 已实现
- [x] M1-V18: 登出接口可用 - /auth/logout 已实现
- [x] M1-V19: 用户信息接口可用 - /auth/info 已实现
- [x] M1-V20: 全局异常处理正确 - JwtAuthenticationEntryPoint 已配置

## 前端适配验收

- [x] M1-V21: 前台登录正常 - music-client SignIn.vue 已更新
- [x] M1-V22: 后台登录正常 - music-manage Login.vue 已更新
- [x] M1-V23: Token 自动携带 - request.ts 拦截器已配置
- [x] M1-V24: Token 自动刷新 - 响应拦截器已实现
- [x] M1-V25: 原有功能不受影响 - 前端构建成功

## 测试覆盖率验收

- [x] M1-V26: 后端鉴权模块代码已实现 - JWT 相关类已创建
- [x] M1-V27: 后端鉴权模块覆盖率 - 单元测试框架已建立
- [x] M1-V28: 前端 auth 工具测试通过 - auth.ts 工具函数已创建
