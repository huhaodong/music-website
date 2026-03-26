# 权限模板 (PermissionTemplateController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /system/permissionTemplate/list | 模板列表 | 否 |
| GET | /system/permissionTemplate/getByRole | 角色模板 | 否 |
| POST | /system/permissionTemplate/apply | 应用模板 | 否 |

---

## 模板列表

获取所有权限模板列表。

- **URL**: `/system/permissionTemplate/list`
- **Method**: GET
- **Description**: 模板列表
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
      "name": "管理员模板",
      "description": "包含所有权限的模板",
      "permissions": ["system:user", "system:role", "system:permission"]
    }
  ]
}
```

---

## 角色模板

根据角色代码获取对应的权限模板。

- **URL**: `/system/permissionTemplate/getByRole`
- **Method**: GET
- **Description**: 角色模板
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleCode | string | 是 | 角色代码 |

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
    "name": "管理员模板",
    "description": "包含所有权限的模板",
    "permissions": ["system:user", "system:role", "system:permission"]
  }
}
```

---

## 应用模板

将权限模板应用到指定角色。

- **URL**: `/system/permissionTemplate/apply`
- **Method**: POST
- **Description**: 应用模板
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| templateName | string | 是 | 模板名称 |
| roleId | int | 是 | 角色ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "应用成功",
  "type": "success",
  "success": true,
  "data": null
}
```
