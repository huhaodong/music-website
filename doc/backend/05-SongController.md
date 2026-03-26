# 歌曲管理 (SongController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /song/add | 添加歌曲 | 否 |
| DELETE | /song/delete | 删除歌曲 | 否 |
| GET | /song | 所有歌曲 | 否 |
| GET | /song/detail | 歌曲详情 | 否 |
| GET | /song/singer/detail | 歌手歌曲 | 否 |
| GET | /song/singerName/detail | 按歌手名搜索 | 否 |
| POST | /song/update | 更新歌曲 | 否 |
| POST | /song/img/update | 更新歌曲图片 | 否 |
| POST | /song/url/update | 更新歌曲文件 | 否 |
| POST | /song/lrc/update | 更新歌词 | 否 |

---

## 添加歌曲

添加新歌曲，需要上传歌词文件和音频文件。

- **URL**: `/song/add`
- **Method**: POST
- **Description**: 添加歌曲
- **权限注解**: 无
- **Content-Type**: `multipart/form-data`

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| lrcfile | file | 是 | 歌词文件 |
| file | file | 是 | 音频文件 (mp3) |
| singerId | int | 是 | 歌手ID |
| name | string | 是 | 歌曲名称 |
| introduction | string | 否 | 简介 |
| lyric | string | 否 | 歌词内容 |
| pic | string | 否 | 歌曲封面URL |
| createTime | date | 否 | 创建时间 |

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

## 删除歌曲

根据歌曲ID删除歌曲。

- **URL**: `/song/delete`
- **Method**: DELETE
- **Description**: 删除歌曲
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌曲ID |

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

## 所有歌曲

获取所有歌曲列表。

- **URL**: `/song`
- **Method**: GET
- **Description**: 所有歌曲
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
      "singerId": 1,
      "name": "晴天",
      "introduction": "周杰伦演唱的歌曲",
      "createTime": "2024-01-01",
      "updateTime": "2024-01-01",
      "pic": "https://example.com/song.jpg",
      "lyric": "[00:00.00]歌词内容...",
      "url": "https://example.com/song.mp3"
    }
  ]
}
```

---

## 歌曲详情

根据歌曲ID获取歌曲详细信息。

- **URL**: `/song/detail`
- **Method**: GET
- **Description**: 歌曲详情
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌曲ID |

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
    "singerId": 1,
    "name": "晴天",
    "introduction": "周杰伦演唱的歌曲",
    "createTime": "2024-01-01",
    "updateTime": "2024-01-01",
    "pic": "https://example.com/song.jpg",
    "lyric": "[00:00.00]歌词内容...",
    "url": "https://example.com/song.mp3"
  }
}
```

---

## 歌手歌曲

根据歌手ID获取该歌手的所有歌曲。

- **URL**: `/song/singer/detail`
- **Method**: GET
- **Description**: 歌手歌曲
- **权限注解**: 无

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| singerId | int | 是 | 歌手ID |

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
      "singerId": 1,
      "name": "晴天",
      "introduction": "周杰伦演唱的歌曲",
      "pic": "https://example.com/song.jpg",
      "url": "https://example.com/song.mp3"
    }
  ]
}
```

---

## 按歌手名搜索

根据歌手名称搜索歌曲（支持模糊匹配）。

- **URL**: `/song/singerName/detail`
- **Method**: GET
- **Description**: 按歌手名搜索
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
      "singerId": 1,
      "name": "晴天",
      "introduction": "周杰伦演唱的歌曲",
      "pic": "https://example.com/song.jpg",
      "url": "https://example.com/song.mp3"
    }
  ]
}
```

---

## 更新歌曲

更新歌曲信息。

- **URL**: `/song/update`
- **Method**: POST
- **Description**: 更新歌曲
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "singerId": 1,
  "name": "string",
  "introduction": "string",
  "pic": "string",
  "lyric": "string"
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 歌曲ID |
| singerId | int | 否 | 歌手ID |
| name | string | 否 | 歌曲名称 |
| introduction | string | 否 | 简介 |
| pic | string | 否 | 歌曲封面URL |
| lyric | string | 否 | 歌词内容 |

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

## 更新歌曲图片

上传并更新歌曲封面图片。

- **URL**: `/song/img/update`
- **Method**: POST
- **Description**: 更新歌曲图片
- **权限注解**: 无
- **Content-Type**: `multipart/form-data`

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 图片文件 |
| id | int | 是 | 歌曲ID |

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

---

## 更新歌曲文件

上传并更新歌曲音频文件。

- **URL**: `/song/url/update`
- **Method**: POST
- **Description**: 更新歌曲文件
- **权限注解**: 无
- **Content-Type**: `multipart/form-data`

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 音频文件 (mp3) |
| id | int | 是 | 歌曲ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "歌曲上传成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 更新歌词

上传并更新歌曲歌词文件。

- **URL**: `/song/lrc/update`
- **Method**: POST
- **Description**: 更新歌词
- **权限注解**: 无
- **Content-Type**: `multipart/form-data`

### Request Parameters

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 歌词文件 (.lrc) |
| id | int | 是 | 歌曲ID |

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "歌词上传成功",
  "type": "success",
  "success": true,
  "data": null
}
```
