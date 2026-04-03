-- V6: Create artist table (Singer upgraded to Artist)
CREATE TABLE IF NOT EXISTS artist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(32) NOT NULL DEFAULT 'singer',
    sex TINYINT DEFAULT NULL,
    pic VARCHAR(255) DEFAULT NULL,
    birth DATETIME DEFAULT NULL,
    location VARCHAR(255) DEFAULT NULL,
    introduction VARCHAR(255) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_artist_type ON artist (type);
CREATE INDEX idx_artist_name ON artist (name);
