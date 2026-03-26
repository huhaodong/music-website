# 组织管理 (OrganizationController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /system/organization/add | 添加组织 | 否 |
| PUT | /system/organization/update | 更新组织 | 否 |
| DELETE | /system/organization/delete | 删除组织 | 否 |
| GET | /system/organization/detail | 组织详情 | 否 |
| GET | /system/organization/list | 组织列表 | 否 |
| GET | /system/organization/tree | 组织树 | 否 |
| GET | /system/organization/children | 子组织 | 否 |

---

## 添加组织

添加新组织。

- **URL**: `/system/organization/add`
- **Method**: POST
- **Description**: 添加组织
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "name": "string",
  "parentId": 0,
  "level": 1,
  "path": "string",
  "sort": 1,
  "status": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 组织ID |
| name | string | 是 | 组织名称 |
| parentId | int | 否 | 父组织ID |
| level | int | 否 | 层级 |
| path | string | 否 | 路径 |
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

## 更新组织

更新组织信息。

- **URL**: `/system/organization/update`
- **Method**: PUT
- **Description**: 更新组织
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "name": "string",
  "parentId": 0,
  "level": 1,
  "path": "string",
  "sort": 1,
  "status": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 组织ID |
| name | string | 否 | 组织名称 |
| parentId | int | 否 | 父组织ID |
| level | int | 否 | 层级 |
| path | string | 否 | 路径 |
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

## 删除组织

根据组织ID删除组织。

- **URL**: `/system/organization/delete`
- **Method**: DELETE
- **Description**: 删除组织
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 组织ID |

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

## 组织详情

根据组织ID获取组织详细信息。

- **URL**: `/system/organization/detail`
- **Method**: GET
- **Description**: 组织详情
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 组织ID |

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
    "name": "技术部",
    "parentId": 0,
    "level": 1,
    "path": "/1",
    "sort": 1,
    "status": 1
  }
}
```

---

## 组织列表

获取所有组织列表。

- **URL**: `/system/organization/list`
- **Method**: GET
- **Description**: 组织列表
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
      "name": "技术部",
      "parentId": 0,
      "level": 1,
      "path": "/1",
      "sort": 1,
      "status": 1
    }
  ]
}
```

---

## 组织树

获取组织的树形结构。

- **URL**: `/system/organization/tree`
- **Method**: GET
- **Description**: 组织树
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
      "name": "总公司",
      "parentId": 0,
      "level": 1,
      "path": "/1",
      "sort": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "name": "技术部",
          "parentId": 1,
          "level": 2,
          "path": "/1/2",
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

## 子组织

获取指定父组织的子组织列表。

- **URL**: `/system/organization/children`
- **Method**: GET
- **Description**: 子组织
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| parentId | int | 否 | 父组织ID（为空则获取顶级组织） |

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
      "name": "总公司",
      "parentId": 0,
      "level": 1,
      "path": "/1",
      "sort": 1,
      "status": 1
    }
  ]
}
```
