-- V8: Create project_artist association table (Artist can have multiple roles in one project)
CREATE TABLE IF NOT EXISTS project_artist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    artist_id INT NOT NULL,
    role VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (project_id, artist_id, role)
);

CREATE INDEX idx_project_artist_project ON project_artist (project_id);
CREATE INDEX idx_project_artist_artist ON project_artist (artist_id);
