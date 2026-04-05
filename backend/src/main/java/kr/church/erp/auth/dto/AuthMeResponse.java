package kr.church.erp.auth.dto;

import java.util.List;

public record AuthMeResponse(
    Long id,
    String loginId,
    String name,
    List<String> roles,
    List<String> permissions
) {}
