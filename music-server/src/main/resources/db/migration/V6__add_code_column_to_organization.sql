-- V6: Add code column to organization table
ALTER TABLE organization
    ADD COLUMN IF NOT EXISTS code VARCHAR(64) NULL COMMENT '组织编码' AFTER name;
