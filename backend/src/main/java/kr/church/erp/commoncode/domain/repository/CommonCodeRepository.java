package kr.church.erp.commoncode.domain.repository;

import java.util.List;
import java.util.Optional;
import kr.church.erp.commoncode.domain.entity.CommonCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {

    Optional<CommonCode> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByGroupCodeAndCodeAndDeletedAtIsNull(String groupCode, String code);

    @Query("""
        select c from CommonCode c
        where c.deletedAt is null
          and (:groupCode is null or c.groupCode = :groupCode)
          and (:keyword is null or lower(c.code) like lower(concat('%', :keyword, '%')) or lower(c.name) like lower(concat('%', :keyword, '%')))
          and (:active is null or c.active = :active)
        order by c.groupCode asc, c.sortOrder asc, c.id desc
    """)
    Page<CommonCode> search(
        @Param("groupCode") String groupCode,
        @Param("keyword") String keyword,
        @Param("active") Boolean active,
        Pageable pageable
    );

    List<CommonCode> findByGroupCodeAndDeletedAtIsNullOrderBySortOrderAscIdAsc(String groupCode);

    List<CommonCode> findByGroupCodeAndActiveAndDeletedAtIsNullOrderBySortOrderAscIdAsc(String groupCode, boolean active);
}
