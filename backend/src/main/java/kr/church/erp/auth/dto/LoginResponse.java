package kr.church.erp.auth.dto;

public record LoginResponse(
    String accessToken,
    AuthUserSummary user
) {}
