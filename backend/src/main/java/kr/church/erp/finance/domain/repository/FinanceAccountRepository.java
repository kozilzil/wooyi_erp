package kr.church.erp.finance.domain.repository;

import java.util.Optional;
import kr.church.erp.finance.domain.entity.FinanceAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinanceAccountRepository extends JpaRepository<FinanceAccount, Long> {

    Optional<FinanceAccount> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByAccountCodeAndDeletedAtIsNull(String accountCode);

    @Query("""
        select a from FinanceAccount a
        where a.deletedAt is null
          and (:accountType is null or a.accountType = :accountType)
          and (:active is null or a.active = :active)
          and (:keyword is null or lower(a.accountCode) like lower(concat('%', :keyword, '%'))
                            or lower(a.accountName) like lower(concat('%', :keyword, '%')))
        order by a.accountCode asc
    """)
    Page<FinanceAccount> search(
        @Param("accountType") String accountType,
        @Param("active") Boolean active,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
