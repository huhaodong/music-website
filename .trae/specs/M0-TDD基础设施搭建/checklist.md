# M0 验收清单

## 环境验收

- [x] M0-V01: 后端可编译 - `mvn clean compile` 零错误
- [x] M0-V02: 后端可启动 - Spring Boot 启动无异常，端口监听正常
- [x] M0-V03: 前台可启动 - `npm run dev` 启动无错误
- [x] M0-V04: 后台可启动 - `npm run dev` 启动无错误
- [x] M0-V05: 数据库连接正常 - 可正常读写数据
- [x] M0-V06: Redis 连接正常 - 验证码功能正常
- [x] M0-V07: MinIO 连接正常 - 文件上传功能正常

## 冒烟测试验收

- [x] M0-V08: 后台管理员登录 - 使用默认管理员账号可登录后台
- [x] M0-V09: 前台用户注册 - 新用户可注册成功
- [x] M0-V10: 前台用户登录 - 注册用户可正常登录
- [x] M0-V11: 歌曲播放 - 歌曲可正常播放
- [x] M0-V12: 后台歌手管理 - CRUD 操作正常
- [x] M0-V13: 后台歌曲管理 - CRUD 操作正常
- [x] M0-V14: 后台歌单管理 - CRUD 操作正常

## 测试框架验收

- [x] M0-V15: 后端单元测试可运行 - `mvn test` 通过 SongServiceTest 14个测试用例
- [x] M0-V16: 后端 MockMvc 测试可运行 - SongControllerTest 可执行（部分因业务数据问题失败）
- [x] M0-V17: 后端覆盖率报告可生成 - JaCoCo 报告在 target/site/jacoco
- [x] M0-V18: 前端 music-client 测试可运行 - Vitest 测试框架正常运行
- [x] M0-V19: 前端 music-manage 测试可运行 - Vitest 测试通过 5 个测试

## 规范验收

- [x] M0-V20: Git 分支结构已建立 - main + develop 分支存在
- [x] M0-V21: Commit 规范已配置 - .gitmessage 模板已创建
- [x] M0-V22: ESLint 已配置 - `npm run lint` 可执行
- [x] M0-V23: 代码结构文档已输出 - doc/开发规范/代码结构说明.md
