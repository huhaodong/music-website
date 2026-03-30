# 权限控制修复 Spec

## Why

根据权限测试报告，当前权限覆盖率仅21.95%，32个权限缺失权限控制。只有UserController实现了权限控制，其他Controller完全没有权限保护，存在严重安全隐患。

## What Changes

1. **为所有缺失权限控制的Controller添加@RequirePermission注解**
2. **统一权限编码规范**
3. **修复权限编码不一致问题**

## Impact

- 涉及Controller: SongController, SingerController, SongListController, RoleController, OrganizationController, CommentController, FileDownloadController, AdminController
- 权限覆盖率目标: 从21.95%提升到100%

## Bug修复清单

### P0 - 严重级别 (立即修复)

| 编号 | 权限编码 | Controller | 方法 | 修复方案 |
|------|----------|------------|------|----------|
| BUG-003 | song:add | SongController | addSong | 添加@RequirePermission("song:add") |
| BUG-004 | song:edit | SongController | updateSongMsg | 添加@RequirePermission("song:edit") |
| BUG-005 | song:delete | SongController | deleteSong | 添加@RequirePermission("song:delete") |
| BUG-018 | role:add | RoleController | addRole | 添加@RequirePermission("role:add") |
| BUG-019 | role:edit | RoleController | updateRole | 添加@RequirePermission("role:edit") |
| BUG-020 | role:delete | RoleController | deleteRole | 添加@RequirePermission("role:delete") |
| BUG-021 | role:assign | PermissionController | assignPermissionsToRole | 添加@RequirePermission("role:assign") |
| BUG-024 | org:add | OrganizationController | addOrganization | 添加@RequirePermission("org:add") |
| BUG-025 | org:edit | OrganizationController | updateOrganization | 添加@RequirePermission("org:edit") |
| BUG-026 | org:delete | OrganizationController | deleteOrganization | 添加@RequirePermission("org:delete") |
| BUG-029 | comment:delete | CommentController | deleteComment | 添加@RequirePermission("comment:delete") |
| BUG-032 | system:admin:login | AdminController | loginStatus | 添加@RequirePermission("system:admin:login") |

### P1 - 高级别

| 编号 | 权限编码 | Controller | 方法 | 修复方案 |
|------|----------|------------|------|----------|
| BUG-001 | song:list | SongController | allSong | 添加@RequirePermission("song:list") |
| BUG-002 | song:detail | SongController | songOfId | 添加@RequirePermission("song:detail") |
| BUG-006 | song:download | FileDownloadController | downloadFile | 添加@RequirePermission("song:download") |
| BUG-016 | role:list | RoleController | getAllRoles | 添加@RequirePermission("role:list") |
| BUG-017 | role:detail | RoleController | getRoleById | 添加@RequirePermission("role:detail") |
| BUG-022 | org:list | OrganizationController | getAllOrganizations | 添加@RequirePermission("org:list") |
| BUG-023 | org:detail | OrganizationController | getOrganizationById | 添加@RequirePermission("org:detail") |
| BUG-028 | comment:add | CommentController | addComment | 添加@RequirePermission("comment:add") |

### P2 - 中级别

| 编号 | 权限编码 | Controller | 方法 | 修复方案 |
|------|----------|------------|------|----------|
| BUG-027 | comment:list | CommentController | commentOfSongId | 添加@RequirePermission("comment:list") |
| BUG-009 | user:list | UserController | getAllUsers | 修改权限编码为"user:list" |
| BUG-010 | user:detail | UserController | getUserById | 修改权限编码为"user:detail" |
| BUG-012 | user:edit | UserController | updateUserMsg | 修改权限编码为"user:edit" |

## 不修复项（功能未实现）

| 编号 | 权限编码 | 原因 |
|------|----------|------|
| BUG-007 | song:listen | 与song:detail共用接口，暂不分离 |
| BUG-008 | song:material | 功能未独立实现 |
| BUG-014 | user:disable | 功能未独立实现 |
| BUG-030 | system:config | 接口不存在，需单独实现 |
| BUG-031 | system:log | 接口不存在，需单独实现 |
