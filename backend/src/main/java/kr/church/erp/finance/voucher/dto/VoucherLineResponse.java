package kr.church.erp.finance.voucher.dto;

import kr.church.erp.finance.voucher.domain.entity.VoucherLine;

public record VoucherLineResponse(
    Long id,
    int lineNo,
    Long accountId,
    long amount,
    String description
) {
    public static VoucherLineResponse from(VoucherLine line) {
        return new VoucherLineResponse(
            line.getId(),
            line.getLineNo(),
            line.getAccountId(),
            line.getAmount(),
            line.getDescription()
        );
    }
}
