package kr.church.erp.finance.dto;

import kr.church.erp.finance.domain.entity.FinanceAccount;

public record FinanceAccountResponse(
    Long id,
    String accountCode,
    String accountName,
    String accountType,
    Long parentId,
    boolean active
) {
    public static FinanceAccountResponse from(FinanceAccount financeAccount) {
        return new FinanceAccountResponse(
            financeAccount.getId(),
            financeAccount.getAccountCode(),
            financeAccount.getAccountName(),
            financeAccount.getAccountType(),
            financeAccount.getParentId(),
            financeAccount.isActive()
        );
    }
}
