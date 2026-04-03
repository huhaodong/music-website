-- M3 里程碑：艺术家管理与项目管理
-- 数据库迁移脚本 V6
-- 创建时间：2026-04-03
-- 描述：创建 artist、project、project_artist、project_status_log 表

-- ========================================
-- 1. 艺术家表 artist
-- ========================================
CREATE TABLE IF NOT EXISTS `artist` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '艺术家 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '艺术家名称',
    `type` VARCHAR(50) NOT NULL DEFAULT 'singer' COMMENT '艺术家类型：singer-歌手，lyricist-作词，composer-作曲，producer-制作人',
    `sex` TINYINT DEFAULT 2 COMMENT '性别：0-女，1-男，2-保密',
    `pic` VARCHAR(500) DEFAULT NULL COMMENT '头像图片 URL',
    `birth` DATE DEFAULT NULL COMMENT '出生日期',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '地区',
    `introduction` TEXT COMMENT '简介',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_type` (`type`),
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺术家表';

-- ========================================
-- 2. 项目表 project
-- ========================================
CREATE TABLE IF NOT EXISTS `project` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '项目 ID',
    `org_id` INT DEFAULT NULL COMMENT '所属组织 ID',
    `name` VARCHAR(200) NOT NULL COMMENT '项目名称',
    `description` TEXT COMMENT '项目描述',
    `status` VARCHAR(50) NOT NULL DEFAULT 'DRAFT' COMMENT '项目状态：DRAFT-草稿，RELEASED-已发布，HOLD-暂停',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_org_id` (`org_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- ========================================
-- 3. 项目艺术家关联表 project_artist
-- ========================================
CREATE TABLE IF NOT EXISTS `project_artist` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '关联 ID',
    `project_id` INT NOT NULL COMMENT '项目 ID',
    `artist_id` INT NOT NULL COMMENT '艺术家 ID',
    `role` VARCHAR(100) DEFAULT NULL COMMENT '在项目中的角色',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_project_artist` (`project_id`, `artist_id`),
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_artist_id` (`artist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目艺术家关联表';

-- ========================================
-- 4. 项目状态变更日志表 project_status_log
-- ========================================
CREATE TABLE IF NOT EXISTS `project_status_log` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志 ID',
    `project_id` INT NOT NULL COMMENT '项目 ID',
    `old_status` VARCHAR(50) DEFAULT NULL COMMENT '原状态',
    `new_status` VARCHAR(50) NOT NULL COMMENT '新状态',
    `operator_id` INT DEFAULT NULL COMMENT '操作人 ID',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目状态变更日志表';

-- ========================================
-- 5. 验证查询
-- ========================================
SELECT 'artist 表' AS item, COUNT(*) AS count FROM `artist`;
SELECT 'project 表' AS item, COUNT(*) AS count FROM `project`;
SELECT 'project_artist 表' AS item, COUNT(*) AS count FROM `project_artist`;
SELECT 'project_status_log 表' AS item, COUNT(*) AS count FROM `project_status_log`;