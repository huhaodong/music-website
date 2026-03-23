-- V2: Add extended fields to consumer table (non-destructive)
ALTER TABLE consumer ADD COLUMN org_id INT DEFAULT NULL COMMENT '组织ID';
ALTER TABLE consumer ADD COLUMN status TINYINT DEFAULT 1 COMMENT '1:启用 0:禁用';
ALTER TABLE consumer ADD COLUMN last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间';
ALTER TABLE consumer ADD COLUMN update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';