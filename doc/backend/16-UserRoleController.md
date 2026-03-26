# 用户角色管理 (UserRoleController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /system/userRole/assign | 分配角色 | 否 |
| DELETE | /system/userRole/remove | 移除角色 | 否 |
| GET | /system/userRole/list | 用户角色列表 | 否 |
| GET | /system/userRole/usersByRole | 角色用户 | 否 |

---

## 分配角色

为用户分配角色。

- **URL**: `/system/userRole/assign`
- **Method**: POST
- **Description**: 分配角色
- **权限注解**: 无

### Request Body

```json
{
  "userId": 1,
  "userType": "consumer",
  "roleIds": [1, 2]
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户ID |
| userType | string | 是 | 用户类型：consumer（前台用户）或 admin（管理员） |
| roleIds | List<Integer> | 是 | 角色ID列表 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "分配成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 移除角色

从用户移除指定角色。

- **URL**: `/system/userRole/remove`
- **Method**: DELETE
- **Description**: 移除角色
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户ID |
| userType | string | 是 | 用户类型 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "移除成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 用户角色列表

获取指定用户的角色列表。

- **URL**: `/system/userRole/list`
- **Method**: GET
- **Description**: 用户角色列表
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户ID |
| userType | string | 是 | 用户类型 |

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
      "name": "管理员",
      "code": "admin",
      "description": "系统管理员角色",
      "status": 1
    }
  ]
}
```

---

## 角色用户

获取具有指定角色的所有用户。

- **URL**: `/system/userRole/usersByRole`
- **Method**: GET
- **Description**: 角色用户
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | int | 是 | 角色ID |

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
      "nickname": "管理员"
    }
  ]
}
```
