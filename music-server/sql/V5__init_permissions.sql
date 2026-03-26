-- M2 里程碑：RBAC 权限系统与组织用户管理
-- 数据库迁移脚本 V5
-- 创建时间：2026-03-23
-- 描述：初始化权限、角色、组织相关表结构及基础数据

-- ========================================
-- 1. 权限表 permission
-- ========================================
CREATE TABLE IF NOT EXISTS `permission` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(100) NOT NULL COMMENT '权限编码（唯一）',
    `type` VARCHAR(20) NOT NULL DEFAULT 'menu' COMMENT '权限类型：menu-菜单，button-按钮，api-API 接口',
    `url` VARCHAR(255) COMMENT 'URL 路径',
    `method` VARCHAR(20) COMMENT 'HTTP 方法：GET/POST/PUT/DELETE',
    `parent_id` INT DEFAULT 0 COMMENT '父权限 ID，0 表示根节点',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ========================================
-- 2. 角色表 role
-- ========================================
CREATE TABLE IF NOT EXISTS `role` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(100) NOT NULL COMMENT '角色编码（唯一）',
    `description` VARCHAR(500) COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `is_system` TINYINT DEFAULT 0 COMMENT '是否系统角色：1-是，0-否（系统角色不可删除）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_status` (`status`),
    INDEX `idx_is_system` (`is_system`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ========================================
-- 3. 角色权限关联表 role_permission
-- ========================================
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '关联 ID',
    `role_id` INT NOT NULL COMMENT '角色 ID',
    `permission_id` INT NOT NULL COMMENT '权限 ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_permission_id` (`permission_id`),
    CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ========================================
-- 4. 用户角色关联表 user_role
-- ========================================
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '关联 ID',
    `user_id` INT NOT NULL COMMENT '用户 ID',
    `user_type` VARCHAR(20) NOT NULL DEFAULT 'consumer' COMMENT '用户类型：admin/consumer',
    `role_id` INT NOT NULL COMMENT '角色 ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_role_type` (`user_id`, `user_type`, `role_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_role_id` (`role_id`),
    CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ========================================
-- 5. 权限模板表 permission_template
-- ========================================
CREATE TABLE IF NOT EXISTS `permission_template` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '模板 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `code` VARCHAR(100) NOT NULL COMMENT '模板编码（唯一）',
    `description` VARCHAR(500) COMMENT '模板描述',
    `permissions_json` TEXT COMMENT '权限 JSON 配置',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限模板表';

-- ========================================
-- 6. 组织表 organization（如已存在则跳过）
-- ========================================
CREATE TABLE IF NOT EXISTS `organization` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '组织 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '组织名称',
    `code` VARCHAR(100) NOT NULL COMMENT '组织编码',
    `parent_id` INT DEFAULT 0 COMMENT '父组织 ID，0 表示根节点',
    `path` VARCHAR(500) COMMENT '层级路径（如：/1/3/5/）',
    `level` INT DEFAULT 1 COMMENT '组织层级',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `description` VARCHAR(500) COMMENT '组织描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_path` (`path`(100)),
    INDEX `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织表';

-- ========================================
-- 7. 初始化权限数据
-- ========================================
-- 系统管理权限树
INSERT INTO `permission` (`name`, `code`, `type`, `parent_id`, `sort`, `status`) VALUES
-- 根节点
('系统管理', 'system', 'menu', 0, 1, 1),
('用户管理', 'system:user', 'menu', 1, 1, 1),
('用户查询', 'system:user:query', 'api', 2, 1, 1),
('用户新增', 'system:user:add', 'api', 2, 2, 1),
('用户修改', 'system:user:update', 'api', 2, 3, 1),
('用户删除', 'system:user:delete', 'api', 2, 4, 1),
('角色分配', 'system:user:assign-role', 'api', 2, 5, 1),
('组织管理', 'system:org', 'menu', 1, 2, 1),
('组织查询', 'system:org:query', 'api', 8, 1, 1),
('组织新增', 'system:org:add', 'api', 8, 2, 1),
('组织修改', 'system:org:update', 'api', 8, 3, 1),
('组织删除', 'system:org:delete', 'api', 8, 4, 1),
('角色管理', 'system:role', 'menu', 1, 3, 1),
('角色查询', 'system:role:query', 'api', 13, 1, 1),
('角色新增', 'system:role:add', 'api', 13, 2, 1),
('角色修改', 'system:role:update', 'api', 13, 3, 1),
('角色删除', 'system:role:delete', 'api', 13, 4, 1),
('权限分配', 'system:role:assign-permission', 'api', 13, 5, 1),
('权限管理', 'system:permission', 'menu', 1, 4, 1),
('权限查询', 'system:permission:query', 'api', 19, 1, 1),
('模板管理', 'system:permission:template', 'api', 19, 2, 1),
-- 业务管理
('内容管理', 'content', 'menu', 0, 2, 1),
('歌手管理', 'content:singer', 'menu', 22, 1, 1),
('歌曲管理', 'content:song', 'menu', 22, 2, 1),
('歌单管理', 'content:songlist', 'menu', 22, 3, 1),
('评论管理', 'content:comment', 'menu', 22, 4, 1),
('收藏管理', 'content:collect', 'menu', 22, 5, 1);

-- ========================================
-- 8. 初始化角色数据
-- ========================================
INSERT INTO `role` (`name`, `code`, `description`, `status`, `sort`, `is_system`) VALUES
('超级管理员', 'admin', '拥有系统所有权限，不可删除', 1, 0, 1),
('组织管理员', 'org_admin', '可管理本组织及下级组织', 1, 1, 1),
('普通用户', 'user', '普通用户权限', 1, 2, 1);

-- ========================================
-- 9. 初始化权限模板数据
-- ========================================
INSERT INTO `permission_template` (`name`, `code`, `description`, `permissions_json`, `status`) VALUES
('普通用户模板', 'user_template', '普通用户基础权限配置', 
 '{"permissions": ["system:user:query", "content:singer:query", "content:song:query", "content:songlist:query"]}', 
 1),
('组织管理员模板', 'org_admin_template', '组织管理员权限配置', 
 '{"permissions": ["system:user:query", "system:org:query", "system:org:add", "system:org:update", "content:*"]}', 
 1);

-- ========================================
-- 10. 为超级管理员角色分配所有权限
-- ========================================
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;

-- ========================================
-- 11. 为普通用户角色分配基础权限
-- ========================================
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `permission` WHERE `code` IN (
    'system:user:query',
    'content:singer:query',
    'content:song:query',
    'content:songlist:query',
    'content:comment:query',
    'content:collect:query'
);

-- ========================================
-- 12. 数据验证查询（执行后验证）
-- ========================================
-- 验证权限总数
SELECT '权限总数' AS item, COUNT(*) AS count FROM `permission`;

-- 验证角色总数
SELECT '角色总数' AS item, COUNT(*) AS count FROM `role`;

-- 验证权限模板总数
SELECT '权限模板总数' AS item, COUNT(*) AS count FROM `permission_template`;

-- 验证超级管理员权限数
SELECT '超级管理员权限数' AS item, COUNT(*) AS count FROM `role_permission` WHERE role_id = 1;

-- 验证普通用户权限数
SELECT '普通用户权限数' AS item, COUNT(*) AS count FROM `role_permission` WHERE role_id = 3;
