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
@Table(name = "vouchers")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_no", nullable = false, unique = true, length = 40)
    private String voucherNo;

    @Column(name = "voucher_type", nullable = false, length = 20)
    private String voucherType;

    @Column(name = "bookkeeping_mode", nullable = false, length = 20)
    private String bookkeepingMode;

    @Column(name = "period_id", nullable = false)
    private Long periodId;

    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Voucher() {
    }

    public static Voucher create(
        String voucherNo,
        String voucherType,
        Long periodId,
        LocalDate voucherDate,
        String description,
        long totalAmount
    ) {
        Voucher voucher = new Voucher();
        voucher.voucherNo = voucherNo;
        voucher.voucherType = voucherType;
        voucher.bookkeepingMode = "SINGLE";
        voucher.periodId = periodId;
        voucher.voucherDate = voucherDate;
        voucher.status = "DRAFT";
        voucher.description = description;
        voucher.totalAmount = totalAmount;
        voucher.createdAt = LocalDateTime.now();
        return voucher;
    }

    public void update(LocalDate voucherDate, String description, long totalAmount) {
        this.voucherDate = voucherDate;
        this.description = description;
        this.totalAmount = totalAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public void requestApproval() {
        this.status = "REQUESTED";
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDraft() {
        return "DRAFT".equals(this.status);
    }

    public Long getId() {
        return id;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public String getBookkeepingMode() {
        return bookkeepingMode;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public LocalDate getVoucherDate() {
        return voucherDate;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public long getTotalAmount() {
        return totalAmount;
    }
}
