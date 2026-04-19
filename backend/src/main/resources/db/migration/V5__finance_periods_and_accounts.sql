CREATE TABLE IF NOT EXISTS finance_periods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fiscal_year INT NOT NULL,
    period_no INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    UNIQUE KEY uq_finance_periods_year_no (fiscal_year, period_no),
    INDEX idx_finance_periods_status (status),
    INDEX idx_finance_periods_active (active),
    INDEX idx_finance_periods_start_date (start_date)
);

CREATE TABLE IF NOT EXISTS finance_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_code VARCHAR(50) NOT NULL UNIQUE,
    account_name VARCHAR(100) NOT NULL,
    account_type VARCHAR(30) NOT NULL,
    parent_id BIGINT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    INDEX idx_finance_accounts_parent_id (parent_id),
    INDEX idx_finance_accounts_account_type (account_type),
    INDEX idx_finance_accounts_active (active),
    CONSTRAINT fk_finance_accounts_parent FOREIGN KEY (parent_id) REFERENCES finance_accounts(id)
);

INSERT INTO finance_periods (fiscal_year, period_no, start_date, end_date, status, active)
VALUES (2026, 1, '2026-01-01', '2026-12-31', 'OPEN', TRUE)
ON DUPLICATE KEY UPDATE
    start_date = VALUES(start_date),
    end_date = VALUES(end_date),
    status = VALUES(status),
    active = VALUES(active);

INSERT INTO finance_accounts (account_code, account_name, account_type, parent_id, active)
VALUES
    ('1100', 'Cash', 'ASSET', NULL, TRUE),
    ('4100', 'Offering Income', 'REVENUE', NULL, TRUE)
ON DUPLICATE KEY UPDATE
    account_name = VALUES(account_name),
    account_type = VALUES(account_type),
    active = VALUES(active);
