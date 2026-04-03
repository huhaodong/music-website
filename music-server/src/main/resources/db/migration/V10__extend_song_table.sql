-- V10: Extend song table to support project and artist dimensions (non-destructive)
ALTER TABLE song ADD COLUMN IF NOT EXISTS artist_id INT DEFAULT NULL;
ALTER TABLE song ADD COLUMN IF NOT EXISTS project_id INT DEFAULT NULL;

-- Keep compatibility with existing DBs that might miss timestamps
ALTER TABLE song ADD COLUMN IF NOT EXISTS create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE song ADD COLUMN IF NOT EXISTS update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Backfill new artist_id based on legacy singer_id mapping
UPDATE song SET artist_id = singer_id WHERE artist_id IS NULL;

CREATE INDEX idx_song_project_id ON song (project_id);
CREATE INDEX idx_song_artist_id ON song (artist_id);
