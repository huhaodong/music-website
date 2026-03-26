# 前台用户管理 (ConsumerController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /user/add | 用户注册 | 否 |
| POST | /user/login/status | 用户名登录 | 否 |
| POST | /user/email/status | 邮箱登录 | 否 |
| POST | /user/resetPassword | 重置密码 | 否 |
| GET | /user/sendVerificationCode | 发送验证码 | 否 |
| GET | /user | 获取所有用户 | 否 |
| GET | /user/detail | 获取用户详情 | 否 |
| GET | /user/delete | 删除用户 | 否 |
| POST | /user/update | 更新用户信息 | 否 |
| POST | /user/updatePassword | 更新密码 | 否 |
| POST | /user/avatar/update | 更新头像 | 否 |

---

## 用户注册

前台用户注册新账号。

- **URL**: `/user/add`
- **Method**: POST
- **Description**: 用户注册
- **权限注解**: 无

### Request Body

```json
{
  "username": "string",
  "password": "string",
  "sex": 0,
  "phoneNum": "string",
  "email": "string",
  "birth": "2024-01-01",
  "introduction": "string",
  "location": "string",
  "nickname": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| sex | byte | 否 | 性别：0-女，1-男，2-保密 |
| phoneNum | string | 否 | 手机号 |
| email | string | 否 | 邮箱 |
| birth | date | 否 | 生日 |
| introduction | string | 否 | 个人简介 |
| location | string | 否 | 所在地 |
| nickname | string | 否 | 昵称 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "注册成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 用户名登录

使用用户名和密码登录（前台）。

- **URL**: `/user/login/status`
- **Method**: POST
- **Description**: 用户名登录
- **权限注解**: 无

### Request Body

```json
{
  "username": "string",
  "password": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "登录成功",
  "type": "success",
  "success": true,
  "data": {
    "id": 1,
    "username": "user1",
    "nickname": "用户1"
  }
}
```

---

## 邮箱登录

使用邮箱和密码登录（前台）。

- **URL**: `/user/email/status`
- **Method**: POST
- **Description**: 邮箱登录
- **权限注解**: 无

### Request Body

```json
{
  "email": "string",
  "password": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | string | 是 | 邮箱 |
| password | string | 是 | 密码 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "登录成功",
  "type": "success",
  "success": true,
  "data": {
    "id": 1,
    "username": "user1",
    "email": "user@example.com"
  }
}
```

---

## 重置密码

通过邮箱验证码重置密码（忘记密码）。

- **URL**: `/user/resetPassword`
- **Method**: POST
- **Description**: 重置密码
- **权限注解**: 无

### Request Body

```json
{
  "email": "string",
  "code": "string",
  "password": "string",
  "confirmPassword": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | string | 是 | 邮箱 |
| code | string | 是 | 验证码 |
| password | string | 是 | 新密码 |
| confirmPassword | string | 是 | 确认密码 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "密码修改成功",
  "type": "success",
  "success": true,
  "data": null
}
```

**失败响应**：

```json
{
  "code": 200,
  "message": "用户不存在",
  "type": "error",
  "success": false,
  "data": null
}
```

或

```json
{
  "code": 200,
  "message": "验证码不存在或失效",
  "type": "error",
  "success": false,
  "data": null
}
```

---

## 发送验证码

向指定邮箱发送验证码，用于密码重置。

- **URL**: `/user/sendVerificationCode`
- **Method**: GET
- **Description**: 发送验证码
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | string | 是 | 邮箱地址 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "发送成功",
  "type": "success",
  "success": true,
  "data": null
}
```

**失败响应**：

```json
{
  "code": 200,
  "message": "用户不存在",
  "type": "error",
  "success": false,
  "data": null
}
```

---

## 获取所有用户

获取系统中所有前台用户列表。

- **URL**: `/user`
- **Method**: GET
- **Description**: 获取所有用户
- **权限注解**: 无

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "获取成功",
  "type": "success",
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "user1",
      "sex": 0,
      "phoneNum": "13800138000",
      "email": "user1@example.com",
      "birth": "2024-01-01",
      "introduction": "音乐爱好者",
      "location": "北京",
      "nickname": "用户1",
      "avator": "https://example.com/avatar.jpg"
    }
  ]
}
```

---

## 获取用户详情

根据用户ID获取用户详细信息。

- **URL**: `/user/detail`
- **Method**: GET
- **Description**: 获取用户详情
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 用户ID |

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
    "username": "user1",
    "sex": 0,
    "phoneNum": "13800138000",
    "email": "user1@example.com",
    "birth": "2024-01-01",
    "introduction": "音乐爱好者",
    "location": "北京",
    "nickname": "用户1",
    "avator": "https://example.com/avatar.jpg"
  }
}
```

---

## 删除用户

根据用户ID删除用户。

- **URL**: `/user/delete`
- **Method**: GET
- **Description**: 删除用户
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 用户ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "删除成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 更新用户信息

更新用户的基本信息。

- **URL**: `/user/update`
- **Method**: POST
- **Description**: 更新用户信息
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "username": "string",
  "sex": 0,
  "phoneNum": "string",
  "email": "string",
  "birth": "2024-01-01",
  "introduction": "string",
  "location": "string",
  "nickname": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 用户ID |
| username | string | 否 | 用户名 |
| sex | byte | 否 | 性别 |
| phoneNum | string | 否 | 手机号 |
| email | string | 否 | 邮箱 |
| birth | date | 否 | 生日 |
| introduction | string | 否 | 个人简介 |
| location | string | 否 | 所在地 |
| nickname | string | 否 | 昵称 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "修改成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 更新密码

更新用户密码。

- **URL**: `/user/updatePassword`
- **Method**: POST
- **Description**: 更新密码
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "oldPassword": "string",
  "password": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 用户ID |
| oldPassword | string | 是 | 旧密码 |
| password | string | 是 | 新密码 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "密码修改成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 更新头像

上传并更新用户头像。

- **URL**: `/user/avatar/update`
- **Method**: POST
- **Description**: 更新头像
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 头像文件 |
| id | int | 是 | 用户ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "头像上传成功",
  "type": "success",
  "success": true,
  "data": null
}
```
