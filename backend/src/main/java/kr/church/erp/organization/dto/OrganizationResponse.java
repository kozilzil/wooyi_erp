package kr.church.erp.organization.dto;

import kr.church.erp.organization.domain.entity.Organization;

public record OrganizationResponse(
    Long id,
    String code,
    String name,
    Long parentId,
    String type,
    boolean active
) {
    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
            organization.getId(),
            organization.getCode(),
            organization.getName(),
            organization.getParentId(),
            organization.getType(),
            organization.isActive()
        );
    }
}
