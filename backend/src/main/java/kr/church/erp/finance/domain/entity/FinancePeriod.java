package kr.church.erp.finance.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_periods")
public class FinancePeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Column(name = "period_no", nullable = false)
    private int periodNo;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected FinancePeriod() {
    }

    public static FinancePeriod create(
        int fiscalYear,
        int periodNo,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        boolean active
    ) {
        FinancePeriod financePeriod = new FinancePeriod();
        financePeriod.fiscalYear = fiscalYear;
        financePeriod.periodNo = periodNo;
        financePeriod.startDate = startDate;
        financePeriod.endDate = endDate;
        financePeriod.status = status;
        financePeriod.active = active;
        financePeriod.createdAt = LocalDateTime.now();
        return financePeriod;
    }

    public void update(LocalDate startDate, LocalDate endDate, String status, boolean active) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    public void close() {
        this.status = "CLOSED";
        this.updatedAt = LocalDateTime.now();
    }

    public void reopen() {
        this.status = "OPEN";
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public int getPeriodNo() {
        return periodNo;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public boolean isActive() {
        return active;
    }
}
