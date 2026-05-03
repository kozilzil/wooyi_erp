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

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

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
        String bookkeepingMode,
        String voucherType,
        Long periodId,
        LocalDate voucherDate,
        String description,
        long totalAmount
    ) {
        Voucher voucher = new Voucher();
        voucher.voucherNo = voucherNo;
        voucher.voucherType = voucherType;
        voucher.bookkeepingMode = bookkeepingMode;
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
        if (!isDraft()) {
            throw new IllegalStateException("Only DRAFT voucher can be requested");
        }
        this.status = "REQUESTED";
        this.updatedAt = LocalDateTime.now();
    }

    public void approve() {
        if (!"REQUESTED".equals(this.status)) {
            throw new IllegalStateException("Only REQUESTED voucher can be approved");
        }
        this.status = "APPROVED";
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String comment) {
        if (!"REQUESTED".equals(this.status)) {
            throw new IllegalStateException("Only REQUESTED voucher can be rejected");
        }
        this.status = "REJECTED";
        this.rejectedAt = LocalDateTime.now();
        this.cancelReason = comment;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (!"APPROVED".equals(this.status)) {
            throw new IllegalStateException("Only APPROVED voucher can be canceled");
        }
        this.status = "CANCELED";
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDraft() {
        return "DRAFT".equals(this.status);
    }

    public boolean isRequested() {
        return "REQUESTED".equals(this.status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(this.status);
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
