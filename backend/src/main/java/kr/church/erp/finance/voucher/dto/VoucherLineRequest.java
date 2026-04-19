package kr.church.erp.finance.voucher.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VoucherLineRequest(
    @NotNull(message = "accountId is required")
    Long accountId,

    @NotNull(message = "amount is required")
    @Min(value = 1, message = "amount must be positive")
    Long amount,

    String description
) {
}
