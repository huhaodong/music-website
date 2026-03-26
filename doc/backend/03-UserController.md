# 后台用户管理 (UserController)

## f接口总览

| 方法   | 路径                            | 描述     | 权限                |
| ---- | ----------------------------- | ------ | ----------------- |
| POST | /system/user/add              | 添加用户   | RequirePermission |
| GET  | /system/user/list             | 用户列表   | RequirePermission |
| GET  | /system/user/detail           | 用户详情   | RequirePermission |
| GET  | /system/user/delete           | 删除用户   | RequirePermission |
| POST | /system/user/update           | 更新用户   | RequirePermission |
| POST | /system/user/updatePassword   | 更新密码   | RequirePermission |
| POST | /system/user/avatar/update    | 更新头像   | RequirePermission |
| POST | /system/user/batchDelete      | 批量删除   | RequirePermission |
| POST | /system/user/batchAssignRoles | 批量分配角色 | RequirePermission |

***

## 添加用户

后台添加新用户。

- **URL**: `/system/user/add`
- **Method**: POST
- **Description**: 添加用户
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:add"})`

### Request Headers

```
Authorization: Bearer {token}
```

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

| 参数           | 类型     | 必填 | 说明              |
| ------------ | ------ | -- | --------------- |
| username     | string | 是  | 用户名             |
| password     | string | 是  | 密码              |
| sex          | byte   | 否  | 性别：0-女，1-男，2-保密 |
| phoneNum     | string | 否  | 手机号             |
| email        | string | 否  | 邮箱              |
| birth        | date   | 否  | 生日              |
| introduction | string | 否  | 个人简介            |
| location     | string | 否  | 所在地             |
| nickname     | string | 否  | 昵称              |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "添加成功",
  "type": "success",
  "success": true,
  "data": null
}
```

***

## 用户列表

获取所有用户列表。

- **URL**: `/system/user/list`
- **Method**: GET
- **Description**: 用户列表
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:list"})`

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
  "data": [
    {
      "id": 1,
      "username": "admin",
      "sex": 1,
      "phoneNum": "13800138000",
      "email": "admin@example.com",
      "birth": "2024-01-01",
      "introduction": "管理员",
      "location": "北京",
      "nickname": "管理员"
    }
  ]
}
```

***

## 用户详情

根据用户ID获取用户详细信息。

- **URL**: `/system/user/detail`
- **Method**: GET
- **Description**: 用户详情
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:detail"})`

### Request Headers

```
Authorization: Bearer {token}
```

### Request Parameters

| 参数 | 类型  | 必填 | 说明   |
| -- | --- | -- | ---- |
| id | int | 是  | 用户ID |

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
    "sex": 1,
    "phoneNum": "13800138000",
    "email": "admin@example.com",
    "birth": "2024-01-01",
    "introduction": "管理员",
    "location": "北京",
    "nickname": "管理员"
  }
}
```

***

## 删除用户

根据用户ID删除用户。

- **URL**: `/system/user/delete`
- **Method**: GET
- **Description**: 删除用户
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:delete"})`

### Request Headers

```
Authorization: Bearer {token}
```

### Request Parameters

| 参数 | 类型  | 必填 | 说明   |
| -- | --- | -- | ---- |
| id | int | 是  | 用户ID |

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

***

## 更新用户

更新用户的基本信息。

- **URL**: `/system/user/update`
- **Method**: POST
- **Description**: 更新用户
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:update"})`

### Request Headers

```
Authorization: Bearer {token}
```

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

| 参数           | 类型     | 必填 | 说明   |
| ------------ | ------ | -- | ---- |
| id           | int    | 是  | 用户ID |
| username     | string | 否  | 用户名  |
| sex          | byte   | 否  | 性别   |
| phoneNum     | string | 否  | 手机号  |
| email        | string | 否  | 邮箱   |
| birth        | date   | 否  | 生日   |
| introduction | string | 否  | 个人简介 |
| location     | string | 否  | 所在地  |
| nickname     | string | 否  | 昵称   |

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

***

## 更新密码

更新用户密码。

- **URL**: `/system/user/updatePassword`
- **Method**: POST
- **Description**: 更新密码
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:updatePassword"})`

### Request Headers

```
Authorization: Bearer {token}
```

### Request Body

```json
{
  "id": 1,
  "oldPassword": "string",
  "password": "string"
}
```

**参数说明**：

| 参数          | 类型     | 必填 | 说明   |
| ----------- | ------ | -- | ---- |
| id          | int    | 是  | 用户ID |
| oldPassword | string | 是  | 旧密码  |
| password    | string | 是  | 新密码  |

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

***

## 更新头像

上传并更新用户头像。

- **URL**: `/system/user/avatar/update`
- **Method**: POST
- **Description**: 更新头像
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:updateAvatar"})`

### Request Headers

```
Authorization: Bearer {token}
```

### Request Parameters

| 参数   | 类型   | 必填 | 说明   |
| ---- | ---- | -- | ---- |
| file | file | 是  | 头像文件 |
| id   | int  | 是  | 用户ID |

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

***

## 批量删除用户

批量删除多个用户。

- **URL**: `/system/user/batchDelete`
- **Method**: POST
- **Description**: 批量删除
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:batchDelete"})`

### Request Headers

```
Authorization: Bearer {token}
```

### Request Body

```json
[1, 2, 3]
```

用户ID数组。

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "批量删除成功",
  "type": "success",
  "success": true,
  "data": null
}
```

***

## 批量分配角色

为多个用户批量分配角色。

- **URL**: `/system/user/batchAssignRoles`
- **Method**: POST
- **Description**: 批量分配角色
- **权限注解**: `@RequirePermission(codes = {"system:user", "user:assignRoles"})`

### Request Headers

```
Authorization: Bearer {token}
```

### Request Parameters

| 参数       | 类型            | 必填 | 说明          |
| -------- | ------------- | -- | ----------- |
| userIds  | Integer\[]    | 是  | 用户ID数组      |
| userType | string        | 是  | 用户类型        |
| roleIds  | List<Integer> | 是  | 角色ID数组（请求体） |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "角色分配成功",
  "type": "success",
  "success": true,
  "data": null
}
```

