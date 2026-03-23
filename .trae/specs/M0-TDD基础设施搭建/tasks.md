# M0: 项目基线确认与TDD基础设施搭建 - 任务列表

## 第一阶段：环境搭建与项目启动验证

- [x] M0-01: 克隆项目并搭建本地开发环境
- [x] M0-02: 配置 MySQL / Redis / MinIO 依赖服务（docker-compose.dev.yml）
- [x] M0-03: 后端 Spring Boot 编译启动验证
- [x] M0-04: 前端 music-client 编译启动验证
- [x] M0-05: 前端 music-manage 编译启动验证
- [x] M0-06: 执行原项目核心功能冒烟测试

## 第二阶段：后端测试框架搭建

- [x] M0-07: 引入 JUnit 5 + Mockito 依赖（pom.xml 更新）
- [x] M0-08: 配置 H2 内存数据库用于测试（application-test.yml）
- [x] M0-09: 编写 SongService 单元测试（SongServiceTest.java）
- [x] M0-10: 引入 Spring MockMvc 配置，编写 SongController 测试（SongControllerTest.java）
- [x] M0-11: 配置测试覆盖率报告工具（JaCoCo）
- [x] M0-12: 验证 `mvn test` 可正常执行全部测试

## 第三阶段：前端测试框架搭建

- [x] M0-13: music-client 引入 Vitest + Vue Test Utils
- [x] M0-14: 编写第一个前端工具函数测试（utils.test.ts）
- [x] M0-15: 编写第一个 Vue 组件测试（Component.test.ts）
- [x] M0-16: music-manage 引入 Vitest + Vue Test Utils
- [x] M0-17: 验证 `npm run test` 可正常执行

## 第四阶段：开发规范建立

- [x] M0-18: 建立 Git 分支管理策略（Git Flow）
- [x] M0-19: 配置 commit message 规范（commitlint）
- [x] M0-20: 配置 ESLint / Prettier（前端）
- [x] M0-21: 建立 develop 分支，保护 main 分支
- [x] M0-22: 编写现有代码结构分析文档（代码结构说明.md）
- [x] M0-23: 编写 TDD 开发流程规范文档（TDD规范.md）

# 任务依赖关系
- M0-03~M0-06 依赖 M0-01, M0-02
- M0-07~M0-12 依赖 M0-03
- M0-13~M0-17 依赖 M0-04, M0-05
- M0-18~M0-21 依赖无特定前置
- M0-22~M0-23 可在任意阶段完成
