-- V7: Add nickname field to consumer table
ALTER TABLE consumer ADD COLUMN nickname VARCHAR(50) DEFAULT NULL;