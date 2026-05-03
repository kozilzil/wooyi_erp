ALTER TABLE voucher_lines
ADD COLUMN dc_type VARCHAR(10) NULL AFTER line_no;

CREATE INDEX idx_voucher_lines_dc_type ON voucher_lines(dc_type);
