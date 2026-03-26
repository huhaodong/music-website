# 轮播图管理 (BannerController)

## 接口总览

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /banner/getAllBanner | 获取所有轮播图 | 否 |

---

## 获取所有轮播图

获取系统中配置的所有轮播图。

- **URL**: `/banner/getAllBanner`
- **Method**: GET
- **Description**: 获取所有轮播图
- **权限注解**: 无

### Response

**成功响应**：

```json
{
  "code": 200,
  "message": "成功获取轮播图与",
  "type": "success",
  "success": true,
  "data": [
    {
      "id": 1,
      "pic": "https://example.com/banner1.jpg",
      "link": "https://example.com",
      "createTime": "2024-01-01"
    }
  ]
}
```

**返回字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 轮播图ID |
| pic | string | 轮播图URL |
| link | string | 跳转链接 |
| createTime | date | 创建时间 |
