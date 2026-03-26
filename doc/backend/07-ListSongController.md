# 歌单歌曲管理 (ListSongController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /listSong/add | 添加歌曲到歌单 | 否 |
| GET | /listSong/delete | 从歌单删除歌曲 | 否 |
| GET | /listSong/detail | 歌单歌曲列表 | 否 |
| POST | /listSong/update | 更新歌单歌曲 | 否 |
| GET | /excle | 导出歌单 | 否 |

---

## 添加歌曲到歌单

将歌曲添加到指定歌单。

- **URL**: `/listSong/add`
- **Method**: POST
- **Description**: 添加歌曲到歌单
- **权限注解**: 无

### Request Body

```json
{
  "songId": 1,
  "songListId": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| songId | int | 是 | 歌曲ID |
| songListId | int | 是 | 歌单ID |

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

## 从歌单删除歌曲

从歌单中移除指定歌曲。

- **URL**: `/listSong/delete`
- **Method**: GET
- **Description**: 从歌单删除歌曲
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
  "message": "删除成功",
  "type": "success",
  "success": true,
  "data": null
}
```

---

## 歌单歌曲列表

获取指定歌单中的所有歌曲。

- **URL**: `/listSong/detail`
- **Method**: GET
- **Description**: 歌单歌曲列表
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
      "songId": 1,
      "songListId": 1
    }
  ]
}
```

---

## 更新歌单歌曲

更新歌单中歌曲的信息。

- **URL**: `/listSong/update`
- **Method**: POST
- **Description**: 更新歌单歌曲
- **权限注解**: 无

### Request Body

```json
{
  "id": 1,
  "songId": 1,
  "songListId": 1
}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 记录ID |
| songId | int | 否 | 歌曲ID |
| songListId | int | 否 | 歌单ID |

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

## 导出歌单

导出歌单为 Excel 文件。

- **URL**: `/excle`
- **Method**: GET
- **Description**: 导出歌单
- **权限注解**: 无

### Response

**成功响应**：

```
Content-Type: application/octet-stream
Content-Disposition: attachment; filename=SongList{timestamp}.xlsx

[Excel文件二进制流]
```
