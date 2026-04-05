package kr.church.erp.common.health.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    public Map<String, Object> currentStatus() {
        return Map.of(
            "status", "UP",
            "service", "church-erp-backend",
            "timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString()
        );
    }
}
