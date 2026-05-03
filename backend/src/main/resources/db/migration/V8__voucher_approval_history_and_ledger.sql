ALTER TABLE vouchers
ADD COLUMN approved_at DATETIME NULL AFTER status,
ADD COLUMN rejected_at DATETIME NULL AFTER approved_at,
ADD COLUMN canceled_at DATETIME NULL AFTER rejected_at,
ADD COLUMN cancel_reason VARCHAR(500) NULL AFTER canceled_at;

CREATE TABLE IF NOT EXISTS voucher_approval_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    action VARCHAR(30) NOT NULL,
    actor_id BIGINT NULL,
    comment VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_vah_voucher_id (voucher_id),
    INDEX idx_vah_action (action),
    CONSTRAINT fk_vah_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id)
);

CREATE TABLE IF NOT EXISTS ledger_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    period_id BIGINT NOT NULL,
    voucher_id BIGINT NOT NULL,
    voucher_line_id BIGINT NOT NULL,
    entry_date DATE NOT NULL,
    account_id BIGINT NOT NULL,
    dc_type VARCHAR(10) NULL,
    amount BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ledger_period_account_date (period_id, account_id, entry_date),
    INDEX idx_ledger_voucher_id (voucher_id),
    CONSTRAINT fk_ledger_period FOREIGN KEY (period_id) REFERENCES finance_periods(id),
    CONSTRAINT fk_ledger_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id),
    CONSTRAINT fk_ledger_voucher_line FOREIGN KEY (voucher_line_id) REFERENCES voucher_lines(id),
    CONSTRAINT fk_ledger_account FOREIGN KEY (account_id) REFERENCES finance_accounts(id)
);
