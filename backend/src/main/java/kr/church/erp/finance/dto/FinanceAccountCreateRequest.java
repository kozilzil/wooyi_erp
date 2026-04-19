package kr.church.erp.finance.dto;

import jakarta.validation.constraints.NotBlank;

public record FinanceAccountCreateRequest(
    @NotBlank(message = "accountCode is required")
    String accountCode,

    @NotBlank(message = "accountName is required")
    String accountName,

    @NotBlank(message = "accountType is required")
    String accountType,

    Long parentId,
    Boolean active
) {
}
