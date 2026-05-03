package kr.church.erp.finance.voucher.domain.repository;

import kr.church.erp.finance.voucher.domain.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    long countByVoucherId(Long voucherId);
    void deleteByVoucherId(Long voucherId);
}
