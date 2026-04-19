package kr.church.erp.commoncode.dto;

import kr.church.erp.commoncode.domain.entity.CommonCode;

public record CommonCodeResponse(
    Long id,
    String groupCode,
    String code,
    String name,
    int sortOrder,
    boolean active,
    String description
) {
    public static CommonCodeResponse from(CommonCode commonCode) {
        return new CommonCodeResponse(
            commonCode.getId(),
            commonCode.getGroupCode(),
            commonCode.getCode(),
            commonCode.getName(),
            commonCode.getSortOrder(),
            commonCode.isActive(),
            commonCode.getDescription()
        );
    }
}
