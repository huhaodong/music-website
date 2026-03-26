# 后端接口文档生成 Spec

## Why

当前项目缺少完整的 API 接口文档，不利于前后端协作开发和接口维护。需要生成标准化、可读的接口文档。

## What Changes

- 生成 music-server 后端所有 REST API 接口文档
- 按模块分类组织接口
- 包含请求/响应格式、参数说明、权限注解等信息
- 文档输出至 docs/backend 文件夹

## Impact

- Affected specs: RBAC 权限系统、组织用户管理
- Affected code: 所有 Controller 层接口

## ADDED Requirements

### Requirement: 后端接口文档生成

系统 SHALL 提供完整的后端 REST API 接口文档

#### Scenario: 认证模块
- **WHEN** 前端调用登录接口 `/auth/login`
- **THEN** 返回 accessToken、refreshToken、userInfo
- **Request**: `{"username": "string", "password": "string", "userType": "consumer|admin"}`
- **Response**: `{"code": 200, "message": "登录成功", "data": {"accessToken": "string", "refreshToken": "string", "user": {...}}}`

#### Scenario: 用户管理模块
- **WHEN** 调用 `/system/user/list`
- **THEN** 返回所有用户列表（需权限）
- **Requires Permission**: `system:user` 或 `user:list`

#### Scenario: 歌曲管理模块
- **WHEN** 调用 `/song/add`
- **THEN** 添加歌曲并返回结果
- **Request**: `SongRequest` + lrcfile + mpfile

#### Scenario: 收藏管理模块
- **WHEN** 调用 `/collection/add`
- **THEN** 添加用户收藏记录

#### Scenario: 评论管理模块
- **WHEN** 调用 `/comment/song/detail?songId=1`
- **THEN** 返回指定歌曲的评论列表

#### Scenario: 权限管理模块
- **WHEN** 调用 `/system/permission/tree`
- **THEN** 返回权限树形结构

#### Scenario: 角色管理模块
- **WHEN** 调用 `/system/role/list`
- **THEN** 返回所有角色列表

#### Scenario: 组织管理模块
- **WHEN** 调用 `/system/organization/tree`
- **THEN** 返回组织机构树形结构

## MODIFIED Requirements

无

## REMOVED Requirements

无
