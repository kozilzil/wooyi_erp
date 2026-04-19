package kr.church.erp.organization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.organization.domain.entity.Organization;
import kr.church.erp.organization.domain.repository.OrganizationRepository;
import kr.church.erp.organization.dto.OrganizationCreateRequest;
import kr.church.erp.organization.dto.OrganizationUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AuditLogService auditLogService;

    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        organizationService = new OrganizationService(organizationRepository, auditLogService);
    }

    @Test
    void createSuccess() {
        Organization organization = Organization.create("ORG001", "����", null, "DEPARTMENT", true);
        when(organizationRepository.existsByCodeAndDeletedAtIsNull("ORG001")).thenReturn(false);
        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);

        var result = organizationService.create(new OrganizationCreateRequest("ORG001", "����", null, "DEPARTMENT", true));

        assertThat(result.code()).isEqualTo("ORG001");
        verify(auditLogService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void createFailWhenCodeDuplicated() {
        when(organizationRepository.existsByCodeAndDeletedAtIsNull("ORG001")).thenReturn(true);

        assertThatThrownBy(() -> organizationService.create(
            new OrganizationCreateRequest("ORG001", "����", null, "DEPARTMENT", true)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateFailWhenNotFound() {
        when(organizationRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> organizationService.update(1L, new OrganizationUpdateRequest("����", null, "TEAM", true)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
