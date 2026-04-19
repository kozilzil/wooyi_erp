package kr.church.erp.common.audit.domain.repository;

import kr.church.erp.common.audit.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
