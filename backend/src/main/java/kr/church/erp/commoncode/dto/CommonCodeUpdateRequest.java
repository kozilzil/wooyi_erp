package kr.church.erp.commoncode.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommonCodeUpdateRequest(
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name length must be <= 100")
    String name,

    @Min(value = 0, message = "sortOrder must be >= 0")
    @Max(value = 9999, message = "sortOrder must be <= 9999")
    Integer sortOrder,

    Boolean active,

    @Size(max = 500, message = "description length must be <= 500")
    String description
) {}
