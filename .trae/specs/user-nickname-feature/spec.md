# 用户昵称功能规范

## Why
用户注册后没有昵称字段，个人页面只显示用户名（username），需要增加昵称（nickname）功能提升用户体验。

## What Changes
- 在 consumer 表中添加 nickname 字段
- 用户注册时，如果未设置昵称，默认生成 "默认用户" + 不重复编号
- 修改 Personal.vue 页面，展示昵称，用户名显示在昵称下方

## Impact
- 影响数据库表：consumer
- 影响后端：ConsumerRequest、ConsumerServiceImpl
- 影响前端：Personal.vue、SignUp.vue（可选）、api/index.ts

## ADDED Requirements
### Requirement: 用户昵称功能
系统 SHALL 支持用户设置昵称，如果未设置则自动生成默认昵称。

#### Scenario: 新用户注册后访问个人页面
- **WHEN** 新用户（未设置昵称）访问 /personal 页面
- **THEN** 显示 "默认用户001" 格式的昵称
- **AND** 昵称下方以小字显示实际用户名（如 test01）

#### Scenario: 用户设置昵称后
- **WHEN** 用户在个人资料页设置昵称为 "音乐达人"
- **THEN** 刷新页面后昵称显示为 "音乐达人"
- **AND** 用户名仍显示在昵称下方

### Requirement: 默认昵称生成规则
系统 SHALL 自动生成不重复的默认昵称。

#### Scenario: 生成默认昵称
- **WHEN** 用户注册时未提供昵称
- **THEN** 系统生成格式为 "默认用户" + 3位数字编号（如 001, 002...）
- **AND** 编号从数据库当前最大编号 +1 计算

## MODIFIED Requirements
### Requirement: 个人信息展示
Personal.vue 页面的用户信息展示区域修改为：
```
[昵称]                          <- 大字，醒目显示
@用户名                         <- 小字，灰色，显示实际username
```

## REMOVED Requirements
无

## Technical Details
### Database
- consumer 表添加 nickname 字段：`nickname VARCHAR(50) DEFAULT NULL`

### Backend Changes
1. ConsumerRequest 添加 nickname 字段
2. ConsumerServiceImpl.addUser() 处理默认昵称逻辑
3. ConsumerServiceImpl.updateUserMsg() 支持更新昵称

### Frontend Changes
1. api/index.ts - getUserOfId 可能需要返回 nickname
2. Personal.vue - 模板和逻辑修改展示昵称和用户名
