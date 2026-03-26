# 歌手管理 (SingerController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /singer/add | 添加歌手 | 否 |
| DELETE | /singer/delete | 删除歌手 | 否 |
| GET | /singer | 所有歌手 | 否 |
| GET | /singer/name/detail | 按名字查找 | 否 |
| GET | /singer/sex/detail | 按性别筛选 | 否 |
| POST | /singer/update | 更新歌手 | 否 |
| POST | /singer/avatar/update | 更新头像 | 否 |

---

## 添加歌手

添加新歌手。

- **URL**: `/singer/add`
- **Method**: POST
- **Description**: 添加歌手
- **权限注解**: 无

### Request Body

```json
{
  "name": "string",
  "sex": 0,
  "pic": "string",
  "birth": "2024-01-01",
  "location": "string",
  "introduction": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 歌手名称 |
| sex | byte | 否 | 性别：0-女，1-男 |
| pic | string | 否 | 头像URL |
| birth | date | 否 | 出生日期 |
| location | string | 否 | 所在地 |
| introduction | string | 否 | 简介 |

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

## 删除歌手

根据歌手ID删除歌手。

- **URL**: `/singer/delete`
- **Method**: DELETE
- **Description**: 删除歌手
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌手ID |

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

## 所有歌手

获取所有歌手列表。

- **URL**: `/singer`
- **Method**: GET
- **Description**: 所有歌手
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
      "name": "周杰伦",
      "sex": 1,
      "pic": "https://example.com/singer.jpg",
      "birth": "2024-01-01",
      "location": "台湾",
      "introduction": "华语流行歌手"
    }
  ]
}
```

---

## 按名字查找

根据歌手名称查找歌手。

- **URL**: `/singer/name/detail`
- **Method**: GET
- **Description**: 按名字查找
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 歌手名称 |

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
      "name": "周杰伦",
      "sex": 1,
      "pic": "https://example.com/singer.jpg",
      "birth": "2024-01-01",
      "location": "台湾",
      "introduction": "华语流行歌手"
    }
  ]
}
```

---

## 按性别筛选

根据歌手性别筛选歌手。

- **URL**: `/singer/sex/detail`
- **Method**: GET
- **Description**: 按性别筛选
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sex | int | 是 | 性别：0-女，1-男 |

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
      "name": "周杰伦",
      "sex": 1,
      "pic": "https://example.com/singer.jpg",
      "birth": "2024-01-01",
      "location": "台湾",
      "introduction": "华语流行歌手"
    }
  ]
}
```

---

## 更新歌手

更新歌手信息。

- **URL**: `/singer/update`
- **Method**: POST
- **Description**: 更新歌手
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "name": "string",
  "sex": 0,
  "pic": "string",
  "birth": "2024-01-01",
  "location": "string",
  "introduction": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌手ID |
| name | string | 否 | 歌手名称 |
| sex | byte | 否 | 性别 |
| pic | string | 否 | 头像URL |
| birth | date | 否 | 出生日期 |
| location | string | 否 | 所在地 |
| introduction | string | 否 | 简介 |

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

## 更新头像

上传并更新歌手头像。

- **URL**: `/singer/avatar/update`
- **Method**: POST
- **Description**: 更新头像
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 头像文件 |
| id | int | 是 | 歌手ID |

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
