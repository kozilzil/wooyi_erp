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
import kr.church.erp.finance.voucher.domain.entity.VoucherApprovalHistory;
import kr.church.erp.finance.voucher.domain.entity.VoucherLine;
import kr.church.erp.finance.voucher.domain.entity.LedgerEntry;
import kr.church.erp.finance.voucher.domain.repository.LedgerEntryRepository;
import kr.church.erp.finance.voucher.domain.repository.VoucherApprovalHistoryRepository;
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
    private static final String MODE_DOUBLE = "DOUBLE";
    private static final String DC_DEBIT = "DEBIT";
    private static final String DC_CREDIT = "CREDIT";
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_REQUESTED = "REQUESTED";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_CANCELED = "CANCELED";
    private static final String PERIOD_STATUS_CLOSED = "CLOSED";

    private final VoucherRepository voucherRepository;
    private final VoucherLineRepository voucherLineRepository;
    private final VoucherApprovalHistoryRepository voucherApprovalHistoryRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final FinancePeriodRepository financePeriodRepository;
    private final FinanceAccountRepository financeAccountRepository;
    private final AuditLogService auditLogService;

    public VoucherService(
        VoucherRepository voucherRepository,
        VoucherLineRepository voucherLineRepository,
        VoucherApprovalHistoryRepository voucherApprovalHistoryRepository,
        LedgerEntryRepository ledgerEntryRepository,
        FinancePeriodRepository financePeriodRepository,
        FinanceAccountRepository financeAccountRepository,
        AuditLogService auditLogService
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherLineRepository = voucherLineRepository;
        this.voucherApprovalHistoryRepository = voucherApprovalHistoryRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.financePeriodRepository = financePeriodRepository;
        this.financeAccountRepository = financeAccountRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public VoucherResponse create(VoucherCreateRequest request) {
        String bookkeepingMode = normalizeBookkeepingMode(request.bookkeepingMode());
        String voucherType = normalizeVoucherType(request.voucherType());
        FinancePeriod period = loadOpenPeriod(request.periodId());
        validateVoucherDateInPeriod(request.voucherDate(), period);
        validateLines(bookkeepingMode, request.lines());

        long totalAmount = calculateTotalAmount(bookkeepingMode, request.lines());
        String voucherNo = generateVoucherNo(bookkeepingMode, request.voucherDate());
        Voucher voucher = Voucher.create(
            voucherNo,
            bookkeepingMode,
            voucherType,
            request.periodId(),
            request.voucherDate(),
            request.description(),
            totalAmount
        );
        Voucher savedVoucher = voucherRepository.save(voucher);
        List<VoucherLine> savedLines = saveLines(savedVoucher.getId(), bookkeepingMode, request.lines());

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
        validateLines(voucher.getBookkeepingMode(), request.lines());

        VoucherResponse before = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        long totalAmount = calculateTotalAmount(voucher.getBookkeepingMode(), request.lines());
        voucher.update(request.voucherDate(), request.description(), totalAmount);

        voucherLineRepository.deleteByVoucherId(voucher.getId());
        List<VoucherLine> savedLines = saveLines(voucher.getId(), voucher.getBookkeepingMode(), request.lines());

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

    @Transactional
    public VoucherResponse approve(Long id, String comment) {
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        if (!voucher.isRequested()) {
            throw new IllegalArgumentException("Only REQUESTED voucher can be approved");
        }
        loadOpenPeriod(voucher.getPeriodId());

        VoucherResponse before = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        voucher.approve();
        List<VoucherLine> lines = voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId());
        postLedgerEntries(voucher, lines);
        appendHistory(voucher.getId(), "APPROVE", comment);
        VoucherResponse after = buildResponse(voucher, lines);
        auditLogService.log("finance", "voucher", voucher.getId(), "APPROVE", null, snapshot(before), snapshot(after));
        return after;
    }

    @Transactional
    public VoucherResponse reject(Long id, String comment) {
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        if (!voucher.isRequested()) {
            throw new IllegalArgumentException("Only REQUESTED voucher can be rejected");
        }
        loadOpenPeriod(voucher.getPeriodId());

        VoucherResponse before = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        voucher.reject(comment);
        appendHistory(voucher.getId(), "REJECT", comment);
        VoucherResponse after = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        auditLogService.log("finance", "voucher", voucher.getId(), "REJECT", null, snapshot(before), snapshot(after));
        return after;
    }

    @Transactional
    public VoucherResponse cancel(Long id, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("cancel reason is required");
        }

        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        if (!voucher.isApproved()) {
            throw new IllegalArgumentException("Only APPROVED voucher can be canceled");
        }
        loadOpenPeriod(voucher.getPeriodId());

        VoucherResponse before = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        voucher.cancel(reason.trim());
        ledgerEntryRepository.deleteByVoucherId(voucher.getId());
        appendHistory(voucher.getId(), "CANCEL", reason.trim());
        VoucherResponse after = buildResponse(voucher, voucherLineRepository.findByVoucherIdOrderByLineNoAsc(voucher.getId()));
        auditLogService.log("finance", "voucher", voucher.getId(), "CANCEL", null, snapshot(before), snapshot(after));
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

    private void validateLines(String bookkeepingMode, List<VoucherLineRequest> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("lines is required");
        }

        if (MODE_DOUBLE.equals(bookkeepingMode) && lines.size() < 2) {
            throw new IllegalArgumentException("DOUBLE mode requires at least 2 lines");
        }

        long debitSum = 0L;
        long creditSum = 0L;

        for (VoucherLineRequest line : lines) {
            if (line.amount() == null || line.amount() <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            if (MODE_DOUBLE.equals(bookkeepingMode)) {
                String dcType = normalizeDcType(line.dcType());
                if (DC_DEBIT.equals(dcType)) {
                    debitSum += line.amount();
                } else {
                    creditSum += line.amount();
                }
            }
            FinanceAccount account = financeAccountRepository.findByIdAndDeletedAtIsNull(line.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Finance account not found"));
            if (!account.isActive()) {
                throw new IllegalArgumentException("Inactive finance account cannot be used");
            }
        }

        if (MODE_DOUBLE.equals(bookkeepingMode)) {
            if (debitSum == 0 || creditSum == 0) {
                throw new IllegalArgumentException("DOUBLE mode requires both DEBIT and CREDIT");
            }
            if (debitSum != creditSum) {
                throw new IllegalArgumentException("DEBIT total must equal CREDIT total");
            }
        }
    }

    private List<VoucherLine> saveLines(Long voucherId, String bookkeepingMode, List<VoucherLineRequest> requests) {
        List<VoucherLine> lines = new ArrayList<>();
        int lineNo = 1;
        for (VoucherLineRequest request : requests) {
            String dcType = MODE_DOUBLE.equals(bookkeepingMode) ? normalizeDcType(request.dcType()) : null;
            lines.add(VoucherLine.create(
                voucherId,
                lineNo++,
                dcType,
                request.accountId(),
                request.amount(),
                request.description()
            ));
        }
        return voucherLineRepository.saveAll(lines);
    }

    private VoucherResponse buildResponse(Voucher voucher, List<VoucherLine> lines) {
        return VoucherResponse.from(voucher, lines.stream().map(VoucherLineResponse::from).toList());
    }

    private String normalizeVoucherType(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase();
        if (!"INCOME".equals(normalized) && !"EXPENSE".equals(normalized) && !"GENERAL".equals(normalized)) {
            throw new IllegalArgumentException("voucherType must be INCOME, EXPENSE or GENERAL");
        }
        return normalized;
    }

    private String normalizeStatus(String value) {
        String normalized = value.trim().toUpperCase();
        if (!STATUS_DRAFT.equals(normalized) && !STATUS_REQUESTED.equals(normalized)) {
            if (!STATUS_APPROVED.equals(normalized) && !STATUS_REJECTED.equals(normalized) && !STATUS_CANCELED.equals(normalized)) {
                throw new IllegalArgumentException("status must be DRAFT, REQUESTED, APPROVED, REJECTED or CANCELED");
            }
        }
        return normalized;
    }

    private String normalizeBookkeepingMode(String value) {
        String normalized = value == null || value.isBlank() ? MODE_SINGLE : value.trim().toUpperCase();
        if (!MODE_SINGLE.equals(normalized) && !MODE_DOUBLE.equals(normalized)) {
            throw new IllegalArgumentException("bookkeepingMode must be SINGLE or DOUBLE");
        }
        return normalized;
    }

    private String normalizeDcType(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase();
        if (!DC_DEBIT.equals(normalized) && !DC_CREDIT.equals(normalized)) {
            throw new IllegalArgumentException("dcType must be DEBIT or CREDIT");
        }
        return normalized;
    }

    private long calculateTotalAmount(String bookkeepingMode, List<VoucherLineRequest> lines) {
        if (MODE_DOUBLE.equals(bookkeepingMode)) {
            return lines.stream()
                .filter(line -> DC_DEBIT.equals(normalizeDcType(line.dcType())))
                .mapToLong(VoucherLineRequest::amount)
                .sum();
        }
        return lines.stream().mapToLong(VoucherLineRequest::amount).sum();
    }

    private String generateVoucherNo(String bookkeepingMode, LocalDate voucherDate) {
        String prefix = MODE_DOUBLE.equals(bookkeepingMode) ? "DV-" : "SV-";
        return prefix + voucherDate.toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Map<String, Object> snapshot(VoucherResponse response) {
        return Map.of(
            "id", response.id(),
            "voucherNo", response.voucherNo(),
            "voucherType", response.voucherType(),
            "bookkeepingMode", response.bookkeepingMode(),
            "periodId", response.periodId(),
            "voucherDate", response.voucherDate().toString(),
            "status", response.status(),
            "totalAmount", response.totalAmount(),
            "lineCount", response.lines().size()
        );
    }

    private void appendHistory(Long voucherId, String action, String comment) {
        voucherApprovalHistoryRepository.save(VoucherApprovalHistory.create(voucherId, action, null, comment));
    }

    private void postLedgerEntries(Voucher voucher, List<VoucherLine> lines) {
        List<LedgerEntry> entries = lines.stream()
            .map(line -> LedgerEntry.create(
                voucher.getPeriodId(),
                voucher.getId(),
                line.getId(),
                voucher.getVoucherDate(),
                line.getAccountId(),
                line.getDcType(),
                line.getAmount()
            ))
            .toList();
        ledgerEntryRepository.saveAll(entries);
    }
}
