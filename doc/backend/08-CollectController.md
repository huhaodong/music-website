# 收藏管理 (CollectController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /collection/add | 添加收藏 | 否 |
| DELETE | /collection/delete | 取消收藏 | 否 |
| POST | /collection/status | 收藏状态 | 否 |
| GET | /collection/detail | 用户收藏列表 | 否 |

---

## 添加收藏

添加歌曲或歌单到用户收藏夹。

- **URL**: `/collection/add`
- **Method**: POST
- **Description**: 添加收藏
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "userId": 1,
  "type": 0,
  "songId": 1,
  "songListId": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 收藏ID |
| userId | int | 是 | 用户ID |
| type | byte | 否 | 收藏类型：0-歌曲，1-歌单 |
| songId | int | 否 | 歌曲ID（type=0时必填） |
| songListId | int | 否 | 歌单ID（type=1时必填） |

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

## 取消收藏

取消收藏的歌曲或歌单。

- **URL**: `/collection/delete`
- **Method**: DELETE
- **Description**: 取消收藏
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户ID |
| songId | int | 是 | 歌曲ID |

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

## 收藏状态

检查用户是否已收藏指定歌曲。

- **URL**: `/collection/status`
- **Method**: POST
- **Description**: 收藏状态
- **权限注解**: 无

### Request Body

```json
{
  "userId": 1,
  "songId": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户ID |
| songId | int | 是 | 歌曲ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "获取成功",
  "type": "success",
  "success": true,
  "data": true
}
```

或

```json
{
  "code": 200,
  "message": "获取成功",
  "type": "success",
  "success": true,
  "data": false
}
```

---

## 用户收藏列表

获取指定用户的所有收藏列表。

- **URL**: `/collection/detail`
- **Method**: GET
- **Description**: 用户收藏列表
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户ID |

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
      "userId": 1,
      "type": 0,
      "songId": 1,
      "songListId": null,
      "createTime": "2024-01-01"
    }
  ]
}
```
