package kr.church.erp.finance.voucher.domain.repository;

import java.util.List;
import kr.church.erp.finance.voucher.domain.entity.VoucherLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherLineRepository extends JpaRepository<VoucherLine, Long> {

    List<VoucherLine> findByVoucherIdOrderByLineNoAsc(Long voucherId);

    void deleteByVoucherId(Long voucherId);
}
