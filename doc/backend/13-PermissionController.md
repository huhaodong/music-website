# 权限管理 (PermissionController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /system/permission/add | 添加权限 | 否 |
| PUT | /system/permission/update | 更新权限 | 否 |
| DELETE | /system/permission/delete | 删除权限 | 否 |
| GET | /system/permission/detail | 权限详情 | 否 |
| GET | /system/permission/list | 权限列表 | 否 |
| GET | /system/permission/tree | 权限树 | 否 |
| GET | /system/permission/listByRole | 角色权限 | 否 |
| POST | /system/permission/assign | 分配权限 | 否 |

---

## 添加权限

添加新的权限信息。

- **URL**: `/system/permission/add`
- **Method**: POST
- **Description**: 添加权限
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "name": "string",
  "code": "string",
  "type": "string",
  "url": "string",
  "method": "string",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 权限ID |
| name | string | 是 | 权限名称 |
| code | string | 是 | 权限代码 |
| type | string | 否 | 权限类型 |
| url | string | 否 | 权限URL |
| method | string | 否 | 请求方法 (GET/POST/PUT/DELETE) |
| parentId | int | 否 | 父权限ID |
| sort | int | 否 | 排序号 |
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

## 更新权限

更新权限信息。

- **URL**: `/system/permission/update`
- **Method**: PUT
- **Description**: 更新权限
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "name": "string",
  "code": "string",
  "type": "string",
  "url": "string",
  "method": "string",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 权限ID |
| name | string | 否 | 权限名称 |
| code | string | 否 | 权限代码 |
| type | string | 否 | 权限类型 |
| url | string | 否 | 权限URL |
| method | string | 否 | 请求方法 |
| parentId | int | 否 | 父权限ID |
| sort | int | 否 | 排序号 |
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

## 删除权限

根据权限ID删除权限。

- **URL**: `/system/permission/delete`
- **Method**: DELETE
- **Description**: 删除权限
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 权限ID |

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

## 权限详情

根据权限ID获取权限详细信息。

- **URL**: `/system/permission/detail`
- **Method**: GET
- **Description**: 权限详情
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 权限ID |

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
    "name": "用户管理",
    "code": "system:user",
    "type": "menu",
    "url": "/system/user",
    "method": null,
    "parentId": 0,
    "sort": 1,
    "status": 1
  }
}
```

---

## 权限列表

获取所有权限列表。

- **URL**: `/system/permission/list`
- **Method**: GET
- **Description**: 权限列表
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
      "name": "用户管理",
      "code": "system:user",
      "type": "menu",
      "url": "/system/user",
      "method": null,
      "parentId": 0,
      "sort": 1,
      "status": 1
    }
  ]
}
```

---

## 权限树

获取权限的树形结构。

- **URL**: `/system/permission/tree`
- **Method**: GET
- **Description**: 权限树
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
      "name": "系统管理",
      "code": "system",
      "type": "menu",
      "url": "/system",
      "method": null,
      "parentId": 0,
      "sort": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "name": "用户管理",
          "code": "system:user",
          "type": "menu",
          "url": "/system/user",
          "method": null,
          "parentId": 1,
          "sort": 1,
          "status": 1,
          "children": []
        }
      ]
    }
  ]
}
```

---

## 角色权限

获取指定角色的所有权限。

- **URL**: `/system/permission/listByRole`
- **Method**: GET
- **Description**: 角色权限
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
      "name": "用户管理",
      "code": "system:user",
      "type": "menu",
      "url": "/system/user",
      "method": null,
      "parentId": 0,
      "sort": 1,
      "status": 1
    }
  ]
}
```

---

## 分配权限

为角色分配权限。

- **URL**: `/system/permission/assign`
- **Method**: POST
- **Description**: 分配权限
- **权限注解**: 无

### Request Body

```json
{
  "roleId": 1,
  "permissionIds": [1, 2, 3]
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | int | 是 | 角色ID |
| permissionIds | List<Integer> | 是 | 权限ID列表 |

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
