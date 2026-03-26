# 角色管理 (RoleController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /system/role/add | 添加角色 | 否 |
| PUT | /system/role/update | 更新角色 | 否 |
| DELETE | /system/role/delete | 删除角色 | 否 |
| GET | /system/role/detail | 角色详情 | 否 |
| GET | /system/role/list | 角色列表 | 否 |
| GET | /system/role/listByStatus | 按状态查询 | 否 |

---

## 添加角色

添加新角色。

- **URL**: `/system/role/add`
- **Method**: POST
- **Description**: 添加角色
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "name": "string",
  "code": "string",
  "description": "string",
  "status": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 角色ID |
| name | string | 是 | 角色名称 |
| code | string | 是 | 角色代码 |
| description | string | 否 | 角色描述 |
| status | int | 否 | 状态：0-禁用，1-启用 |

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

---

## 更新角色

更新角色信息。

- **URL**: `/system/role/update`
- **Method**: PUT
- **Description**: 更新角色
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "name": "string",
  "code": "string",
  "description": "string",
  "status": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 角色ID |
| name | string | 否 | 角色名称 |
| code | string | 否 | 角色代码 |
| description | string | 否 | 角色描述 |
| status | int | 否 | 状态 |

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

## 删除角色

根据角色ID删除角色。

- **URL**: `/system/role/delete`
- **Method**: DELETE
- **Description**: 删除角色
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 角色ID |

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

## 角色详情

根据角色ID获取角色详细信息。

- **URL**: `/system/role/detail`
- **Method**: GET
- **Description**: 角色详情
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 角色ID |

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
    "name": "管理员",
    "code": "admin",
    "description": "系统管理员角色",
    "status": 1
  }
}
```

---

## 角色列表

获取所有角色列表。

- **URL**: `/system/role/list`
- **Method**: GET
- **Description**: 角色列表
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
      "name": "管理员",
      "code": "admin",
      "description": "系统管理员角色",
      "status": 1
    }
  ]
}
```

---

## 按状态查询

根据状态查询角色列表。

- **URL**: `/system/role/listByStatus`
- **Method**: GET
- **Description**: 按状态查询
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int | 否 | 状态：0-禁用，1-启用 |

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
