# 前端架构文档 - music-manage（后台管理端）

## 1. 项目概述

music-manage 是音乐网站的后台管理系统，面向管理员提供内容管理、数据统计等功能。

## 2. 技术架构

### 2.1 核心技术栈

- **Vue 3.2.13**: 渐进式 JavaScript 框架
- **TypeScript 4.5.5**: 类型安全的 JavaScript 超集
- **Vue Router 4.0.3**: 官方路由管理器
- **Vuex 4.0.0**: 状态管理模式
- **Element Plus 2.1.8**: UI 组件库
- **Axios 0.26.1**: HTTP 客户端
- **ECharts 5.3.2**: 数据可视化图表库
- **Mitt 3.0.0**: 事件总线

### 2.2 项目结构

```
music-manage/
├── public/                  # 静态资源
├── src/
│   ├── api/                 # API 接口封装
│   │   ├── index.ts         # API 方法定义
│   │   └── request.ts       # Axios 请求配置
│   ├── assets/              # 资源文件
│   │   ├── css/             # 全局样式
│   │   ├── icons/           # 图标字体
│   │   └── images/          # 图片资源
│   ├── components/          # 公共组件
│   │   ├── dialog/          # 对话框组件
│   │   │   └── YinDelDialog.vue  # 删除确认对话框
│   │   └── layouts/         # 布局组件
│   │       ├── YinAside.vue # 侧边栏
│   │       ├── YinAudio.vue # 音频播放器
│   │       └── YinHeader.vue # 顶部导航
│   ├── enums/               # 枚举常量
│   │   ├── area.ts          # 地区枚举
│   │   ├── icon.ts          # 图标枚举
│   │   ├── index.ts         # 枚举入口
│   │   ├── music-name.ts    # 音乐相关枚举
│   │   └── router-name.ts   # 路由名称枚举
│   ├── mixins/              # 混入代码
│   │   └── mixin.ts         # 公共混入
│   ├── router/              # 路由配置
│   │   └── index.ts         # 路由定义
│   ├── store/               # Vuex 状态管理
│   │   └── index.ts         # Store 定义
│   ├── utils/               # 工具函数
│   │   ├── emitter.ts       # 事件总线
│   │   └── index.ts         # 通用工具
│   ├── views/               # 页面视图
│   │   ├── CollectPage.vue  # 收藏管理
│   │   ├── CommentPage.vue  # 评论管理
│   │   ├── ConsumerPage.vue # 用户管理
│   │   ├── Home.vue         # 管理首页
│   │   ├── InfoPage.vue     # 信息统计
│   │   ├── ListSongPage.vue # 歌单歌曲管理
│   │   ├── Login.vue        # 登录页
│   │   ├── SingerPage.vue   # 歌手管理
│   │   ├── SongListPage.vue # 歌单管理
│   │   └── SongPage.vue     # 歌曲管理
│   ├── App.vue              # 根组件
│   ├── main.ts              # 入口文件
│   └── shims-vue.d.ts       # TypeScript 声明
├── package.json             # 项目依赖
├── tsconfig.json            # TypeScript 配置
├── vue.config.js            # Vue CLI 配置
└── babel.config.js          # Babel 配置
```

## 3. 路由设计

### 3.1 路由表

| 路径 | 组件 | 说明 | 权限 |
|------|------|------|------|
| / | Login.vue | 登录页 | 公开 |
| /Home | Home.vue | 管理首页 | 需登录 |
| /Info | InfoPage.vue | 数据统计 | 需登录 |
| /song | SongPage.vue | 歌曲管理 | 需登录 |
| /singer | SingerPage.vue | 歌手管理 | 需登录 |
| /SongList | SongListPage.vue | 歌单管理 | 需登录 |
| /ListSong | ListSongPage.vue | 歌单歌曲管理 | 需登录 |
| /Comment | CommentPage.vue | 评论管理 | 需登录 |
| /Consumer | ConsumerPage.vue | 用户管理 | 需登录 |
| /Collect | CollectPage.vue | 收藏管理 | 需登录 |

### 3.2 路由结构

采用嵌套路由设计，所有管理页面都作为 Home.vue 的子路由：

```typescript
{
  path: '/Home',
  component: () => import('@/views/Home.vue'),
  children: [
    { path: '/Info', component: InfoPage.vue },
    { path: '/song', component: SongPage.vue },
    // ... 其他管理页面
  ]
}
```

## 4. 状态管理 (Vuex)

### 4.1 State 定义

```typescript
state: {
  userPic: "/img/avatorImages/user.jpg",  // 管理员头像
  isPlay: false,                          // 播放状态
  url: '',                                // 当前播放URL
  id: '',                                 // 当前播放ID
  breadcrumbList: []                      // 面包屑导航
}
```

### 4.2 Getters

- `userPic`: 获取管理员头像
- `isPlay`: 获取播放状态
- `url`: 获取当前播放URL
- `id`: 获取当前播放ID
- `breadcrumbList`: 获取面包屑列表

### 4.3 Mutations

- `setUserPic`: 设置管理员头像
- `setIsPlay`: 设置播放状态
- `setUrl`: 设置播放URL
- `setId`: 设置播放ID
- `setBreadcrumbList`: 设置面包屑导航

## 5. API 接口封装

### 5.1 接口分类

| 分类 | 接口数量 | 说明 |
|------|----------|------|
| 管理员 API | 1个 | 登录验证 |
| 用户 API | 3个 | 用户查询、删除等 |
| 收藏 API | 2个 | 收藏查询、删除等 |
| 评论 API | 3个 | 评论查询、删除等 |
| 歌手 API | 4个 | 歌手增删改查 |
| 歌曲 API | 8个 | 歌曲增删改查、文件上传 |
| 歌单 API | 4个 | 歌单增删改查 |
| 歌单歌曲 API | 3个 | 歌单歌曲关联管理 |

### 5.2 主要 API 列表

```typescript
// 管理员相关
getLoginStatus({username, password})   // 管理员登录

// 用户相关
getAllUser()                           // 获取所有用户
getUserOfId(id)                        // 获取指定用户
deleteUser(id)                         // 删除用户

// 收藏相关
getCollectionOfUser(userId)            // 获取用户收藏
deleteCollection(userId, songId)       // 删除收藏

// 评论相关
getCommentOfSongId(songId)             // 获取歌曲评论
getCommentOfSongListId(songListId)     // 获取歌单评论
deleteComment(id)                      // 删除评论

// 歌手相关
getAllSinger()                         // 获取所有歌手
setSinger({...})                       // 添加歌手
updateSingerMsg({...})                 // 更新歌手信息
deleteSinger(id)                       // 删除歌手

// 歌曲相关
getAllSong()                           // 获取所有歌曲
getSongOfSingerId(id)                  // 获取歌手歌曲
getSongOfId(id)                        // 获取歌曲详情
getSongOfSingerName(name)              // 按歌手名搜索
updateSongMsg({...})                   // 更新歌曲信息
updateSongUrl(id)                      // 更新歌曲文件
updateSongImg(id)                      // 更新歌曲图片
updateSongLrc(id)                      // 更新歌词文件
deleteSong(id)                         // 删除歌曲

// 歌单相关
setSongList({...})                     // 添加歌单
getSongList()                          // 获取所有歌单
updateSongListMsg({...})               // 更新歌单信息
deleteSongList(id)                     // 删除歌单

// 歌单歌曲相关
setListSong({songId, songListId})      // 添加歌曲到歌单
getListSongOfSongId(songListId)        // 获取歌单内歌曲
deleteListSong(songId)                 // 从歌单移除歌曲
```

## 6. 组件架构

### 6.1 布局组件 (layouts)

| 组件名 | 功能说明 |
|--------|----------|
| YinAside.vue | 侧边栏导航菜单 |
| YinHeader.vue | 顶部导航栏 |
| YinAudio.vue | 音频预览播放器 |

### 6.2 对话框组件 (dialog)

| 组件名 | 功能说明 |
|--------|----------|
| YinDelDialog.vue | 删除确认对话框 |

## 7. 页面功能详解

### 7.1 登录页 (Login.vue)

- 管理员账号密码登录
- 登录状态验证
- 登录成功后跳转到管理首页

### 7.2 管理首页 (Home.vue)

- 侧边栏导航菜单
- 顶部用户信息展示
- 面包屑导航
- 子路由内容区域

### 7.3 数据统计 (InfoPage.vue)

- 使用 ECharts 展示数据统计图表
- 用户统计
- 歌曲统计
- 歌单统计
- 评论统计

### 7.4 用户管理 (ConsumerPage.vue)

- 用户列表展示
- 用户搜索
- 用户删除
- 查看用户收藏

### 7.5 歌手管理 (SingerPage.vue)

- 歌手列表展示
- 添加歌手
- 编辑歌手信息
- 删除歌手
- 歌手图片上传

### 7.6 歌曲管理 (SongPage.vue)

- 歌曲列表展示
- 添加歌曲（支持文件上传）
- 编辑歌曲信息
- 删除歌曲
- 歌曲文件管理（音频、图片、歌词）

### 7.7 歌单管理 (SongListPage.vue)

- 歌单列表展示
- 添加歌单
- 编辑歌单信息
- 删除歌单
- 歌单图片上传

### 7.8 歌单歌曲管理 (ListSongPage.vue)

- 歌单内歌曲列表
- 添加歌曲到歌单
- 从歌单移除歌曲

### 7.9 评论管理 (CommentPage.vue)

- 评论列表展示
- 按歌曲/歌单筛选评论
- 删除评论

### 7.10 收藏管理 (CollectPage.vue)

- 用户收藏列表
- 删除收藏记录

## 8. 事件总线 (Mitt)

使用 Mitt 作为事件总线，实现组件间通信：

```typescript
// emitter.ts
import mitt from 'mitt'
const emitter = mitt()
export default emitter
```

常用事件：
- `refresh`: 刷新列表数据
- `play`: 播放音频
- `delete`: 删除确认

## 9. 样式架构

### 9.1 样式文件

```
assets/css/
└── main.css                 # 全局样式
```

### 9.2 样式特点

- 使用原生 CSS
- Element Plus 主题定制
- 管理界面风格统一

## 10. 与 Client 端差异

| 特性 | music-client | music-manage |
|------|--------------|--------------|
| 目标用户 | 普通用户 | 管理员 |
| 主要功能 | 音乐播放、社交 | 内容管理、数据统计 |
| 图表库 | 无 | ECharts |
| 事件总线 | 无 | Mitt |
| 布局 | 顶部+底部播放栏 | 侧边栏+顶部 |
| 文件上传 | 头像上传 | 多类型文件上传 |
| 路由权限 | 部分需登录 | 全部需登录 |
