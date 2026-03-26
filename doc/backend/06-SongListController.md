# 歌单管理 (SongListController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /songList/add | 添加歌单 | 否 |
| GET | /songList/delete | 删除歌单 | 否 |
| GET | /songList | 所有歌单 | 否 |
| GET | /songList/detail | 歌单详情 | 否 |
| GET | /songList/likeTitle/detail | 按标题搜索 | 否 |
| GET | /songList/style/detail | 按风格筛选 | 否 |
| POST | /songList/update | 更新歌单 | 否 |
| POST | /songList/img/update | 更新歌单图片 | 否 |

---

## 添加歌单

创建新歌单。

- **URL**: `/songList/add`
- **Method**: POST
- **Description**: 添加歌单
- **权限注解**: 无

### Request Body

```json
{
  "title": "string",
  "pic": "string",
  "style": "string",
  "introduction": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 歌单标题 |
| pic | string | 否 | 歌单封面URL |
| style | string | 否 | 风格/分类 |
| introduction | string | 否 | 歌单简介 |

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

## 删除歌单

根据歌单ID删除歌单。

- **URL**: `/songList/delete`
- **Method**: GET
- **Description**: 删除歌单
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌单ID |

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

## 所有歌单

获取所有歌单列表。

- **URL**: `/songList`
- **Method**: GET
- **Description**: 所有歌单
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
      "title": "我的收藏",
      "pic": "https://example.com/playlist.jpg",
      "style": "流行",
      "introduction": "我喜欢的歌曲集合"
    }
  ]
}
```

---

## 歌单详情

根据歌单ID获取歌单详细信息。

- **URL**: `/songList/detail`
- **Method**: GET
- **Description**: 歌单详情
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌单ID |

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
    "title": "我的收藏",
    "pic": "https://example.com/playlist.jpg",
    "style": "流行",
    "introduction": "我喜欢的歌曲集合"
  }
}
```

---

## 按标题搜索

根据歌单标题搜索歌单（支持模糊匹配）。

- **URL**: `/songList/likeTitle/detail`
- **Method**: GET
- **Description**: 按标题搜索
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 歌单标题 |

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
      "title": "我的收藏",
      "pic": "https://example.com/playlist.jpg",
      "style": "流行",
      "introduction": "我喜欢的歌曲集合"
    }
  ]
}
```

---

## 按风格筛选

根据歌单风格/分类筛选歌单。

- **URL**: `/songList/style/detail`
- **Method**: GET
- **Description**: 按风格筛选
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| style | string | 是 | 风格/分类 |

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
      "title": "我的收藏",
      "pic": "https://example.com/playlist.jpg",
      "style": "流行",
      "introduction": "我喜欢的歌曲集合"
    }
  ]
}
```

---

## 更新歌单

更新歌单信息。

- **URL**: `/songList/update`
- **Method**: POST
- **Description**: 更新歌单
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "title": "string",
  "pic": "string",
  "style": "string",
  "introduction": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌单ID |
| title | string | 否 | 歌单标题 |
| pic | string | 否 | 歌单封面URL |
| style | string | 否 | 风格/分类 |
| introduction | string | 否 | 歌单简介 |

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

## 更新歌单图片

上传并更新歌单封面图片。

- **URL**: `/songList/img/update`
- **Method**: POST
- **Description**: 更新歌单图片
- **权限注解**: 无
- **Content-Type**: `multipart/form-data`

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 图片文件 |
| id | int | 是 | 歌单ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "图片上传成功",
  "type": "success",
  "success": true,
  "data": null
}
```
