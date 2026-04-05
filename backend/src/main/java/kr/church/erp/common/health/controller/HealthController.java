package kr.church.erp.common.health.controller;

import kr.church.erp.common.api.ApiResponse;
import kr.church.erp.common.health.service.HealthCheckService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthCheckService healthCheckService;

    public HealthController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(healthCheckService.currentStatus());
    }
}
