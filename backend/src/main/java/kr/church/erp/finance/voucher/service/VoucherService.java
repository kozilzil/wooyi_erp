package kr.church.erp.finance.voucher.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.finance.domain.entity.FinanceAccount;
import kr.church.erp.finance.domain.entity.FinancePeriod;
import kr.church.erp.finance.domain.repository.FinanceAccountRepository;
import kr.church.erp.finance.domain.repository.FinancePeriodRepository;
import kr.church.erp.finance.voucher.domain.entity.Voucher;
import kr.church.erp.finance.voucher.domain.entity.VoucherLine;
import kr.church.erp.finance.voucher.domain.repository.VoucherLineRepository;
import kr.church.erp.finance.voucher.domain.repository.VoucherRepository;
import kr.church.erp.finance.voucher.dto.VoucherCreateRequest;
import kr.church.erp.finance.voucher.dto.VoucherLineRequest;
import kr.church.erp.finance.voucher.dto.VoucherLineResponse;
import kr.church.erp.finance.voucher.dto.VoucherResponse;
import kr.church.erp.finance.voucher.dto.VoucherUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VoucherService {

    private static final String MODE_SINGLE = "SINGLE";
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_REQUESTED = "REQUESTED";
    private static final String PERIOD_STATUS_CLOSED = "CLOSED";

    private final VoucherRepository voucherRepository;
    private final VoucherLineRepository voucherLineRepository;
    private final FinancePeriodRepository financePeriodRepository;
    private final FinanceAccountRepository financeAccountRepository;
    private final AuditLogService auditLogService;

    public VoucherService(
        VoucherRepository voucherRepository,
        VoucherLineRepository voucherLineRepository,
        FinancePeriodRepository financePeriodRepository,
        FinanceAccountRepository financeAccountRepository,
        AuditLogService auditLogService
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherLineRepository = voucherLineRepository;
        this.financePeriodRepository = financePeriodRepository;
        this.financeAccountRepository = financeAccountRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public VoucherResponse create(VoucherCreateRequest request) {
        String voucherType = normalizeVoucherType(request.voucherType());
        FinancePeriod period = loadOpenPeriod(request.periodId());
        validateVoucherDateInPeriod(request.voucherDate(), period);
        validateLines(request.lines());

        long totalAmount = request.lines().stream().mapToLong(VoucherLineRequest::amount).sum();
        String voucherNo = generateVoucherNo(request.voucherDate());
        Voucher voucher = Voucher.create(
            voucherNo,
            voucherType,
            request.periodId(),
            request.voucherDate(),
            request.description(),
            totalAmount
        );
        Voucher savedVoucher = voucherRepository.save(voucher);
        List<VoucherLine> savedLines = saveLines(savedVoucher.getId(), request.lines());

        VoucherResponse response = buildResponse(savedVoucher, savedLines);
        auditLogService.log("finance", "voucher", savedVoucher.getId(), "CREATE", null, null, snapshot(response));
        return response;
    }

    public Page<VoucherResponse> search(
        Long periodId,
        String voucherType,
        String status,
        LocalDate fromDate,
        LocalDate toDate,
        Pageable pageable
    ) {
        String normalizedType = voucherType == null || voucherType.isBlank() ? null : normalizeVoucherType(voucherType);
        String normalizedStatus = status == null || status.isBlank() ? null : normalizeStatus(status);

        return voucherRepository.search(periodId, normalizedType, normalizedStatus, fromDate, toDate, pageable)
            .map(voucher -> buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId())));
    }

    public VoucherResponse get(Long id) {
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        List<VoucherLine> lines = voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId());
        return buildResponse(voucher, lines);
    }

    @Transactional
    public VoucherResponse update(Long id, VoucherUpdateRequest request) {
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        ensureDraft(voucher);

        FinancePeriod period = loadOpenPeriod(voucher.getPeriodId());
        validateVoucherDateInPeriod(request.voucherDate(), period);
        validateLines(request.lines());

        VoucherResponse before = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        long totalAmount = request.lines().stream().mapToLong(VoucherLineRequest::amount).sum();
        voucher.update(request.voucherDate(), request.description(), totalAmount);

        voucherLineRepository.deleteByVoucherId(voucher.getId());
        List<VoucherLine> savedLines = saveLines(voucher.getId(), request.lines());

        VoucherResponse after = buildResponse(voucher, savedLines);
        auditLogService.log("finance", "voucher", voucher.getId(), "UPDATE", null, snapshot(before), snapshot(after));
        return after;
    }

    @Transactional
    public void delete(Long id) {
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        ensureDraft(voucher);

        VoucherResponse before = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        voucher.softDelete();
        auditLogService.log("finance", "voucher", voucher.getId(), "DELETE", null, snapshot(before), null);
    }

    @Transactional
    public VoucherResponse requestApproval(Long id) {
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        ensureDraft(voucher);

        FinancePeriod period = loadOpenPeriod(voucher.getPeriodId());
        validateVoucherDateInPeriod(voucher.getVoucherDate(), period);

        VoucherResponse before = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        voucher.requestApproval();
        VoucherResponse after = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        auditLogService.log("finance", "voucher", voucher.getId(), "REQUEST_APPROVAL", null, snapshot(before), snapshot(after));
        return after;
    }

    private void ensureDraft(Voucher voucher) {
        if (!voucher.isDraft()) {
            throw new IllegalArgumentException("Only DRAFT voucher can be modified");
        }
    }

    private FinancePeriod loadOpenPeriod(Long periodId) {
        FinancePeriod period = financePeriodRepository.findByIdAndDeletedAtIsNull(periodId)
            .orElseThrow(() -> new IllegalArgumentException("Finance period not found"));
        if (PERIOD_STATUS_CLOSED.equals(period.getStatus())) {
            throw new IllegalArgumentException("Closed period cannot accept voucher");
        }
        return period;
    }

    private void validateVoucherDateInPeriod(LocalDate voucherDate, FinancePeriod period) {
        if (voucherDate.isBefore(period.getStartDate()) || voucherDate.isAfter(period.getEndDate())) {
            throw new IllegalArgumentException("voucherDate must be inside period range");
        }
    }

    private void validateLines(List<VoucherLineRequest> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("lines is required");
        }

        for (VoucherLineRequest line : lines) {
            if (line.amount() == null || line.amount() <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            FinanceAccount account = financeAccountRepository.findByIdAndDeletedAtIsNull(line.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Finance account not found"));
            if (!account.isActive()) {
                throw new IllegalArgumentException("Inactive finance account cannot be used");
            }
        }
    }

    private List<VoucherLine> saveLines(Long voucherId, List<VoucherLineRequest> requests) {
        List<VoucherLine> lines = new ArrayList<>();
        int lineNo = 1;
        for (VoucherLineRequest request : requests) {
            lines.add(VoucherLine.create(voucherId, lineNo++, request.accountId(), request.amount(), request.description()));
        }
        return voucherLineRepository.saveAll(lines);
    }

    private VoucherResponse buildResponse(Voucher voucher, List<VoucherLine> lines) {
        return VoucherResponse.from(voucher, lines.stream().map(VoucherLineResponse::from).toList());
    }

    private String normalizeVoucherType(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase();
        if (!"INCOME".equals(normalized) && !"EXPENSE".equals(normalized)) {
            throw new IllegalArgumentException("voucherType must be INCOME or EXPENSE");
        }
        return normalized;
    }

    private String normalizeStatus(String value) {
        String normalized = value.trim().toUpperCase();
        if (!STATUS_DRAFT.equals(normalized) && !STATUS_REQUESTED.equals(normalized)) {
            throw new IllegalArgumentException("status must be DRAFT or REQUESTED");
        }
        return normalized;
    }

    private String generateVoucherNo(LocalDate voucherDate) {
        return "SV-" + voucherDate.toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Map<String, Object> snapshot(VoucherResponse response) {
        return Map.of(
            "id", response.id(),
            "voucherNo", response.voucherNo(),
            "voucherType", response.voucherType(),
            "bookkeepingMode", MODE_SINGLE,
            "periodId", response.periodId(),
            "voucherDate", response.voucherDate().toString(),
            "status", response.status(),
            "totalAmount", response.totalAmount(),
            "lineCount", response.lines().size()
        );
    }
}
