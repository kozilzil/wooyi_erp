INSERT INTO roles (code, name, description)
VALUES
    ('SYS_ADMIN', 'System Admin', 'System administrator'),
    ('MEMBER_ADMIN', 'Member Admin', 'Member module administrator'),
    ('FINANCE_ADMIN', 'Finance Admin', 'Finance module administrator'),
    ('ASSET_ADMIN', 'Asset Admin', 'Asset module administrator')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

INSERT INTO permissions (code, name, module, action)
VALUES
    ('AUTH.LOGIN', 'Login API', 'auth', 'login'),
    ('AUTH.ME', 'Current user API', 'auth', 'read'),
    ('AUTH.LOGOUT', 'Logout API', 'auth', 'logout')
ON DUPLICATE KEY UPDATE name = VALUES(name), module = VALUES(module), action = VALUES(action);

INSERT INTO users (login_id, password_hash, name, status)
VALUES
    ('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi0f6QmZ0dM6S8xR271GGBqBPdZiZsa', '������', 'ACTIVE')
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.code = 'SYS_ADMIN'
WHERE u.login_id = 'admin'
ON DUPLICATE KEY UPDATE user_id = user_id;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'SYS_ADMIN'
ON DUPLICATE KEY UPDATE role_id = role_id;
