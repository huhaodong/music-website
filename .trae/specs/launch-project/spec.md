# 项目启动规范

## Why
需要启动音乐版权资产管理项目的开发环境，包括后端服务、前台客户端和后台管理端，确保所有服务正常运行并可以访问。

## What Changes
- 检查并启动 MySQL 数据库服务
- 检查并启动 Redis 服务
- 编译并启动后端 Spring Boot 服务 (music-server)
- 安装依赖并启动前台客户端 (music-client)
- 安装依赖并启动后台管理端 (music-manage)

## Impact
- Affected specs: 无新功能，仅启动现有服务
- Affected code: 无代码改动

## ADDED Requirements

### Requirement: 后端服务启动
后端 Spring Boot 服务必须正常启动并可访问

#### Scenario: 后端启动成功
- **WHEN** 执行 `mvn spring-boot:run`
- **THEN** 服务在端口 8888 启动，无异常错误

#### Scenario: 数据库连接正常
- **WHEN** 后端服务启动
- **THEN** 成功连接 MySQL 数据库，Flyway 迁移执行成功

#### Scenario: Redis 连接正常
- **WHEN** 后端服务启动
- **THEN** 成功连接 Redis 服务

### Requirement: 前台客户端启动
前台客户端 (music-client) 必须正常启动并可访问

#### Scenario: 前台启动成功
- **WHEN** 执行 `npm run serve`
- **THEN** 服务在端口 8080 启动，浏览器可访问首页

### Requirement: 后台管理端启动
后台管理端 (music-manage) 必须正常启动并可访问

#### Scenario: 后台启动成功
- **WHEN** 执行 `npm run serve`
- **THEN** 服务在端口 8081 启动，浏览器可访问登录页

## MODIFIED Requirements
无

## REMOVED Requirements
无

## Task Dependencies
无前置依赖，可直接执行
