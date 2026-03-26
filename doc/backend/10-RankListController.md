# 评分管理 (RankListController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /rankList/add | 提交评分 | 否 |
| GET | /rankList | 歌单评分 | 否 |
| GET | /rankList/user | 用户评分 | 否 |

---

## 提交评分

用户对歌单进行评分。

- **URL**: `/rankList/add`
- **Method**: POST
- **Description**: 提交评分
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "songListId": 1,
  "consumerId": 1,
  "score": 5
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 否 | 评分记录ID |
| songListId | long | 是 | 歌单ID |
| consumerId | long | 是 | 用户ID |
| score | int | 是 | 评分（1-5分） |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "评分成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 歌单评分

获取指定歌单的平均评分。

- **URL**: `/rankList`
- **Method**: GET
- **Description**: 歌单评分
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| songListId | long | 是 | 歌单ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "获取成功",
  "type": "success",
  "success": true,
  "data": 4.5
}
```

返回值为歌单的平均评分（浮点数）。

---

## 用户评分

获取指定用户对歌单的评分。

- **URL**: `/rankList/user`
- **Method**: GET
- **Description**: 用户评分
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| consumerId | long | 否 | 用户ID |
| songListId | long | 是 | 歌单ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "成功",
  "type": "success",
  "success": true,
  "data": {
    "id": 1,
    "songListId": 1,
    "consumerId": 1,
    "score": 5
  }
}
```

如果没有评分记录，`data` 可能为 `null`。
