package kr.church.erp.organization.service;

import jakarta.transaction.Transactional;
import java.util.Map;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.organization.domain.entity.Organization;
import kr.church.erp.organization.domain.repository.OrganizationRepository;
import kr.church.erp.organization.dto.OrganizationCreateRequest;
import kr.church.erp.organization.dto.OrganizationResponse;
import kr.church.erp.organization.dto.OrganizationUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    public OrganizationService(OrganizationRepository organizationRepository, AuditLogService auditLogService) {
        this.organizationRepository = organizationRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public OrganizationResponse create(OrganizationCreateRequest request) {
        if (organizationRepository.existsByCodeAndDeletedAtIsNull(request.code())) {
            throw new IllegalArgumentException("Organization code already exists");
        }

        Organization organization = Organization.create(
            request.code(),
            request.name(),
            request.parentId(),
            request.type(),
            request.active() == null || request.active()
        );
        Organization saved = organizationRepository.save(organization);

        auditLogService.log(
            "organization",
            "organization",
            saved.getId(),
            "CREATE",
            null,
            null,
            snapshot(saved)
        );

        return OrganizationResponse.from(saved);
    }

    public Page<OrganizationResponse> search(String keyword, Boolean active, Pageable pageable) {
        return organizationRepository.search(keyword, active, pageable)
            .map(OrganizationResponse::from);
    }

    @Transactional
    public OrganizationResponse update(Long id, OrganizationUpdateRequest request) {
        Organization organization = organizationRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        Map<String, Object> before = snapshot(organization);
        organization.update(
            request.name(),
            request.parentId(),
            request.type(),
            request.active() == null || request.active()
        );

        auditLogService.log(
            "organization",
            "organization",
            organization.getId(),
            "UPDATE",
            null,
            before,
            snapshot(organization)
        );

        return OrganizationResponse.from(organization);
    }

    @Transactional
    public void delete(Long id) {
        Organization organization = organizationRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        Map<String, Object> before = snapshot(organization);
        organization.softDelete();

        auditLogService.log(
            "organization",
            "organization",
            organization.getId(),
            "DELETE",
            null,
            before,
            snapshot(organization)
        );
    }

    private Map<String, Object> snapshot(Organization organization) {
        return Map.of(
            "id", organization.getId() == null ? -1 : organization.getId(),
            "code", organization.getCode(),
            "name", organization.getName(),
            "parentId", organization.getParentId() == null ? -1 : organization.getParentId(),
            "type", organization.getType() == null ? "" : organization.getType(),
            "active", organization.isActive()
        );
    }
}
