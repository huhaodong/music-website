# 前端架构文档 - music-client（前台客户端）

## 1. 项目概述

music-client 是音乐网站的前台客户端，面向普通用户提供音乐播放、搜索、收藏、评论等功能。

## 2. 技术架构

### 2.1 核心技术栈

- **Vue 3.2.13**: 渐进式 JavaScript 框架
- **TypeScript 4.5.5**: 类型安全的 JavaScript 超集
- **Vue Router 4.0.3**: 官方路由管理器
- **Vuex 4.0.0**: 状态管理模式
- **Element Plus 2.0.4**: UI 组件库
- **Axios 0.26.0**: HTTP 客户端
- **Sass 1.32.7**: CSS 预处理器

### 2.2 项目结构

```
music-client/
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
│   │   ├── layouts/         # 布局组件
│   │   ├── Comment.vue      # 评论组件
│   │   ├── PlayList.vue     # 播放列表组件
│   │   └── SongList.vue     # 歌曲列表组件
│   ├── enums/               # 枚举常量
│   │   ├── area.ts          # 地区枚举
│   │   ├── icon.ts          # 图标枚举
│   │   ├── music-name.ts    # 音乐相关枚举
│   │   ├── nav.ts           # 导航枚举
│   │   ├── router-name.ts   # 路由名称枚举
│   │   ├── singer.ts        # 歌手枚举
│   │   ├── songList.ts      # 歌单枚举
│   │   └── validate.ts      # 验证规则枚举
│   ├── mixins/              # 混入代码
│   │   └── mixin.ts         # 公共混入
│   ├── router/              # 路由配置
│   │   └── index.ts         # 路由定义
│   ├── store/               # Vuex 状态管理
│   │   ├── index.ts         # Store 入口
│   │   ├── configure.ts     # 全局配置状态
│   │   ├── song.ts          # 歌曲播放状态
│   │   └── user.ts          # 用户状态
│   ├── utils/               # 工具函数
│   │   └── index.ts         # 通用工具
│   ├── views/               # 页面视图
│   │   ├── error/           # 错误页面
│   │   ├── personal/        # 个人中心
│   │   ├── search/          # 搜索页面
│   │   ├── setting/         # 设置页面
│   │   ├── singer/          # 歌手页面
│   │   ├── song-sheet/      # 歌单页面
│   │   ├── FPassword.vue    # 忘记密码
│   │   ├── Home.vue         # 首页
│   │   ├── Lyric.vue        # 歌词页面
│   │   ├── SignIn.vue       # 登录页
│   │   ├── SignUp.vue       # 注册页
│   │   ├── loginByemail.vue # 邮箱登录
│   │   └── YinContainer.vue # 主容器
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

| 路径 | 名称 | 组件 | 说明 | 权限 |
|------|------|------|------|------|
| / | home | Home.vue | 首页 | 公开 |
| /sign-in | sign-in | SignIn.vue | 登录页 | 公开 |
| /sign-up | sign-up | SignUp.vue | 注册页 | 公开 |
| /loginByemail | loginByemail | loginByemail.vue | 邮箱登录 | 公开 |
| /FPassword | FPassword | FPassword.vue | 忘记密码 | 公开 |
| /personal | personal | Personal.vue | 个人主页 | 需登录 |
| /personal-data | personal-data | PersonalData.vue | 个人资料 | 公开 |
| /setting | setting | Setting.vue | 设置中心 | 需登录 |
| /setting/PersonalData | personalData | PersonalData.vue | 个人资料设置 | 需登录 |
| /song-sheet | song-sheet | SongSheet.vue | 歌单列表 | 公开 |
| /song-sheet-detail/:id | song-sheet-detail | SongSheetDetail.vue | 歌单详情 | 公开 |
| /singer | singer | Singer.vue | 歌手列表 | 公开 |
| /singer-detail/:id | singer-detail | SingerDetail.vue | 歌手详情 | 公开 |
| /lyric/:id | lyric | Lyric.vue | 歌词展示 | 公开 |
| /search | search | Search.vue | 搜索结果 | 公开 |
| /404 | - | 404.vue | 错误页面 | 公开 |

### 3.2 路由守卫

- 部分路由设置了 `meta.requireAuth: true`，需要用户登录后才能访问
- 未登录用户访问受保护路由会被拦截

## 4. 状态管理 (Vuex)

### 4.1 模块划分

#### configure 模块 - 全局配置

```typescript
state: {
  token: false,        // 用户登录状态
  showAside: false,    // 侧边栏显示状态
  searchWord: "",      // 搜索关键词
  activeNavName: "",   // 当前激活的导航
}
```

#### user 模块 - 用户信息

```typescript
state: {
  userId: "",          // 用户ID
  username: "",        // 用户名
  userPic: "",         // 用户头像
}
```

#### song 模块 - 播放状态

```typescript
state: {
  // 音乐信息
  songId: "",          // 音乐ID
  songTitle: "",       // 歌名
  songUrl: "",         // 音乐URL
  songPic: "",         // 歌曲图片
  singerName: "",      // 歌手名
  lyric: [],           // 歌词数据

  // 播放信息
  isPlay: false,       // 播放状态
  playBtnIcon: "",     // 播放按钮图标
  volume: 0,           // 音量
  duration: 0,         // 音乐时长
  curTime: 0,          // 当前播放位置
  changeTime: 0,       // 指定播放时刻
  autoNext: true,      // 自动播放下一首

  // 列表信息
  currentPlayList: [], // 当前播放列表
  songDetails: null,   // 歌单信息
  currentPlayIndex: -1,// 当前歌曲索引
}
```

## 5. API 接口封装

### 5.1 接口分类

| 分类 | 接口数量 | 说明 |
|------|----------|------|
| 用户 API | 8个 | 登录、注册、信息更新等 |
| 歌单 API | 4个 | 歌单查询、分类等 |
| 歌手 API | 2个 | 歌手列表、分类等 |
| 收藏 API | 5个 | 添加、删除、查询收藏等 |
| 评分 API | 3个 | 提交评分、获取评分等 |
| 评论 API | 5个 | 添加、删除、点赞评论等 |
| 歌曲 API | 5个 | 歌曲查询、下载等 |
| 点赞 API | 3个 | 评论点赞相关 |
| Banner API | 1个 | 轮播图获取 |

### 5.2 主要 API 列表

```typescript
// 用户相关
signIn({username, password})           // 用户名密码登录
signInByemail({email, password})       // 邮箱登录
SignUp({...})                          // 用户注册
updateUserMsg({...})                   // 更新用户信息
updateUserPassword({...})              // 更新密码
getUserOfId(id)                        // 获取用户信息
uploadUrl(userId)                      // 头像上传地址

// 歌单相关
getSongList()                          // 获取全部歌单
getSongListOfStyle(style)              // 按风格获取歌单
getSongListOfLikeTitle(keywords)       // 搜索歌单
getListSongOfSongId(songListId)        // 获取歌单内歌曲

// 歌手相关
getAllSinger()                         // 获取所有歌手
getSingerOfSex(sex)                    // 按性别筛选歌手

// 收藏相关
getCollectionOfUser(userId)            // 获取用户收藏
setCollection({userId, type, songId})  // 添加收藏
deleteCollection(userId, songId)       // 取消收藏
isCollection({userId, type, songId})   // 检查是否已收藏

// 评分相关
setRank({songListId, consumerId, score}) // 提交评分
getRankOfSongListId(songListId)        // 获取歌单评分
getUserRank(consumerId, songListId)    // 获取用户评分

// 评论相关
setComment({...})                      // 添加评论
deleteComment(id)                      // 删除评论
setSupport({id, up})                   // 点赞评论
getAllComment(type, id)                // 获取评论列表

// 歌曲相关
getSongOfId(id)                        // 获取歌曲详情
getSongOfSingerId(id)                  // 获取歌手歌曲
getSongOfSingerName(keywords)          // 按歌手名搜索
downloadMusic(url)                     // 下载音乐

// 点赞优化
testAlreadySupport({commentId, userId}) // 检查是否已点赞
deleteUserSupport({...})               // 取消点赞
insertUserSupport({...})               // 添加点赞

// Banner
getBannerList()                        // 获取轮播图
```

## 6. 组件架构

### 6.1 布局组件 (layouts)

| 组件名 | 功能说明 |
|--------|----------|
| YinHeader.vue | 顶部导航栏 |
| YinHeaderNav.vue | 导航菜单 |
| YinNav.vue | 侧边导航 |
| YinFooter.vue | 底部版权信息 |
| YinPlayBar.vue | 底部播放控制栏 |
| YinAudio.vue | 音频播放器核心 |
| YinCurrentPlay.vue | 当前播放列表 |
| YinIcon.vue | 图标组件 |
| YinLoginLogo.vue | 登录页 Logo |
| YinScrollTop.vue | 回到顶部按钮 |

### 6.2 业务组件

| 组件名 | 功能说明 |
|--------|----------|
| Comment.vue | 评论展示与提交 |
| PlayList.vue | 播放列表展示 |
| SongList.vue | 歌曲列表展示 |

## 7. 样式架构

### 7.1 样式文件组织

```
assets/css/
├── global.scss          # 全局样式
├── index.scss           # 入口样式
├── sign.scss            # 登录注册页样式
├── var.scss             # 变量定义
├── yin-current-play.scss # 当前播放列表样式
└── yin-play-bar.scss    # 播放栏样式
```

### 7.2 主要样式变量

- 使用 SCSS 预处理器
- 颜色、字体大小等通过变量统一管理
- 组件样式使用 scoped 限定作用域

## 8. 枚举常量

### 8.1 导航枚举 (nav.ts)

```typescript
enum NavName {
  Home = "首页",
  SongSheet = "歌单",
  Singer = "歌手",
  Personal = "个人主页",
  Setting = "设置",
  SignIn = "登录",
  SignUp = "注册",
  SignOut = "退出",
}
```

### 8.2 其他枚举

- **area.ts**: 地区枚举
- **singer.ts**: 歌手类型枚举
- **songList.ts**: 歌单风格枚举
- **validate.ts**: 表单验证规则
- **icon.ts**: 图标名称枚举

## 9. 工具函数

### 9.1 主要工具方法

```typescript
// 时间格式化
formatDate(date: Date, format: string): string

// 歌词解析
parseLyric(lyric: string): Array<{time: number, content: string}>

// 图片 URL 处理
attachImageUrl(url: string): string
```

## 10. 构建配置

### 10.1 Vue CLI 配置

```javascript
// vue.config.js
module.exports = {
  configureWebpack: {
    // 路径别名
    resolve: {
      alias: {
        '@': resolve('src')
      }
    }
  },
  devServer: {
    port: 8080,  // 开发服务器端口
    proxy: {
      // API 代理配置
    }
  }
}
```

### 10.2 TypeScript 配置

- 严格类型检查
- 路径映射配置
- Vue 单文件组件支持
