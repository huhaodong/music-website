# 点赞管理 (UserSupportController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /userSupport/test | 检查点赞 | 否 |
| POST | /userSupport/insert | 添加点赞 | 否 |
| POST | /userSupport/delete | 取消点赞 | 否 |

---

## 检查点赞

检查用户是否已点赞指定评论。

- **URL**: `/userSupport/test`
- **Method**: POST
- **Description**: 检查点赞
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "commentId": 1,
  "userId": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 记录ID |
| commentId | int | 是 | 评论ID |
| userId | int | 是 | 用户ID |

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

## 添加点赞

为评论添加用户点赞。

- **URL**: `/userSupport/insert`
- **Method**: POST
- **Description**: 添加点赞
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "commentId": 1,
  "userId": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 记录ID |
| commentId | int | 是 | 评论ID |
| userId | int | 是 | 用户ID |

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

---

## 取消点赞

取消用户对评论的点赞。

- **URL**: `/userSupport/delete`
- **Method**: POST
- **Description**: 取消点赞
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "commentId": 1,
  "userId": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否 | 记录ID |
| commentId | int | 是 | 评论ID |
| userId | int | 是 | 用户ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "取消成功",
  "type": "success",
  "success": true,
  "data": null
}
```
