# M0: 项目基线确认与TDD基础设施搭建 Spec

## Why
在二次开发前，必须确认原项目可完整运行，并搭建TDD测试框架，为后续M1-M8的开发奠定基础。M0是起点，不改动任何业务代码。

## What Changes
- 验证后端 Spring Boot 项目可编译启动
- 验证前端 music-client 和 music-manage 可编译启动
- 搭建后端 JUnit 5 + Mockito + MockMvc + JaCoCo 测试框架
- 搭建前端 Vitest + Vue Test Utils 测试框架
- 建立 Git Flow 分支策略和开发规范

## Impact
- Affected specs: 新增测试框架支持
- Affected code: 无业务代码改动，仅新增测试和配置

## ADDED Requirements

### Requirement: 后端测试框架
后端必须配置 JUnit 5 + Mockito + MockMvc + JaCoCo

#### Scenario: 后端测试框架可用
- **WHEN** 执行 `mvn test`
- **THEN** 测试可正常运行并生成覆盖率报告

### Requirement: 前端测试框架
前端必须配置 Vitest + Vue Test Utils

#### Scenario: 前端测试框架可用
- **WHEN** 执行 `npm run test`
- **THEN** 测试可正常运行

### Requirement: 开发规范
项目必须建立 Git Flow 分支策略和代码规范

#### Scenario: 开发规范已建立
- **WHEN** 开发者提交代码
- **THEN** 必须遵循 commitlint 规范，代码通过 ESLint 检查

## MODIFIED Requirements

### Requirement: 项目启动验证
原项目必须可正常编译、启动、运行

#### Scenario: 项目完整运行
- **WHEN** 启动后端和前端服务
- **THEN** 所有服务正常启动，核心功能可用

## REMOVED Requirements
无

## Task Dependencies
M0 无前置依赖，可直接开始
