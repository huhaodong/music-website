-- V6: Update admin password to BCrypt encoded password
-- Password: 123 (BCrypt encoded)

UPDATE admin SET password = '$2a$10$S6UjTauU8WD0hzmymcoujuH4Iv3rlW6ltbhJlK4BLoRLPHMk2uaG6' WHERE username = 'admin';
UPDATE admin SET password = '$2a$10$S6UjTauU8WD0hzmymcoujuH4Iv3rlW6ltbhJlK4BLoRLPHMk2uaG6' WHERE username = 'admin1';
