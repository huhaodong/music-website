# /system/user/list 接口 500 错误修复计划

## Why

`/system/user/list` 接口访问时返回 500 Internal Server Error，经日志分析发现两个问题：
1. Spring Security 防火墙拦截包含双斜杠的 URL
2. 数据库 RBAC 相关表（role、permission等）未创建

## What Changes

### 问题 1：URL 双斜杠拦截
- **原因**：前端请求 `/system/user//list` 或类似路径，被 `StrictHttpFirewall` 拒绝
- **修复**：配置 `StrictHttpFirewall` 允许双斜杠，或修复前端请求路径

### 问题 2：数据库表缺失
- **原因**：`V5__init_permissions.sql` 未执行
- **修复**：执行数据库初始化脚本

## Impact

- Affected specs: M2-RBAC权限系统与组织用户管理
- Affected code:
  - `SecurityConfig.java` - Spring Security 配置
  - 数据库 `tp_music` - 需要初始化表结构

## MODIFIED Requirements

### Requirement: Spring Security 防火墙配置
系统 SHALL 配置 `StrictHttpFirewall` 允许规范化后的双斜杠路径

#### Scenario: 包含双斜杠的请求
- **WHEN** 前端发送 `/system/user//list` 请求
- **THEN** 请求应被规范化处理而非直接拒绝

## ADDED Requirements

### Requirement: 数据库初始化
系统 SHALL 确保 RBAC 相关表已正确创建

#### Scenario: 执行初始化脚本
- **WHEN** 应用启动或首次部署
- **THEN** `V5__init_permissions.sql` 脚本应被执行

## 修复计划

1. 修改 `SecurityConfig.java`，配置 `StrictHttpFirewall` 允许双斜杠
2. 执行 `V5__init_permissions.sql` 创建所需表结构
3. 验证 `/system/user/list` 接口正常访问
