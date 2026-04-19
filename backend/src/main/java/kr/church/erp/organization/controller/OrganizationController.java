package kr.church.erp.organization.controller;

import jakarta.validation.Valid;
import kr.church.erp.common.api.ApiResponse;
import kr.church.erp.common.api.PageResponse;
import kr.church.erp.organization.dto.OrganizationCreateRequest;
import kr.church.erp.organization.dto.OrganizationResponse;
import kr.church.erp.organization.dto.OrganizationUpdateRequest;
import kr.church.erp.organization.service.OrganizationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ApiResponse<PageResponse<OrganizationResponse>> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Boolean active,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(PageResponse.from(organizationService.search(keyword, active, pageable)));
    }

    @PostMapping
    public ApiResponse<OrganizationResponse> create(@Valid @RequestBody OrganizationCreateRequest request) {
        return ApiResponse.ok(organizationService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<OrganizationResponse> update(@PathVariable Long id, @Valid @RequestBody OrganizationUpdateRequest request) {
        return ApiResponse.ok(organizationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        organizationService.delete(id);
        return ApiResponse.ok(null);
    }
}
