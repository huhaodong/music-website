# Music Server REST API 文档

## 概述

本文档描述了 Music Server 后端 REST API 接口。服务端点基于以下基础配置：

- **基础 URL**: `http://localhost:8888`
- **响应格式**: 统一使用 `R` 类封装
- **认证方式**: JWT Bearer Token

### 统一响应格式

所有 API 响应都遵循以下 JSON 格式：

```json
{
  "code": 200,
  "message": "string",
  "type": "success",
  "success": true,
  "data": object
}
```

**响应状态码说明**：
- `code: 200` - 成功
- `code: 500` - 服务器错误
- `success: true` - 操作成功
- `success: false` - 操作失败

**响应类型说明**：
- `success` - 成功
- `warning` - 警告
- `error` - 错误

---

## 目录

### 认证模块
1. [认证模块 (AuthController)](01-AuthController.md)

### 用户管理模块
2. [前台用户管理 (ConsumerController)](02-ConsumerController.md)
3. [后台用户管理 (UserController)](03-UserController.md)

### 业务模块
4. [歌手管理 (SingerController)](04-SingerController.md)
5. [歌曲管理 (SongController)](05-SongController.md)
6. [歌单管理 (SongListController)](06-SongListController.md)
7. [歌单歌曲管理 (ListSongController)](07-ListSongController.md)
8. [收藏管理 (CollectController)](08-CollectController.md)
9. [评论管理 (CommentController)](09-CommentController.md)
10. [评分管理 (RankListController)](10-RankListController.md)
11. [点赞管理 (UserSupportController)](11-UserSupportController.md)
12. [轮播图管理 (BannerController)](12-BannerController.md)

### 权限管理模块
13. [权限管理 (PermissionController)](13-PermissionController.md)
14. [角色管理 (RoleController)](14-RoleController.md)
15. [组织管理 (OrganizationController)](15-OrganizationController.md)
16. [用户角色管理 (UserRoleController)](16-UserRoleController.md)
17. [权限模板 (PermissionTemplateController)](17-PermissionTemplateController.md)

---

## 通用说明

### 请求头

需要认证的接口需要在请求头中包含：

```
Authorization: Bearer {token}
```

### 分页

当前版本未实现分页，所有列表接口返回完整数据。

### 文件上传

文件上传接口使用 `multipart/form-data` 格式，文件字段名为 `file`。

### 日期格式

日期字段使用 `yyyy-MM-dd` 或 `yyyy-MM-dd HH:mm:ss` 格式。

---

## 错误处理

当请求失败时，响应示例：

```json
{
  "code": 200,
  "message": "错误信息描述",
  "type": "error",
  "success": false,
  "data": null
}
```

当发生服务器异常时：

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "type": "error",
  "success": false,
  "data": null
}
```
