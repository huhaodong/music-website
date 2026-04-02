-- V8: 修复 /system/permission/assign 权限码兼容性
-- 目标：
-- 1) 补齐 legacy 权限码 role:assign
-- 2) 补齐 system 权限码 system:role:assign-permission
-- 3) 为超级管理员角色补齐上述权限（若缺失）

INSERT INTO permission (name, code, type, url, method, parent_id, sort, status)
SELECT '分配权限', 'role:assign', 'api', '/system/permission/assign', 'POST',
       (SELECT id FROM permission WHERE code = 'system:role' LIMIT 1),
       5, 1
WHERE NOT EXISTS (
    SELECT 1 FROM permission WHERE code = 'role:assign'
);

INSERT INTO permission (name, code, type, url, method, parent_id, sort, status)
SELECT '权限分配', 'system:role:assign-permission', 'api', '/system/permission/assign', 'POST',
       (SELECT id FROM permission WHERE code = 'system:role' LIMIT 1),
       5, 1
WHERE NOT EXISTS (
    SELECT 1 FROM permission WHERE code = 'system:role:assign-permission'
);

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
JOIN permission p ON p.code IN ('role:assign', 'system:role:assign-permission')
WHERE LOWER(r.code) IN ('super_admin', 'admin')
  AND NOT EXISTS (
      SELECT 1
      FROM role_permission rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );
