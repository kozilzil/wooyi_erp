package kr.church.erp.auth.domain.repository;

import kr.church.erp.auth.domain.entity.AuthUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<AuthUser> findByLoginIdAndDeletedAtIsNull(String loginId);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<AuthUser> findByIdAndDeletedAtIsNull(Long id);
}
