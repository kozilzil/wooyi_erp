package kr.church.erp.finance.voucher.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import kr.church.erp.finance.voucher.domain.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Optional<Voucher> findByIdAndDeletedAtIsNull(Long id);

    @Query("""
        select v from Voucher v
        where v.deletedAt is null
          and (:periodId is null or v.periodId = :periodId)
          and (:voucherType is null or v.voucherType = :voucherType)
          and (:status is null or v.status = :status)
          and (:fromDate is null or v.voucherDate >= :fromDate)
          and (:toDate is null or v.voucherDate <= :toDate)
        order by v.id desc
    """)
    Page<Voucher> search(
        @Param("periodId") Long periodId,
        @Param("voucherType") String voucherType,
        @Param("status") String status,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        Pageable pageable
    );
}
