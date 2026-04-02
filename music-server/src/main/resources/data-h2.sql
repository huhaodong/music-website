-- Initial data for music-website
-- This data should be consistent with Flyway migration scripts

-- Insert default roles (must match V5__init_default_data.sql)
INSERT INTO role (id, name, code, description, status) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
(2, '普通用户', 'USER', '普通用户，拥有基本权限', 1),
(3, '歌手', 'SINGER', '歌手用户', 1);

-- Insert default permissions (must match V5__init_default_data.sql)
INSERT INTO permission (id, name, code, type, url, method, parent_id, sort, status) VALUES
(1, '系统管理', 'system', 'menu', '/system', NULL, NULL, 1, 1),
(2, '用户管理', 'system:user', 'menu', '/system/user', NULL, 1, 1, 1),
(3, '角色管理', 'system:role', 'menu', '/system/role', NULL, 1, 2, 1),
(4, '权限管理', 'system:permission', 'menu', '/system/permission', NULL, 1, 3, 1),
(5, '歌曲管理', 'song', 'menu', '/song', NULL, NULL, 2, 1),
(6, '歌曲列表', 'song:list', 'menu', '/song/list', NULL, 5, 1, 1),
(7, '歌手管理', 'singer', 'menu', '/singer', NULL, NULL, 3, 1),
(8, '歌手列表', 'singer:list', 'menu', '/singer/list', NULL, 7, 1, 1),
(9, '歌单管理', 'songlist', 'menu', '/songlist', NULL, NULL, 4, 1),
(10, '歌单列表', 'songlist:list', 'menu', '/songlist/list', NULL, 9, 1, 1),
(11, '评论管理', 'comment', 'menu', '/comment', NULL, NULL, 5, 1),
(12, '评论列表', 'comment:list', 'menu', '/comment/list', NULL, 11, 1, 1);

-- Assign all permissions to SUPER_ADMIN role (role_id=1)
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission;

-- Assign basic permissions to USER role (role_id=2)
-- USER can access: song:list, singer:list, songlist:list, comment:list
INSERT INTO role_permission (role_id, permission_id)
SELECT 2, id FROM permission WHERE code IN ('song:list', 'singer:list', 'songlist:list', 'comment:list');

-- Insert default organization
INSERT INTO organization (id, name, code, parent_id, level, path, status) VALUES
(1, '默认组织', 'ORG-DEFAULT', NULL, 1, '/1/', 1);

-- Insert test consumer (password is BCrypt encrypted: 123456)
INSERT INTO consumer (id, username, password, nickname, create_time, update_time, status) VALUES
(1, 'test', '$2a$10$UmZvDXLvvDUIOQoXELlkpunII9CUmVv1cuvW7/x.5FXPpElrGCKfK', '测试用户', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- Insert admin consumer (password is BCrypt encrypted: admin123)
INSERT INTO consumer (id, username, password, nickname, create_time, update_time, status) VALUES
(2, 'admin', '$2a$10$MAopowFUYub8ORMw2KIxp.GPsG8/0DjCfeu0P9WOTy510fVfpNIRW', '管理员', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- Assign roles to users (must be consistent with V9__enforce_default_role_for_existing_users.sql)
-- test user (id=1) has USER role
INSERT INTO user_role (user_id, user_type, role_id) VALUES
(1, 'consumer', 2);

-- admin user (id=2) has SUPER_ADMIN role
INSERT INTO user_role (user_id, user_type, role_id) VALUES
(2, 'consumer', 1);
