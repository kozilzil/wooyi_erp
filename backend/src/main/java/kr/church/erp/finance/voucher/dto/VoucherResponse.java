package kr.church.erp.finance.voucher.dto;

import java.time.LocalDate;
import java.util.List;
import kr.church.erp.finance.voucher.domain.entity.Voucher;

public record VoucherResponse(
    Long id,
    String voucherNo,
    String voucherType,
    String bookkeepingMode,
    Long periodId,
    LocalDate voucherDate,
    String status,
    String description,
    long totalAmount,
    List<VoucherLineResponse> lines
) {
    public static VoucherResponse from(Voucher voucher, List<VoucherLineResponse> lines) {
        return new VoucherResponse(
            voucher.getId(),
            voucher.getVoucherNo(),
            voucher.getVoucherType(),
            voucher.getBookkeepingMode(),
            voucher.getPeriodId(),
            voucher.getVoucherDate(),
            voucher.getStatus(),
            voucher.getDescription(),
            voucher.getTotalAmount(),
            lines
        );
    }
}
