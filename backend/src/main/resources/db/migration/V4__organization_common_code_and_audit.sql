CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT NULL,
    type VARCHAR(50) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    INDEX idx_organizations_parent_id (parent_id),
    INDEX idx_organizations_active (active),
    CONSTRAINT fk_organizations_parent FOREIGN KEY (parent_id) REFERENCES organizations(id)
);

CREATE TABLE IF NOT EXISTS common_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_code VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    UNIQUE KEY uq_common_codes_group_code_code (group_code, code),
    INDEX idx_common_codes_group_code (group_code),
    INDEX idx_common_codes_active (active)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_name VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    action VARCHAR(50) NOT NULL,
    actor_id BIGINT NULL,
    payload_before LONGTEXT NULL,
    payload_after LONGTEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_logs_module_name (module_name),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_created_at (created_at)
);

INSERT INTO common_codes (group_code, code, name, sort_order, active, description)
VALUES
    ('ORG_TYPE', 'DEPARTMENT', '�μ�', 1, TRUE, '���� ���� - �μ�'),
    ('ORG_TYPE', 'TEAM', '��', 2, TRUE, '���� ���� - ��'),
    ('ASSET_STATUS', 'NORMAL', '����', 1, TRUE, '��ǰ ���� - ����'),
    ('ASSET_STATUS', 'REPAIR', '������', 2, TRUE, '��ǰ ���� - ������')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort_order = VALUES(sort_order),
    active = VALUES(active),
    description = VALUES(description);
