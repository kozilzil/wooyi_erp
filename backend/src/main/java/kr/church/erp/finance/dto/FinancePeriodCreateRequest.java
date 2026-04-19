package kr.church.erp.finance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FinancePeriodCreateRequest(
    @NotNull(message = "fiscalYear is required")
    @Min(value = 1900, message = "fiscalYear must be >= 1900")
    @Max(value = 2999, message = "fiscalYear must be <= 2999")
    Integer fiscalYear,

    @NotNull(message = "periodNo is required")
    @Min(value = 1, message = "periodNo must be >= 1")
    Integer periodNo,

    @NotNull(message = "startDate is required")
    LocalDate startDate,

    @NotNull(message = "endDate is required")
    LocalDate endDate,

    String status,
    Boolean active
) {
}
