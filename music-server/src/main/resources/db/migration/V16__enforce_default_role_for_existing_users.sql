-- V16: Enforce default role for existing users who lack roles
-- This script ensures all consumers have at least the 'USER' role assigned.

INSERT INTO user_role (user_id, user_type, role_id)
SELECT c.id, 'consumer', r.id
FROM consumer c, role r
WHERE r.code = 'USER'
  AND NOT EXISTS (
    SELECT 1 FROM user_role ur
    WHERE ur.user_id = c.id
      AND ur.user_type = 'consumer'
  );
