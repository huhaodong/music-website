# 评论管理 (CommentController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /comment/add | 添加评论 | 否 |
| GET | /comment/delete | 删除评论 | 否 |
| GET | /comment/song/detail | 歌曲评论 | 否 |
| GET | /comment/songList/detail | 歌单评论 | 否 |
| POST | /comment/like | 点赞评论 | 否 |

---

## 添加评论

对歌曲或歌单发表评论。

- **URL**: `/comment/add`
- **Method**: POST
- **Description**: 添加评论
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "userId": 1,
  "songId": 1,
  "songListId": 1,
  "content": "string",
  "nowType": 0
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 评论ID |
| userId | int | 是 | 用户ID |
| songId | int | 否 | 歌曲ID（评论歌曲时必填） |
| songListId | int | 否 | 歌单ID（评论歌单时必填） |
| content | string | 是 | 评论内容 |
| nowType | byte | 否 | 类型：0-歌曲评论，1-歌单评论 |

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

## 删除评论

根据评论ID删除评论。

- **URL**: `/comment/delete`
- **Method**: GET
- **Description**: 删除评论
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 评论ID |

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

## 歌曲评论

获取指定歌曲的所有评论。

- **URL**: `/comment/song/detail`
- **Method**: GET
- **Description**: 歌曲评论
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| songId | int | 是 | 歌曲ID |

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
      "songId": 1,
      "songListId": null,
      "content": "很好听的歌曲！",
      "createTime": "2024-01-01",
      "nowType": 0,
      "up": 10
    }
  ]
}
```

---

## 歌单评论

获取指定歌单的所有评论。

- **URL**: `/comment/songList/detail`
- **Method**: GET
- **Description**: 歌单评论
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| songListId | int | 是 | 歌单ID |

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
      "songId": null,
      "songListId": 1,
      "content": "很棒的歌单！",
      "createTime": "2024-01-01",
      "nowType": 1,
      "up": 5
    }
  ]
}
```

---

## 点赞评论

对评论进行点赞。

- **URL**: `/comment/like`
- **Method**: POST
- **Description**: 点赞评论
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "up": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 评论ID |
| up | int | 是 | 点赞数量 |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "点赞成功",
  "type": "success",
  "success": true,
  "data": null
}
```
