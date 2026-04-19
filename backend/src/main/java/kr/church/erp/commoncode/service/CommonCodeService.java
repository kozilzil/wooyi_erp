package kr.church.erp.commoncode.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import kr.church.erp.common.audit.service.AuditLogService;
import kr.church.erp.commoncode.domain.entity.CommonCode;
import kr.church.erp.commoncode.domain.repository.CommonCodeRepository;
import kr.church.erp.commoncode.dto.CommonCodeCreateRequest;
import kr.church.erp.commoncode.dto.CommonCodeResponse;
import kr.church.erp.commoncode.dto.CommonCodeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;
    private final AuditLogService auditLogService;

    public CommonCodeService(CommonCodeRepository commonCodeRepository, AuditLogService auditLogService) {
        this.commonCodeRepository = commonCodeRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public CommonCodeResponse create(CommonCodeCreateRequest request) {
        if (commonCodeRepository.existsByGroupCodeAndCodeAndDeletedAtIsNull(request.groupCode(), request.code())) {
            throw new IllegalArgumentException("Common code already exists in group");
        }

        CommonCode commonCode = CommonCode.create(
            request.groupCode(),
            request.code(),
            request.name(),
            request.sortOrder() == null ? 0 : request.sortOrder(),
            request.active() == null || request.active(),
            request.description()
        );

        CommonCode saved = commonCodeRepository.save(commonCode);

        auditLogService.log(
            "common-code",
            "common_code",
            saved.getId(),
            "CREATE",
            null,
            null,
            snapshot(saved)
        );

        return CommonCodeResponse.from(saved);
    }

    public Page<CommonCodeResponse> search(String groupCode, String keyword, Boolean active, Pageable pageable) {
        return commonCodeRepository.search(groupCode, keyword, active, pageable)
            .map(CommonCodeResponse::from);
    }

    public List<CommonCodeResponse> getByGroupCode(String groupCode, boolean activeOnly) {
        List<CommonCode> list = activeOnly
            ? commonCodeRepository.findByGroupCodeAndActiveAndDeletedAtIsNullOrderBySortOrderAscIdAsc(groupCode, true)
            : commonCodeRepository.findByGroupCodeAndDeletedAtIsNullOrderBySortOrderAscIdAsc(groupCode);

        return list.stream().map(CommonCodeResponse::from).toList();
    }

    @Transactional
    public CommonCodeResponse update(Long id, CommonCodeUpdateRequest request) {
        CommonCode commonCode = commonCodeRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Common code not found"));

        Map<String, Object> before = snapshot(commonCode);
        commonCode.update(
            request.name(),
            request.sortOrder() == null ? commonCode.getSortOrder() : request.sortOrder(),
            request.active() == null || request.active(),
            request.description()
        );

        auditLogService.log(
            "common-code",
            "common_code",
            commonCode.getId(),
            "UPDATE",
            null,
            before,
            snapshot(commonCode)
        );

        return CommonCodeResponse.from(commonCode);
    }

    @Transactional
    public void delete(Long id) {
        CommonCode commonCode = commonCodeRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Common code not found"));

        Map<String, Object> before = snapshot(commonCode);
        commonCode.softDelete();

        auditLogService.log(
            "common-code",
            "common_code",
            commonCode.getId(),
            "DELETE",
            null,
            before,
            snapshot(commonCode)
        );
    }

    private Map<String, Object> snapshot(CommonCode commonCode) {
        return Map.of(
            "id", commonCode.getId() == null ? -1 : commonCode.getId(),
            "groupCode", commonCode.getGroupCode(),
            "code", commonCode.getCode(),
            "name", commonCode.getName(),
            "sortOrder", commonCode.getSortOrder(),
            "active", commonCode.isActive(),
            "description", commonCode.getDescription() == null ? "" : commonCode.getDescription()
        );
    }
}
