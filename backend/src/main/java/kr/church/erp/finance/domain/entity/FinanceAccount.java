package kr.church.erp.finance.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_accounts")
public class FinanceAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_code", nullable = false, unique = true, length = 50)
    private String accountCode;

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Column(name = "account_type", nullable = false, length = 30)
    private String accountType;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected FinanceAccount() {
    }

    public static FinanceAccount create(
        String accountCode,
        String accountName,
        String accountType,
        Long parentId,
        boolean active
    ) {
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.accountCode = accountCode;
        financeAccount.accountName = accountName;
        financeAccount.accountType = accountType;
        financeAccount.parentId = parentId;
        financeAccount.active = active;
        financeAccount.createdAt = LocalDateTime.now();
        return financeAccount;
    }

    public void update(String accountName, String accountType, Long parentId, boolean active) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.parentId = parentId;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public Long getParentId() {
        return parentId;
    }

    public boolean isActive() {
        return active;
    }
}
