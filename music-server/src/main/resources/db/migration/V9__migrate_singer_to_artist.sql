-- V9: Migrate existing singer data into artist table (id must be kept)
INSERT INTO artist (id, name, type, sex, pic, birth, location, introduction, create_time, update_time)
SELECT s.id,
       s.name,
       'singer',
       s.sex,
       s.pic,
       s.birth,
       s.location,
       s.introduction,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM singer s
WHERE NOT EXISTS (
    SELECT 1 FROM artist a WHERE a.id = s.id
);
