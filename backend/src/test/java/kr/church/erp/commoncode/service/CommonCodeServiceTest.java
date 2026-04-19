package kr.church.erp.commoncode.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.commoncode.domain.entity.CommonCode;
import kr.church.erp.commoncode.domain.repository.CommonCodeRepository;
import kr.church.erp.commoncode.dto.CommonCodeCreateRequest;
import kr.church.erp.commoncode.dto.CommonCodeUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonCodeServiceTest {

    @Mock
    private CommonCodeRepository commonCodeRepository;

    @Mock
    private AuditLogService auditLogService;

    private CommonCodeService commonCodeService;

    @BeforeEach
    void setUp() {
        commonCodeService = new CommonCodeService(commonCodeRepository, auditLogService);
    }

    @Test
    void createSuccess() {
        CommonCode commonCode = CommonCode.create("ORG_TYPE", "TEAM", "��", 1, true, "desc");
        when(commonCodeRepository.existsByGroupCodeAndCodeAndDeletedAtIsNull("ORG_TYPE", "TEAM")).thenReturn(false);
        when(commonCodeRepository.save(any(CommonCode.class))).thenReturn(commonCode);

        var result = commonCodeService.create(new CommonCodeCreateRequest("ORG_TYPE", "TEAM", "��", 1, true, "desc"));

        assertThat(result.groupCode()).isEqualTo("ORG_TYPE");
        verify(auditLogService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void createFailWhenDuplicated() {
        when(commonCodeRepository.existsByGroupCodeAndCodeAndDeletedAtIsNull("ORG_TYPE", "TEAM")).thenReturn(true);

        assertThatThrownBy(() -> commonCodeService.create(
            new CommonCodeCreateRequest("ORG_TYPE", "TEAM", "��", 1, true, null)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void groupQueryShouldReturnActiveOnlyWhenTrue() {
        when(commonCodeRepository.findByGroupCodeAndActiveAndDeletedAtIsNullOrderBySortOrderAscIdAsc("ORG_TYPE", true))
            .thenReturn(List.of(CommonCode.create("ORG_TYPE", "A", "Ȱ��", 1, true, null)));

        var result = commonCodeService.getByGroupCode("ORG_TYPE", true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).active()).isTrue();
    }

    @Test
    void updateFailWhenNotFound() {
        when(commonCodeRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commonCodeService.update(1L, new CommonCodeUpdateRequest("�̸�", 1, true, null)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
