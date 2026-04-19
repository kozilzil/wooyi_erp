package kr.church.erp.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationUpdateRequest(
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name length must be <= 100")
    String name,

    Long parentId,

    @Size(max = 50, message = "type length must be <= 50")
    String type,

    Boolean active
) {}
