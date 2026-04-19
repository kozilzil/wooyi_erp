package kr.church.erp.finance.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FinancePeriodUpdateRequest(
    @NotNull(message = "startDate is required")
    LocalDate startDate,

    @NotNull(message = "endDate is required")
    LocalDate endDate,

    String status,
    Boolean active
) {
}
