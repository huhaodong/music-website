# 音乐网站项目技术文档

## 文档目录

本文档包含了音乐网站项目的完整技术细节，帮助开发者全面了解项目架构和实现，以便进行二次开发。

### 文档列表

| 序号 | 文档名称 | 说明 |
|------|----------|------|
| 01 | [项目概述](./01-项目概述.md) | 项目简介、技术栈概览、功能列表、启动方式 |
| 02 | [前端架构-client](./02-前端架构-client.md) | 前台客户端架构、路由设计、状态管理、API封装 |
| 03 | [前端架构-manage](./03-前端架构-manage.md) | 后台管理端架构、页面功能、事件总线 |
| 04 | [后端架构](./04-后端架构.md) | 服务端架构、分层设计、核心功能模块 |
| 05 | [数据库设计](./05-数据库设计.md) | ER图、表结构、字段说明、索引设计 |
| 06 | [API接口文档](./06-API接口文档.md) | 接口列表、请求参数、响应格式、调用示例 |
| 07 | [部署指南](./07-部署指南.md) | 环境搭建、本地部署、生产部署、Docker部署 |

## 项目简介

这是一个基于 **Vue 3 + Spring Boot + MySQL** 开发的全栈音乐网站系统，包含：

- **music-client**: 前台客户端 - 面向用户的音乐播放和交互界面
- **music-manage**: 后台管理端 - 面向管理员的内容管理系统
- **music-server**: 服务端 - 提供 RESTful API 服务的后端系统

## 技术栈

### 前端
- Vue 3.2.13 + TypeScript 4.5.5
- Vue Router 4.0.3 + Vuex 4.0.0
- Element Plus 2.0.4/2.1.8
- Axios 0.26.0 + Sass 1.32.7
- ECharts 5.3.2 (管理端)

### 后端
- Spring Boot 2.6.2
- Java 1.8
- MyBatis Plus 3.5.1
- MySQL 8.0.16
- Redis 5.0.8+
- MinIO 8.3.0

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Yin-Hongwei/music-website.git
cd music-website
```

### 2. 启动数据库和 Redis

```bash
# 启动 MySQL
mysql -u root -p < music-server/sql/tp_music.sql

# 启动 Redis
redis-server
```

### 3. 启动后端服务

```bash
cd music-server
mvn spring-boot:run
```

### 4. 启动前端项目

```bash
# 前台客户端
cd music-client
npm install
npm run serve

# 后台管理端
cd music-manage
npm install
npm run serve
```

## 功能特性

### 前台功能
- 音乐播放（支持歌词同步显示）
- 用户注册/登录（支持邮箱登录）
- 用户信息编辑、头像修改
- 歌曲、歌单搜索
- 歌单打分
- 歌单、歌曲评论
- 歌单列表、歌手列表分页显示
- 音乐收藏、下载
- 播放控制（拖动、音量控制）
- 忘记密码（邮箱验证码重置）

### 后台功能
- 用户管理
- 歌手管理
- 歌曲管理
- 歌单管理
- 评论管理
- 收藏管理
- 数据统计（ECharts 图表）

## 项目结构

```
music-website/
├── music-client/          # 前台客户端（Vue 3）
├── music-manage/          # 后台管理端（Vue 3）
├── music-server/          # 服务端（Spring Boot）
│   ├── sql/               # 数据库脚本
│   └── docker-server/     # Docker 配置
└── doc/                   # 项目文档
```

## 二次开发建议

### 前端开发
1. 熟悉 Vue 3 Composition API
2. 了解 TypeScript 类型系统
3. 掌握 Element Plus 组件使用
4. 阅读 [前端架构-client](./02-前端架构-client.md) 和 [前端架构-manage](./03-前端架构-manage.md)

### 后端开发
1. 熟悉 Spring Boot 框架
2. 了解 MyBatis Plus ORM 框架
3. 掌握 RESTful API 设计规范
4. 阅读 [后端架构](./04-后端架构.md) 和 [API接口文档](./06-API接口文档.md)

### 数据库开发
1. 了解现有表结构和关系
2. 遵循命名规范
3. 合理设计索引
4. 阅读 [数据库设计](./05-数据库设计.md)

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进项目。

## 许可证

本项目采用 CC BY-NC 4.0 许可证，仅供学习交流使用，禁止商业用途。

## 联系方式

如有问题，欢迎通过以下方式联系：
- GitHub Issues
- 项目 README 中的联系方式

---

**文档版本**: v1.0  
**更新日期**: 2024年
