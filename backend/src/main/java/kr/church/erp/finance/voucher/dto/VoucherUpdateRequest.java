package kr.church.erp.finance.voucher.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record VoucherUpdateRequest(
    @NotNull(message = "voucherDate is required")
    LocalDate voucherDate,

    String description,

    @NotEmpty(message = "lines is required")
    List<@Valid VoucherLineRequest> lines
) {
}
