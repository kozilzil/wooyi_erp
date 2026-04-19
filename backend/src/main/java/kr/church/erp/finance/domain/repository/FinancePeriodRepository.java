package kr.church.erp.finance.domain.repository;

import java.util.Optional;
import kr.church.erp.finance.domain.entity.FinancePeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinancePeriodRepository extends JpaRepository<FinancePeriod, Long> {

    Optional<FinancePeriod> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByFiscalYearAndPeriodNoAndDeletedAtIsNull(int fiscalYear, int periodNo);

    @Query("""
        select p from FinancePeriod p
        where p.deletedAt is null
          and (:fiscalYear is null or p.fiscalYear = :fiscalYear)
          and (:status is null or p.status = :status)
          and (:active is null or p.active = :active)
        order by p.fiscalYear desc, p.periodNo desc
    """)
    Page<FinancePeriod> search(
        @Param("fiscalYear") Integer fiscalYear,
        @Param("status") String status,
        @Param("active") Boolean active,
        Pageable pageable
    );
}
