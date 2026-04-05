package kr.church.erp.common.health.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckServiceTest {

    private final HealthCheckService healthCheckService = new HealthCheckService();

    @Test
    void currentStatusShouldReturnUpState() {
        var result = healthCheckService.currentStatus();

        assertThat(result.get("status")).isEqualTo("UP");
        assertThat(result.get("service")).isEqualTo("church-erp-backend");
        assertThat(result).containsKey("timestamp");
    }
}
