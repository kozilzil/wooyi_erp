package kr.church.erp.finance.voucher.domain.repository;

import kr.church.erp.finance.voucher.domain.entity.VoucherApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherApprovalHistoryRepository extends JpaRepository<VoucherApprovalHistory, Long> {
}
