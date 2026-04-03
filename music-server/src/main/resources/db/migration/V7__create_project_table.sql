-- V7: Create project table (Song project lifecycle management)
CREATE TABLE IF NOT EXISTS project (
    id INT AUTO_INCREMENT PRIMARY KEY,
    org_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000) DEFAULT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_by INT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_project_org_status ON project (org_id, status);
CREATE INDEX idx_project_deleted ON project (deleted);
