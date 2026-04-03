-- 修复 artist 和 project 页面显示 "No message available" 的问题
-- 问题原因：artist:list 和 project:list 权限缺失

-- 1. 添加 artist 相关权限
INSERT INTO `permission` (`name`, `code`, `type`, `parent_id`, `sort`, `status`) VALUES
('艺术家管理', 'artist', 'menu', 0, 3, 1),
('艺术家查询', 'artist:list', 'api', 30, 1, 1),
('艺术家详情', 'artist:detail', 'api', 30, 2, 1),
('艺术家新增', 'artist:create', 'api', 30, 3, 1),
('艺术家修改', 'artist:update', 'api', 30, 4, 1),
('艺术家删除', 'artist:delete', 'api', 30, 5, 1),
('艺术家项目', 'artist:projects', 'api', 30, 6, 1),
('艺术家编辑', 'artist:edit', 'api', 30, 7, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 2. 添加 project 相关权限
INSERT INTO `permission` (`name`, `code`, `type`, `parent_id`, `sort`, `status`) VALUES
('项目管理', 'project', 'menu', 0, 4, 1),
('项目查询', 'project:list', 'api', 40, 1, 1),
('项目详情', 'project:detail', 'api', 40, 2, 1),
('项目新增', 'project:create', 'api', 40, 3, 1),
('项目修改', 'project:update', 'api', 40, 4, 1),
('项目删除', 'project:delete', 'api', 40, 5, 1),
('项目状态', 'project:status', 'api', 40, 6, 1),
('项目编辑', 'project:edit', 'api', 40, 7, 1),
('项目艺术家', 'project:artist:bind', 'api', 40, 8, 1),
('项目艺术家解绑', 'project:artist:unbind', 'api', 40, 9, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 3. 为 admin 角色添加所有 artist 和 project 权限
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission` WHERE `code` IN (
    'artist:list', 'artist:detail', 'artist:create', 'artist:update', 'artist:delete', 'artist:projects', 'artist:edit',
    'project:list', 'project:detail', 'project:create', 'project:update', 'project:delete', 'project:status', 'project:edit',
    'project:artist:bind', 'project:artist:unbind'
);

-- 4. 为 org_admin 角色添加 artist 和 project 权限
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `permission` WHERE `code` IN (
    'artist:list', 'artist:detail', 'artist:create', 'artist:update', 'artist:delete', 'artist:projects', 'artist:edit',
    'project:list', 'project:detail', 'project:create', 'project:update', 'project:delete', 'project:status', 'project:edit',
    'project:artist:bind', 'project:artist:unbind'
);

-- 5. 验证修复结果
SELECT 'artist 权限' AS item, COUNT(*) AS count FROM `permission` WHERE `code` LIKE 'artist:%';
SELECT 'project 权限' AS item, COUNT(*) AS count FROM `permission` WHERE `code` LIKE 'project:%';
SELECT 'admin 的 artist/project 权限数' AS item, COUNT(*) AS count FROM `role_permission` WHERE `role_id` = 1 AND `permission_id` IN (
    SELECT id FROM `permission` WHERE `code` LIKE 'artist:%' OR `code` LIKE 'project:%'
);