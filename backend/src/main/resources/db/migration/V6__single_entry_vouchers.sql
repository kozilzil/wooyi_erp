CREATE TABLE IF NOT EXISTS vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_no VARCHAR(40) NOT NULL UNIQUE,
    voucher_type VARCHAR(20) NOT NULL,
    bookkeeping_mode VARCHAR(20) NOT NULL,
    period_id BIGINT NOT NULL,
    voucher_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    description VARCHAR(500) NULL,
    total_amount BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    INDEX idx_vouchers_period_id (period_id),
    INDEX idx_vouchers_status (status),
    INDEX idx_vouchers_voucher_date (voucher_date),
    CONSTRAINT fk_vouchers_period FOREIGN KEY (period_id) REFERENCES finance_periods(id)
);

CREATE TABLE IF NOT EXISTS voucher_lines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    line_no INT NOT NULL,
    account_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    description VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_voucher_lines_voucher_line_no (voucher_id, line_no),
    INDEX idx_voucher_lines_voucher_id (voucher_id),
    CONSTRAINT fk_voucher_lines_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id),
    CONSTRAINT fk_voucher_lines_account FOREIGN KEY (account_id) REFERENCES finance_accounts(id)
);
