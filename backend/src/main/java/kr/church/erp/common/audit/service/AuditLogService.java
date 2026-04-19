package kr.church.erp.common.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.church.erp.common.audit.domain.entity.AuditLog;
import kr.church.erp.common.audit.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditLogService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public void log(
        String moduleName,
        String entityName,
        Long entityId,
        String action,
        Long actorId,
        Object before,
        Object after
    ) {
        AuditLog log = AuditLog.of(
            moduleName,
            entityName,
            entityId,
            action,
            actorId,
            toJson(before),
            toJson(after)
        );
        auditLogRepository.save(log);
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"json_serialization_failed\"}";
        }
    }
}
