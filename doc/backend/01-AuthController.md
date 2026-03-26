# 认证模块 (AuthController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /auth/login | 用户登录 | 否 |
| POST | /auth/refresh | 刷新Token | 否 |
| POST | /auth/logout | 用户登出 | 是 |
| GET | /auth/info | 获取当前用户信息 | 是 |

---

## 用户登录

用户登录系统，获取访问令牌。

- **URL**: `/auth/login`
- **Method**: POST
- **Description**: 用户登录
- **权限注解**: 无

### Request Body

```json
{
  "username": "string",
  "password": "string",
  "userType": "consumer" | "admin"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| userType | string | 是 | 用户类型：consumer（前台用户）或 admin（管理员） |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "登录成功",
  "type": "success",
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "admin",
      "roles": "admin"
    }
  }
}
```

**失败响应**：

```json
{
  "code": 200,
  "message": "用户名或密码错误",
  "type": "error",
  "success": false,
  "data": null
}
```

---

## 刷新Token

使用刷新令牌获取新的访问令牌。

- **URL**: `/auth/refresh`
- **Method**: POST
- **Description**: 刷新Token
- **权限注解**: 无

### Request Body

```
refreshToken字符串
```

纯字符串类型的 refreshToken。

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "刷新成功",
  "type": "success",
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "admin",
      "roles": "admin"
    }
  }
}
```

---

## 用户登出

清除令牌，将 Token 加入黑名单。

- **URL**: `/auth/logout`
- **Method**: POST
- **Description**: 用户登出
- **权限注解**: 无

### Request Headers

```
Authorization: Bearer {token}
```

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "登出成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 获取当前用户信息

获取当前登录用户的基本信息。

- **URL**: `/auth/info`
- **Method**: GET
- **Description**: 获取当前用户信息
- **权限注解**: 无

### Request Headers

```
Authorization: Bearer {token}
```

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "获取成功",
  "type": "success",
  "success": true,
  "data": {
    "id": 1,
    "username": "admin",
    "roles": "admin"
  }
}
```

**失败响应**：

```json
{
  "code": 200,
  "message": "未授权",
  "type": "error",
  "success": false,
  "data": null
}
```
