-- V17: Create project_status_log table (project lifecycle audit)
CREATE TABLE IF NOT EXISTS project_status_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    from_status VARCHAR(32) NOT NULL,
    to_status VARCHAR(32) NOT NULL,
    operator_id INT DEFAULT NULL,
    operator_name VARCHAR(255) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_project_status_log_project ON project_status_log (project_id);
CREATE INDEX idx_project_status_log_time ON project_status_log (create_time);

