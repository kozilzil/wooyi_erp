package kr.church.erp.finance.dto;

import java.time.LocalDate;
import kr.church.erp.finance.domain.entity.FinancePeriod;

public record FinancePeriodResponse(
    Long id,
    int fiscalYear,
    int periodNo,
    LocalDate startDate,
    LocalDate endDate,
    String status,
    boolean active
) {
    public static FinancePeriodResponse from(FinancePeriod financePeriod) {
        return new FinancePeriodResponse(
            financePeriod.getId(),
            financePeriod.getFiscalYear(),
            financePeriod.getPeriodNo(),
            financePeriod.getStartDate(),
            financePeriod.getEndDate(),
            financePeriod.getStatus(),
            financePeriod.isActive()
        );
    }
}
