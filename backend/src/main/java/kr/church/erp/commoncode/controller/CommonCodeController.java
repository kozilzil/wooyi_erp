package kr.church.erp.commoncode.controller;

import jakarta.validation.Valid;
import kr.church.erp.common.api.ApiResponse;
import kr.church.erp.common.api.PageResponse;
import kr.church.erp.commoncode.dto.CommonCodeCreateRequest;
import kr.church.erp.commoncode.dto.CommonCodeResponse;
import kr.church.erp.commoncode.dto.CommonCodeUpdateRequest;
import kr.church.erp.commoncode.service.CommonCodeService;
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

import java.util.List;

@RestController
@RequestMapping("/api/common-codes")
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    public CommonCodeController(CommonCodeService commonCodeService) {
        this.commonCodeService = commonCodeService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CommonCodeResponse>> search(
        @RequestParam(required = false) String groupCode,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Boolean active,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(PageResponse.from(commonCodeService.search(groupCode, keyword, active, pageable)));
    }

    @GetMapping("/groups/{groupCode}")
    public ApiResponse<List<CommonCodeResponse>> findByGroupCode(
        @PathVariable String groupCode,
        @RequestParam(defaultValue = "true") boolean activeOnly
    ) {
        return ApiResponse.ok(commonCodeService.getByGroupCode(groupCode, activeOnly));
    }

    @PostMapping
    public ApiResponse<CommonCodeResponse> create(@Valid @RequestBody CommonCodeCreateRequest request) {
        return ApiResponse.ok(commonCodeService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CommonCodeResponse> update(@PathVariable Long id, @Valid @RequestBody CommonCodeUpdateRequest request) {
        return ApiResponse.ok(commonCodeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        commonCodeService.delete(id);
        return ApiResponse.ok(null);
    }
}
