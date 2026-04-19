package kr.church.erp.organization.domain.repository;

import kr.church.erp.organization.domain.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByCodeAndDeletedAtIsNull(String code);

    @Query("""
        select o from Organization o
        where o.deletedAt is null
          and (:keyword is null or lower(o.code) like lower(concat('%', :keyword, '%')) or lower(o.name) like lower(concat('%', :keyword, '%')))
          and (:active is null or o.active = :active)
        order by o.id desc
    """)
    Page<Organization> search(@Param("keyword") String keyword, @Param("active") Boolean active, Pageable pageable);
}
