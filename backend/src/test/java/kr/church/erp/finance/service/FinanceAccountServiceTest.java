package kr.church.erp.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.finance.domain.entity.FinanceAccount;
import kr.church.erp.finance.domain.repository.FinanceAccountRepository;
import kr.church.erp.finance.dto.FinanceAccountCreateRequest;
import kr.church.erp.finance.dto.FinanceAccountUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinanceAccountServiceTest {

    @Mock
    private FinanceAccountRepository financeAccountRepository;

    @Mock
    private AuditLogService auditLogService;

    private FinanceAccountService financeAccountService;

    @BeforeEach
    void setUp() {
        financeAccountService = new FinanceAccountService(financeAccountRepository, auditLogService);
    }

    @Test
    void createSuccess() {
        FinanceAccount account = FinanceAccount.create("1100", "Cash", "ASSET", null, true);
        when(financeAccountRepository.existsByAccountCodeAndDeletedAtIsNull("1100")).thenReturn(false);
        when(financeAccountRepository.save(any(FinanceAccount.class))).thenReturn(account);

        var result = financeAccountService.create(new FinanceAccountCreateRequest("1100", "Cash", "ASSET", null, true));

        assertThat(result.accountCode()).isEqualTo("1100");
        verify(auditLogService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void createFailWhenDuplicateCode() {
        when(financeAccountRepository.existsByAccountCodeAndDeletedAtIsNull("1100")).thenReturn(true);

        assertThatThrownBy(() -> financeAccountService.create(
            new FinanceAccountCreateRequest("1100", "Cash", "ASSET", null, true)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateFailWhenNotFound() {
        when(financeAccountRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> financeAccountService.update(
            1L,
            new FinanceAccountUpdateRequest("Cash", "ASSET", null, true)
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
