# M3: 艺术家管理与项目管理 Spec

## Why
M3 需要补齐曲库系统的核心业务实体，将 Singer 概念升级为 Artist，并引入 Project（歌曲项目）替代 SongList，实现项目全流程管理与状态机控制，同时保证历史数据可迁移且不丢失。

## What Changes
- 数据库：新增 artist、project、project_artist 表；扩展 song 表以支持 project_id 等字段；提供 Singer→Artist 数据迁移脚本与验证测试
- 后端：新增 Artist/Project 领域模型、CRUD 服务与 REST API；实现项目状态机与状态变更日志；实现项目与组织的数据隔离与权限控制
- 兼容：原 Singer 相关接口保持可用（旧接口适配/转发到新 Artist 能力）
- 前端：新增后台管理 Artist 管理页、Project 管理页与 Project 详情页，支持筛选/分页/状态展示与状态变更操作

## Impact
- Affected specs: 艺术家管理、项目管理、状态机、数据迁移、组织隔离与权限
- Affected code: music-server Flyway 迁移脚本、Artist/Project 模块（Entity/Mapper/Service/Controller）、权限拦截/数据权限、music-manage 管理端页面与组件测试

## ADDED Requirements

### Requirement: Singer→Artist 数据迁移
系统 SHALL 提供迁移脚本，将既有 Singer 数据迁移为 Artist，且迁移后原有歌曲相关数据不丢失。

#### Scenario: 迁移成功
- **WHEN** 执行数据库迁移（包含 Singer→Artist 脚本）
- **THEN** artist 表包含原 singer 的全部记录，且 id 保持一致，type 默认值为 singer，song 表与原 singer 关联映射正确

### Requirement: Artist 管理（CRUD + 类型筛选）
系统 SHALL 提供 Artist 的 CRUD 能力，并支持按类型筛选、关键字搜索与分页查询。

#### Scenario: 创建艺术家
- **WHEN** 创建艺术家并提供必要字段与类型（歌手/词作者/曲作者/制作人等）
- **THEN** 创建成功并可通过查询接口读取到

#### Scenario: 删除艺术家（有关联项目）
- **WHEN** 删除一个已与项目存在关联的艺术家
- **THEN** 拒绝直接删除并返回明确提示信息

### Requirement: Artist REST API
系统 SHALL 提供 Artist 的 REST API，并在无权限访问时返回 403。

#### Scenario: 无权限访问 Artist 列表
- **WHEN** 未具备所需权限的用户请求 GET /api/artist
- **THEN** 返回 403 Forbidden

### Requirement: Project 管理（CRUD + 列表 + 详情）
系统 SHALL 提供 Project 的创建/编辑/逻辑删除，以及分页/排序/筛选/关键字搜索的列表查询，并提供聚合详情（关联艺术家与歌曲）。

#### Scenario: 创建项目
- **WHEN** 在组织上下文内创建项目
- **THEN** 创建成功，且初始状态为 DRAFT（未发行）

#### Scenario: 获取项目详情
- **WHEN** 请求项目详情
- **THEN** 返回项目基本信息 + 关联艺术家列表（含角色信息）+ 关联歌曲列表

### Requirement: Project 状态机
系统 SHALL 按定义的合法状态转换规则进行状态变更，并拒绝非法状态转换，且记录状态变更日志。

#### Scenario: 合法状态转换
- **WHEN** 将状态从 DRAFT 变更到 RELEASED 或 HOLD，或从 HOLD 变更到 DRAFT/RELEASED，或从 RELEASED 变更到 HOLD
- **THEN** 状态更新成功并记录操作日志

#### Scenario: 非法状态转换
- **WHEN** 尝试将状态从 RELEASED 变更到 DRAFT
- **THEN** 拒绝变更并返回 400（或业务错误码）说明非法转换

### Requirement: 项目-艺术家关联管理
系统 SHALL 支持维护项目与艺术家的关联关系，并支持在同一项目中为同一艺术家分配多个角色。

#### Scenario: 一个艺术家多角色
- **WHEN** 在同一项目中为同一艺术家分别关联“词/曲/演唱/编曲”等多个角色
- **THEN** 查询项目艺术家列表时返回多角色信息且数据一致

### Requirement: 组织隔离与权限控制
系统 SHALL 在项目查询与详情访问中执行组织隔离；仅允许创建者与具备相应权限的用户编辑/变更状态；超级管理员可访问所有项目。

#### Scenario: 跨组织访问项目
- **WHEN** 非同组织用户访问其他组织项目列表或详情
- **THEN** 返回无权限或空结果（以项目既定数据权限策略为准）

## MODIFIED Requirements

### Requirement: 旧 Singer 接口兼容
系统 SHALL 保持既有 Singer 相关 API 可用，并将其行为适配到新的 Artist 模型（不破坏既有调用方）。

## REMOVED Requirements
无

