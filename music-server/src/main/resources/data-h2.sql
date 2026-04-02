-- Initial data for music-website
SET NAMES utf8mb4;

-- Insert admin users
INSERT INTO admin (id, name, password) VALUES
(1, 'admin', '123'),
(2, 'admin1', '565');

-- Insert banner data
INSERT INTO banner (id, pic) VALUES
(1, '/img/swiper/1.jpg'),
(2, '/img/swiper/2.jpg'),
(3, '/img/swiper/3.jpg'),
(4, '/img/swiper/4.jpg'),
(5, '/img/swiper/5.jpg'),
(6, '/img/swiper/6.jpg'),
(7, '/img/swiper/7.jpg'),
(8, '/img/swiper/8.jpeg');

-- Insert default roles
INSERT INTO role (id, name, code, description) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统最高权限'),
(2, '管理员', 'ADMIN', '普通管理员权限'),
(3, '普通用户', 'USER', '普通用户权限');

-- Insert default permissions
INSERT INTO permission (id, name, code, type, url, method, parent_id) VALUES
(1, '系统管理', 'system:manage', 'menu', '/system', NULL, NULL),
(35, '后台登录', 'system:admin:login', 'button', '/api/auth/login', 'POST', 1),
(2, '用户管理', 'user:manage', 'menu', '/user', NULL, 1),
(3, '角色管理', 'role:manage', 'menu', '/role', NULL, 1),
(4, '权限管理', 'permission:manage', 'menu', '/permission', NULL, 1),
(5, '组织管理', 'org:manage', 'menu', '/organization', NULL, 1),
(6, '音乐管理', 'music:manage', 'menu', '/music', NULL, NULL),
(7, '歌手管理', 'singer:manage', 'menu', '/singer', NULL, 6),
(8, '歌曲管理', 'song:manage', 'menu', '/song', NULL, 6),
(9, '歌单管理', 'songlist:manage', 'menu', '/songlist', NULL, 6),
(10, '用户列表', 'user:list', 'button', '/api/user/list', 'GET', 2),
(11, '用户创建', 'user:create', 'button', '/api/user/create', 'POST', 2),
(12, '用户编辑', 'user:update', 'button', '/api/user/update', 'PUT', 2),
(13, '用户删除', 'user:delete', 'button', '/api/user/delete', 'DELETE', 2),
(14, '角色列表', 'role:list', 'button', '/api/role/list', 'GET', 3),
(15, '角色创建', 'role:create', 'button', '/api/role/create', 'POST', 3),
(16, '角色编辑', 'role:update', 'button', '/api/role/update', 'PUT', 3),
(17, '角色删除', 'role:delete', 'button', '/api/role/delete', 'DELETE', 3),
(18, '权限列表', 'permission:list', 'button', '/api/permission/list', 'GET', 4),
(19, '组织列表', 'org:list', 'button', '/system/organization/list', 'GET', 5),
(20, '组织创建', 'org:add', 'button', '/system/organization/add', 'POST', 5),
(21, '组织编辑', 'org:edit', 'button', '/system/organization/update', 'PUT', 5),
(22, '组织删除', 'org:delete', 'button', '/system/organization/delete', 'DELETE', 5),
(36, '组织详情', 'org:detail', 'button', '/system/organization/detail', 'GET', 5),
(23, '歌手列表', 'singer:list', 'button', '/api/singer/list', 'GET', 7),
(24, '歌手添加', 'singer:add', 'button', '/api/singer/add', 'POST', 7),
(25, '歌手编辑', 'singer:update', 'button', '/api/singer/update', 'PUT', 7),
(26, '歌手删除', 'singer:delete', 'button', '/api/singer/delete', 'DELETE', 7),
(27, '歌曲列表', 'song:list', 'button', '/api/song/list', 'GET', 8),
(28, '歌曲添加', 'song:add', 'button', '/api/song/add', 'POST', 8),
(29, '歌曲编辑', 'song:update', 'button', '/api/song/update', 'PUT', 8),
(30, '歌曲删除', 'song:delete', 'button', '/api/song/delete', 'DELETE', 8),
(31, '歌单列表', 'songlist:list', 'button', '/api/songlist/list', 'GET', 9),
(32, '歌单添加', 'songlist:add', 'button', '/api/songlist/add', 'POST', 9),
(33, '歌单编辑', 'songlist:update', 'button', '/api/songlist/update', 'PUT', 9),
(34, '歌单删除', 'songlist:delete', 'button', '/api/songlist/delete', 'DELETE', 9),
(37, '权限分配', 'role:assign', 'button', '/system/permission/assign', 'POST', 3),
(38, '权限分配(系统码)', 'system:role:assign-permission', 'button', '/system/permission/assign', 'POST', 3);

-- Insert role permissions (super admin has all permissions)
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 35),
(1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18),
(1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27),
(1, 28), (1, 29), (1, 30), (1, 31), (1, 32), (1, 33), (1, 34), (1, 36),
(1, 37), (1, 38);

-- Insert admin role permissions
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 35), (2, 2), (2, 3), (2, 6), (2, 7), (2, 8), (2, 9),
(2, 10), (2, 11), (2, 12), (2, 13), (2, 14), (2, 15), (2, 16), (2, 17),
(2, 23), (2, 24), (2, 25), (2, 26), (2, 27), (2, 28), (2, 29), (2, 30),
(2, 31), (2, 32), (2, 33), (2, 34),
(2, 37), (2, 38);

-- Insert user role permissions
INSERT INTO role_permission (role_id, permission_id) VALUES
(3, 6), (3, 7), (3, 8), (3, 9),
(3, 23), (3, 27), (3, 31);

-- Insert default organization
INSERT INTO organization (id, name, code, parent_id, level, path) VALUES
(1, '默认组织', 'ORG-DEFAULT', NULL, 1, '/1/');

-- Insert test consumer (password is BCrypt encrypted: 123456)
INSERT INTO consumer (id, username, password, nickname, create_time, update_time) VALUES
(1, 'test', '$2a$10$UmZvDXLvvDUIOQoXELlkpunII9CUmVv1cuvW7/x.5FXPpElrGCKfK', '测试用户', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert admin consumer (password is BCrypt encrypted: admin123)
INSERT INTO consumer (id, username, password, nickname, create_time, update_time) VALUES
(2, 'admin', '$2a$10$MAopowFUYub8ORMw2KIxp.GPsG8/0DjCfeu0P9WOTy510fVfpNIRW', '管理员', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign roles to users
INSERT INTO user_role (user_id, user_type, role_id) VALUES
(1, 'admin', 1),  -- admin has super admin role
(1, 'consumer', 3), -- test consumer has user role
(2, 'consumer', 1); -- admin consumer has super admin role
