package kr.church.erp.finance.service;

import jakarta.transaction.Transactional;
import java.util.Map;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.finance.domain.entity.FinancePeriod;
import kr.church.erp.finance.domain.repository.FinancePeriodRepository;
import kr.church.erp.finance.dto.FinancePeriodCreateRequest;
import kr.church.erp.finance.dto.FinancePeriodResponse;
import kr.church.erp.finance.dto.FinancePeriodUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FinancePeriodService {

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_CLOSED = "CLOSED";

    private final FinancePeriodRepository financePeriodRepository;
    private final AuditLogService auditLogService;

    public FinancePeriodService(FinancePeriodRepository financePeriodRepository, AuditLogService auditLogService) {
        this.financePeriodRepository = financePeriodRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public FinancePeriodResponse create(FinancePeriodCreateRequest request) {
        validateDateRange(request.startDate(), request.endDate());
        String status = normalizeAndValidateStatus(request.status());

        if (financePeriodRepository.existsByFiscalYearAndPeriodNoAndDeletedAtIsNull(request.fiscalYear(), request.periodNo())) {
            throw new IllegalArgumentException("Finance period already exists");
        }

        FinancePeriod financePeriod = FinancePeriod.create(
            request.fiscalYear(),
            request.periodNo(),
            request.startDate(),
            request.endDate(),
            status,
            request.active() == null || request.active()
        );

        FinancePeriod saved = financePeriodRepository.save(financePeriod);
        auditLogService.log("finance", "finance_period", saved.getId(), "CREATE", null, null, snapshot(saved));
        return FinancePeriodResponse.from(saved);
    }

    public Page<FinancePeriodResponse> search(Integer fiscalYear, String status, Boolean active, Pageable pageable) {
        String normalizedStatus = status == null || status.isBlank() ? null : normalizeAndValidateStatus(status);
        return financePeriodRepository.search(fiscalYear, normalizedStatus, active, pageable).map(FinancePeriodResponse::from);
    }

    @Transactional
    public FinancePeriodResponse update(Long id, FinancePeriodUpdateRequest request) {
        validateDateRange(request.startDate(), request.endDate());
        String status = normalizeAndValidateStatus(request.status());

        FinancePeriod financePeriod = financePeriodRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Finance period not found"));

        Map<String, Object> before = snapshot(financePeriod);
        financePeriod.update(
            request.startDate(),
            request.endDate(),
            status,
            request.active() == null || request.active()
        );

        auditLogService.log("finance", "finance_period", financePeriod.getId(), "UPDATE", null, before, snapshot(financePeriod));
        return FinancePeriodResponse.from(financePeriod);
    }

    @Transactional
    public void delete(Long id) {
        FinancePeriod financePeriod = financePeriodRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Finance period not found"));

        Map<String, Object> before = snapshot(financePeriod);
        financePeriod.softDelete();
        auditLogService.log("finance", "finance_period", financePeriod.getId(), "DELETE", null, before, snapshot(financePeriod));
    }

    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
    }

    private String normalizeAndValidateStatus(String value) {
        String normalized = value == null || value.isBlank() ? STATUS_OPEN : value.trim().toUpperCase();
        if (!STATUS_OPEN.equals(normalized) && !STATUS_CLOSED.equals(normalized)) {
            throw new IllegalArgumentException("status must be OPEN or CLOSED");
        }
        return normalized;
    }

    private Map<String, Object> snapshot(FinancePeriod financePeriod) {
        return Map.of(
            "id", financePeriod.getId() == null ? -1 : financePeriod.getId(),
            "fiscalYear", financePeriod.getFiscalYear(),
            "periodNo", financePeriod.getPeriodNo(),
            "startDate", financePeriod.getStartDate().toString(),
            "endDate", financePeriod.getEndDate().toString(),
            "status", financePeriod.getStatus(),
            "active", financePeriod.isActive()
        );
    }
}
