# RBAC 页面布局统一样式 Spec

## Why
当前用户管理、组织管理、角色管理、权限管理页面作为独立路由显示，与歌曲管理、歌单管理等页面布局不一致。需要将这些页面统一到 Home 布局内显示，保持视觉和交互一致性。

## What Changes
- 将 RBAC 路由从独立路由改回 Home 的子路由
- 为 RBAC 页面添加统一的布局结构（面包屑、容器、操作栏）
- 保持路由路径不变，确保现有菜单链接仍然有效

## Impact
- 受影响的功能：用户管理、组织管理、角色管理、权限管理页面的显示布局
- 受影响代码：
  - `music-manage/src/router/index.ts` - 路由配置
  - `music-manage/src/views/UserManage.vue` - 用户管理页面
  - `music-manage/src/views/OrgManage.vue` - 组织管理页面
  - `music-manage/src/views/RoleManage.vue` - 角色管理页面
  - `music-manage/src/views/PermissionManage.vue` - 权限管理页面

## ADDED Requirements
### Requirement: 统一页面布局
RBAC 页面必须采用与歌曲管理、歌单管理相同的布局结构。

#### Scenario: 用户管理页面显示
- **WHEN** 用户访问 `/Home/user` 路由
- **THEN** 页面应在 Home 布局内显示
- **AND** 包含面包屑导航 `首页 / 系统管理 / 用户管理`
- **AND** 包含 `.container` 容器和 `.handle-box` 操作栏

#### Scenario: 组织管理页面显示
- **WHEN** 用户访问 `/Home/org` 路由
- **THEN** 页面应在 Home 布局内显示
- **AND** 包含面包屑导航 `首页 / 系统管理 / 组织管理`

#### Scenario: 角色管理页面显示
- **WHEN** 用户访问 `/Home/role` 路由
- **THEN** 页面应在 Home 布局内显示
- **AND** 包含面包屑导航 `首页 / 系统管理 / 角色管理`

#### Scenario: 权限管理页面显示
- **WHEN** 用户访问 `/Home/permission` 路由
- **THEN** 页面应在 Home 布局内显示
- **AND** 包含面包屑导航 `首页 / 系统管理 / 权限管理`

## MODIFIED Requirements
### Requirement: 路由配置修改
RBAC 路由必须作为 Home 的子路由存在。

- **原路由**: `/system/user` (独立路由)
- **新路由**: `/Home/user` (Home 子路由)
- **菜单链接**: 需要更新为 `/Home/user`

### Requirement: 面包屑导航
RBAC 页面需要添加统一的 `<el-breadcrumb>` 面包屑组件。

面包屑结构：`首页 / 系统管理 / [当前页面名称]`

### Requirement: 容器结构
RBAC 页面需要添加统一的容器结构：

```html
<el-breadcrumb class="crumbs" separator="/">
  <el-breadcrumb-item to="/Home">首页</el-breadcrumb-item>
  <el-breadcrumb-item>系统管理</el-breadcrumb-item>
  <el-breadcrumb-item>用户管理</el-breadcrumb-item>
</el-breadcrumb>

<div class="container">
  <div class="handle-box">
    <!-- 操作按钮区域 -->
  </div>
  <!-- 表格和分页 -->
</div>
```
