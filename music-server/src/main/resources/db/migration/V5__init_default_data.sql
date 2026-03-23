-- V5: Initialize default roles and permissions

-- Insert default roles
INSERT INTO role (name, code, description, status) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
('普通用户', 'USER', '普通用户，拥有基本权限', 1),
('歌手', 'SINGER', '歌手用户', 1);

-- Insert default permissions
INSERT INTO permission (name, code, type, url, method, parent_id, sort) VALUES
('系统管理', 'system', 'menu', '/system', NULL, NULL, 1),
('用户管理', 'system:user', 'menu', '/system/user', NULL, 1, 1),
('角色管理', 'system:role', 'menu', '/system/role', NULL, 1, 2),
('权限管理', 'system:permission', 'menu', '/system/permission', NULL, 1, 3),
('歌曲管理', 'song', 'menu', '/song', NULL, NULL, 2),
('歌曲列表', 'song:list', 'menu', '/song/list', NULL, 5, 1),
('歌手管理', 'singer', 'menu', '/singer', NULL, NULL, 3),
('歌手列表', 'singer:list', 'menu', '/singer/list', NULL, 7, 1),
('歌单管理', 'songlist', 'menu', '/songlist', NULL, NULL, 4),
('歌单列表', 'songlist:list', 'menu', '/songlist/list', NULL, 9, 1),
('评论管理', 'comment', 'menu', '/comment', NULL, NULL, 5),
('评论列表', 'comment:list', 'menu', '/comment/list', NULL, 11, 1);

-- Assign all permissions to SUPER_ADMIN role
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission;

-- Assign basic permissions to USER role (song list, singer list, songlist list, comment list)
INSERT INTO role_permission (role_id, permission_id)
SELECT 2, id FROM permission WHERE code IN ('song:list', 'singer:list', 'songlist:list', 'comment:list');
