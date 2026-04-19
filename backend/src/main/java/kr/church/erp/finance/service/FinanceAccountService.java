package kr.church.erp.finance.service;

import jakarta.transaction.Transactional;
import java.util.Map;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.finance.domain.entity.FinanceAccount;
import kr.church.erp.finance.domain.repository.FinanceAccountRepository;
import kr.church.erp.finance.dto.FinanceAccountCreateRequest;
import kr.church.erp.finance.dto.FinanceAccountResponse;
import kr.church.erp.finance.dto.FinanceAccountUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FinanceAccountService {

    private final FinanceAccountRepository financeAccountRepository;
    private final AuditLogService auditLogService;

    public FinanceAccountService(FinanceAccountRepository financeAccountRepository, AuditLogService auditLogService) {
        this.financeAccountRepository = financeAccountRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public FinanceAccountResponse create(FinanceAccountCreateRequest request) {
        String accountCode = request.accountCode().trim().toUpperCase();
        String accountType = request.accountType().trim().toUpperCase();
        validateParentId(request.parentId());

        if (financeAccountRepository.existsByAccountCodeAndDeletedAtIsNull(accountCode)) {
            throw new IllegalArgumentException("Finance account code already exists");
        }

        FinanceAccount financeAccount = FinanceAccount.create(
            accountCode,
            request.accountName().trim(),
            accountType,
            request.parentId(),
            request.active() == null || request.active()
        );

        FinanceAccount saved = financeAccountRepository.save(financeAccount);
        auditLogService.log("finance", "finance_account", saved.getId(), "CREATE", null, null, snapshot(saved));
        return FinanceAccountResponse.from(saved);
    }

    public Page<FinanceAccountResponse> search(String accountType, Boolean active, String keyword, Pageable pageable) {
        String normalizedType = accountType == null || accountType.isBlank() ? null : accountType.trim().toUpperCase();
        return financeAccountRepository.search(normalizedType, active, keyword, pageable).map(FinanceAccountResponse::from);
    }

    @Transactional
    public FinanceAccountResponse update(Long id, FinanceAccountUpdateRequest request) {
        String accountType = request.accountType().trim().toUpperCase();
        validateParentId(request.parentId());

        FinanceAccount financeAccount = financeAccountRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Finance account not found"));

        if (request.parentId() != null && request.parentId().equals(id)) {
            throw new IllegalArgumentException("parentId cannot be same as id");
        }

        Map<String, Object> before = snapshot(financeAccount);
        financeAccount.update(
            request.accountName().trim(),
            accountType,
            request.parentId(),
            request.active() == null || request.active()
        );

        auditLogService.log("finance", "finance_account", financeAccount.getId(), "UPDATE", null, before, snapshot(financeAccount));
        return FinanceAccountResponse.from(financeAccount);
    }

    @Transactional
    public void delete(Long id) {
        FinanceAccount financeAccount = financeAccountRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Finance account not found"));

        Map<String, Object> before = snapshot(financeAccount);
        financeAccount.softDelete();
        auditLogService.log("finance", "finance_account", financeAccount.getId(), "DELETE", null, before, snapshot(financeAccount));
    }

    private void validateParentId(Long parentId) {
        if (parentId == null) {
            return;
        }
        financeAccountRepository.findByIdAndDeletedAtIsNull(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent finance account not found"));
    }

    private Map<String, Object> snapshot(FinanceAccount financeAccount) {
        return Map.of(
            "id", financeAccount.getId() == null ? -1 : financeAccount.getId(),
            "accountCode", financeAccount.getAccountCode(),
            "accountName", financeAccount.getAccountName(),
            "accountType", financeAccount.getAccountType(),
            "parentId", financeAccount.getParentId() == null ? -1 : financeAccount.getParentId(),
            "active", financeAccount.isActive()
        );
    }
}
