package kr.church.erp.finance.voucher.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_lines")
public class VoucherLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    @Column(name = "line_no", nullable = false)
    private int lineNo;

    @Column(name = "dc_type", length = 10)
    private String dcType;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected VoucherLine() {
    }

    public static VoucherLine create(
        Long voucherId,
        int lineNo,
        String dcType,
        Long accountId,
        long amount,
        String description
    ) {
        VoucherLine line = new VoucherLine();
        line.voucherId = voucherId;
        line.lineNo = lineNo;
        line.dcType = dcType;
        line.accountId = accountId;
        line.amount = amount;
        line.description = description;
        line.createdAt = LocalDateTime.now();
        return line;
    }

    public Long getId() {
        return id;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getDcType() {
        return dcType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public long getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
