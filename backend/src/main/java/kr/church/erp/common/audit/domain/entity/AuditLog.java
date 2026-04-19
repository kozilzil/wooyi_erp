package kr.church.erp.common.audit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name", nullable = false, length = 50)
    private String moduleName;

    @Column(name = "entity_name", nullable = false, length = 50)
    private String entityName;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "payload_before")
    private String payloadBefore;

    @Column(name = "payload_after")
    private String payloadAfter;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AuditLog() {
    }

    public static AuditLog of(
        String moduleName,
        String entityName,
        Long entityId,
        String action,
        Long actorId,
        String payloadBefore,
        String payloadAfter
    ) {
        AuditLog log = new AuditLog();
        log.moduleName = moduleName;
        log.entityName = entityName;
        log.entityId = entityId;
        log.action = action;
        log.actorId = actorId;
        log.payloadBefore = payloadBefore;
        log.payloadAfter = payloadAfter;
        log.createdAt = LocalDateTime.now();
        return log;
    }
}
