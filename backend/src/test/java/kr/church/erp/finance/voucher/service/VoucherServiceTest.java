package kr.church.erp.finance.voucher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.finance.domain.entity.FinanceAccount;
import kr.church.erp.finance.domain.entity.FinancePeriod;
import kr.church.erp.finance.domain.repository.FinanceAccountRepository;
import kr.church.erp.finance.domain.repository.FinancePeriodRepository;
import kr.church.erp.finance.voucher.domain.entity.Voucher;
import kr.church.erp.finance.voucher.domain.repository.VoucherLineRepository;
import kr.church.erp.finance.voucher.domain.repository.VoucherRepository;
import kr.church.erp.finance.voucher.dto.VoucherCreateRequest;
import kr.church.erp.finance.voucher.dto.VoucherLineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;
    @Mock
    private VoucherLineRepository voucherLineRepository;
    @Mock
    private FinancePeriodRepository financePeriodRepository;
    @Mock
    private FinanceAccountRepository financeAccountRepository;
    @Mock
    private AuditLogService auditLogService;

    private VoucherService voucherService;

    @BeforeEach
    void setUp() {
        voucherService = new VoucherService(
            voucherRepository,
            voucherLineRepository,
            financePeriodRepository,
            financeAccountRepository,
            auditLogService
        );
    }

    @Test
    void createSingleSuccess() throws Exception {
        FinancePeriod period = FinancePeriod.create(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true);
        FinanceAccount account = FinanceAccount.create("1100", "Cash", "ASSET", null, true);
        Voucher voucher = Voucher.create("SV-1", "SINGLE", "INCOME", 1L, LocalDate.parse("2026-04-01"), "offering", 1000L);
        setId(voucher, 1L);

        when(financePeriodRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(period));
        when(financeAccountRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(account));
        when(voucherRepository.save(any(Voucher.class))).thenReturn(voucher);
        when(voucherLineRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = voucherService.create(new VoucherCreateRequest(
            "SINGLE",
            "INCOME",
            1L,
            LocalDate.parse("2026-04-01"),
            "offering",
            List.of(new VoucherLineRequest(null, 10L, 1000L, "sunday offering"))
        ));

        assertThat(result.status()).isEqualTo("DRAFT");
        assertThat(result.bookkeepingMode()).isEqualTo("SINGLE");
        assertThat(result.totalAmount()).isEqualTo(1000L);
        verify(auditLogService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void createFailWhenPeriodClosed() {
        FinancePeriod period = FinancePeriod.create(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "CLOSED", true);
        when(financePeriodRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(period));

        assertThatThrownBy(() -> voucherService.create(new VoucherCreateRequest(
            "SINGLE",
            "INCOME",
            1L,
            LocalDate.parse("2026-04-01"),
            "offering",
            List.of(new VoucherLineRequest(null, 10L, 1000L, "sunday offering"))
        ))).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Closed period");
    }

    @Test
    void requestApprovalSuccess() throws Exception {
        FinancePeriod period = FinancePeriod.create(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true);
        Voucher voucher = Voucher.create("SV-1", "SINGLE", "INCOME", 1L, LocalDate.parse("2026-04-01"), "offering", 1000L);
        setId(voucher, 1L);

        when(voucherRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(voucher));
        when(financePeriodRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(period));
        when(voucherLineRepository.findByVoucherIdOrderByLineNoAsc(1L)).thenReturn(List.of());

        var result = voucherService.requestApproval(1L);

        assertThat(result.status()).isEqualTo("REQUESTED");
        verify(auditLogService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void createDoubleFailWhenDebitCreditNotEqual() {
        FinancePeriod period = FinancePeriod.create(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true);
        FinanceAccount debit = FinanceAccount.create("5100", "Expense", "EXPENSE", null, true);
        FinanceAccount credit = FinanceAccount.create("1100", "Cash", "ASSET", null, true);
        when(financePeriodRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(period));
        when(financeAccountRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(debit));
        when(financeAccountRepository.findByIdAndDeletedAtIsNull(11L)).thenReturn(Optional.of(credit));

        assertThatThrownBy(() -> voucherService.create(new VoucherCreateRequest(
            "DOUBLE",
            "GENERAL",
            1L,
            LocalDate.parse("2026-04-01"),
            "office supplies",
            List.of(
                new VoucherLineRequest("DEBIT", 10L, 1200L, "debit"),
                new VoucherLineRequest("CREDIT", 11L, 1000L, "credit")
            )
        ))).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("DEBIT total must equal CREDIT total");
    }

    private static void setId(Voucher voucher, Long id) throws Exception {
        Field field = Voucher.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(voucher, id);
    }
}
