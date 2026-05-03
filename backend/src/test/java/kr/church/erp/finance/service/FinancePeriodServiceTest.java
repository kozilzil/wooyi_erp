package kr.church.erp.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.finance.domain.entity.FinancePeriod;
import kr.church.erp.finance.domain.repository.FinancePeriodRepository;
import kr.church.erp.finance.voucher.domain.repository.VoucherRepository;
import kr.church.erp.finance.dto.FinancePeriodCreateRequest;
import kr.church.erp.finance.dto.FinancePeriodUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinancePeriodServiceTest {

    @Mock
    private FinancePeriodRepository financePeriodRepository;

    @Mock
    private AuditLogService auditLogService;
    @Mock
    private VoucherRepository voucherRepository;

    private FinancePeriodService financePeriodService;

    @BeforeEach
    void setUp() {
        financePeriodService = new FinancePeriodService(financePeriodRepository, voucherRepository, auditLogService);
    }

    @Test
    void createSuccess() {
        FinancePeriod period = FinancePeriod.create(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true);
        when(financePeriodRepository.existsByFiscalYearAndPeriodNoAndDeletedAtIsNull(2026, 1)).thenReturn(false);
        when(financePeriodRepository.save(any(FinancePeriod.class))).thenReturn(period);

        var result = financePeriodService.create(
            new FinancePeriodCreateRequest(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true)
        );

        assertThat(result.fiscalYear()).isEqualTo(2026);
        verify(auditLogService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void createFailWhenDateRangeInvalid() {
        assertThatThrownBy(() -> financePeriodService.create(
            new FinancePeriodCreateRequest(2026, 1, LocalDate.parse("2026-12-31"), LocalDate.parse("2026-01-01"), "OPEN", true)
        )).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("startDate");
    }

    @Test
    void updateFailWhenNotFound() {
        when(financePeriodRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> financePeriodService.update(
            1L,
            new FinancePeriodUpdateRequest(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reopenFailWhenNotAdmin() {
        assertThatThrownBy(() -> financePeriodService.reopen(1L, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("admin-only");
    }

    @Test
    void closeFailWhenPendingVouchersExist() {
        FinancePeriod period = FinancePeriod.create(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true);
        when(financePeriodRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(period));
        when(voucherRepository.existsByPeriodIdAndStatusInAndDeletedAtIsNull(1L, java.util.List.of("DRAFT", "REQUESTED"))).thenReturn(true);

        assertThatThrownBy(() -> financePeriodService.close(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("pending vouchers");
    }
}
