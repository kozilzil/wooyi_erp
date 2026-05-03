package kr.church.erp.finance.voucher.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period_id", nullable = false)
    private Long periodId;

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    @Column(name = "voucher_line_id", nullable = false)
    private Long voucherLineId;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "dc_type", length = 10)
    private String dcType;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected LedgerEntry() {}

    public static LedgerEntry create(
        Long periodId,
        Long voucherId,
        Long voucherLineId,
        LocalDate entryDate,
        Long accountId,
        String dcType,
        long amount
    ) {
        LedgerEntry entry = new LedgerEntry();
        entry.periodId = periodId;
        entry.voucherId = voucherId;
        entry.voucherLineId = voucherLineId;
        entry.entryDate = entryDate;
        entry.accountId = accountId;
        entry.dcType = dcType;
        entry.amount = amount;
        entry.createdAt = LocalDateTime.now();
        return entry;
    }
}
