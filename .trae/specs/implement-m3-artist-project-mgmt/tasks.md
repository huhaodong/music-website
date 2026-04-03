# Tasks
- [x] Task 1: 数据库增量迁移与验证（M3-01~M3-06）
  - [x] 1.1 新增 artist 表迁移脚本（V6__create_artist_table.sql），并添加最小验证测试（RED→GREEN）
  - [x] 1.2 新增 project 表迁移脚本（V7__create_project_table.sql），并添加最小验证测试（RED→GREEN）
  - [x] 1.3 新增 project_artist 关联表迁移脚本（V8__create_project_artist.sql），并添加最小验证测试（RED→GREEN）
  - [x] 1.4 新增 Singer→Artist 数据迁移脚本（V9__migrate_singer_to_artist.sql），并编写数据完整性测试（ArtistMigrationTest.java）
  - [x] 1.5 扩展 song 表（V10__extend_song_table.sql），并补充迁移验证测试覆盖字段与映射

- [x] Task 2: Artist 后端能力（M3-07~M3-11）
  - [x] 2.1 新增 Artist 实体与 Mapper（Artist.java / ArtistMapper.java）
  - [x] 2.2 实现 ArtistService：CRUD + 类型筛选 + 模糊搜索 + 分页（TDD：ArtistServiceTest.java）
  - [x] 2.3 实现 ArtistController：REST API + 权限校验 + 参数校验（TDD：ArtistControllerTest.java，MockMvc）
  - [x] 2.4 实现 艺术家-项目 关联查询能力（TDD：覆盖“查询艺术家关联项目”）
  - [x] 2.5 旧 SingerController 兼容适配：旧接口转发/映射到新 Artist 能力（TDD：旧接口可用）

- [x] Task 3: Project 后端能力（M3-12~M3-19）
  - [x] 3.1 新增 Project 实体与 Mapper（Project.java / ProjectMapper.java）
  - [x] 3.2 实现 ProjectService：基础 CRUD + 逻辑删除（TDD：ProjectServiceTest.java）
  - [x] 3.3 实现 ProjectController：REST API（列表/详情/创建/更新/删除/状态变更）（TDD：ProjectControllerTest.java，MockMvc）
  - [x] 3.4 实现项目状态机服务（ProjectStatusService.java）：合法转换/非法拒绝/状态日志（TDD：ProjectStatusServiceTest.java）
  - [x] 3.5 实现项目列表：分页/排序/状态筛选/组织筛选/关键字搜索（TDD：PaginationTest/相关用例）
  - [x] 3.6 实现项目详情聚合：关联艺术家（含角色）与歌曲（TDD：DetailTest/相关用例）
  - [x] 3.7 实现项目-艺术家关联管理：关联/取消关联/查询（支持同一艺术家多角色）（TDD：ProjectArtistServiceTest.java）
  - [x] 3.8 实现项目权限与组织隔离（TDD：ProjectPermissionTest.java）

- [x] Task 4: 后台管理前端页面（M3-20~M3-22）
  - [x] 4.1 新增 ArtistManage.vue：列表/筛选/分页/创建/编辑/删除（含“有关联项目时提示”）
  - [x] 4.2 新增 ProjectManage.vue：列表/筛选/分页/创建/编辑/删除（逻辑删除后列表不可见）
  - [x] 4.3 新增 ProjectDetail.vue：项目详情 + 关联艺术家/歌曲展示 + 状态变更操作（非法转换按钮不可见）
  - [x] 4.4 补齐前端组件测试（ArtistManage.test.ts / ProjectManage.test.ts / ProjectDetail.test.ts）

- [x] Task 5: 回归与覆盖率验收（M3-V22~M3-V25）
  - [x] 5.1 后端：运行测试并修复失败用例（Artist/Project/状态机/迁移）
  - [x] 5.2 后端：生成覆盖率报告并确保 ProjectStatusService 覆盖率 ≥ 90%
  - [x] 5.3 前端：运行组件测试并修复失败用例

# Task Dependencies
- Task 2 依赖 Task 1（迁移脚本与字段准备就绪）
- Task 3 依赖 Task 1（表结构与扩展字段就绪）
- Task 4 依赖 Task 2 与 Task 3（API 就绪）
- Task 5 依赖 Task 1~4
