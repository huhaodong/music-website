-- 清空现有权限和角色权限关联
DELETE FROM role_permission;
DELETE FROM permission;

-- 插入歌曲相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('查看歌曲列表', 'song:list', 1, 1, 1),
('查看歌曲详情', 'song:detail', 1, 2, 1),
('添加歌曲', 'song:add', 2, 3, 1),
('编辑歌曲', 'song:edit', 2, 4, 1),
('删除歌曲', 'song:delete', 2, 5, 1),
('下载歌曲', 'song:download', 2, 6, 1),
('歌曲试听', 'song:listen', 2, 7, 1),
('查看歌曲物料', 'song:material', 2, 8, 1);

-- 插入歌手相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('查看歌手列表', 'singer:list', 1, 11, 1),
('查看歌手详情', 'singer:detail', 1, 12, 1),
('添加歌手', 'singer:add', 2, 13, 1),
('编辑歌手', 'singer:edit', 2, 14, 1),
('删除歌手', 'singer:delete', 2, 15, 1);

-- 插入歌单相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('查看歌单列表', 'songlist:list', 1, 21, 1),
('查看歌单详情', 'songlist:detail', 1, 22, 1),
('创建歌单', 'songlist:add', 2, 23, 1),
('编辑歌单', 'songlist:edit', 2, 24, 1),
('删除歌单', 'songlist:delete', 2, 25, 1),
('收藏歌单', 'songlist:collect', 2, 26, 1);

-- 插入用户相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('查看用户列表', 'user:list', 1, 31, 1),
('查看用户详情', 'user:detail', 1, 32, 1),
('添加用户', 'user:add', 2, 33, 1),
('编辑用户', 'user:edit', 2, 34, 1),
('删除用户', 'user:delete', 2, 35, 1),
('禁用/启用用户', 'user:disable', 2, 36, 1);

-- 插入角色相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('查看角色列表', 'role:list', 1, 41, 1),
('查看角色详情', 'role:detail', 1, 42, 1),
('创建角色', 'role:add', 2, 43, 1),
('编辑角色', 'role:edit', 2, 44, 1),
('删除角色', 'role:delete', 2, 45, 1),
('分配权限', 'role:assign', 2, 46, 1);

-- 插入组织相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('查看组织列表', 'org:list', 1, 51, 1),
('查看组织详情', 'org:detail', 1, 52, 1),
('创建组织', 'org:add', 2, 53, 1),
('编辑组织', 'org:edit', 2, 54, 1),
('删除组织', 'org:delete', 2, 55, 1);

-- 插入评论相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('查看评论列表', 'comment:list', 1, 61, 1),
('添加评论', 'comment:add', 2, 62, 1),
('删除评论', 'comment:delete', 2, 63, 1);

-- 插入系统相关权限
INSERT INTO permission (name, code, type, sort, status) VALUES
('系统配置', 'system:config', 1, 71, 1),
('查看操作日志', 'system:log', 1, 72, 1);
