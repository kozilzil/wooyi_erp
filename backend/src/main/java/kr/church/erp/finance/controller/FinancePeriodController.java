package kr.church.erp.finance.controller;

import jakarta.validation.Valid;
import kr.church.erp.common.api.ApiResponse;
import kr.church.erp.common.api.PageResponse;
import kr.church.erp.finance.dto.FinancePeriodCreateRequest;
import kr.church.erp.finance.dto.FinancePeriodResponse;
import kr.church.erp.finance.dto.FinancePeriodUpdateRequest;
import kr.church.erp.finance.service.FinancePeriodService;
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
@RequestMapping("/api/finance/periods")
public class FinancePeriodController {

    private final FinancePeriodService financePeriodService;

    public FinancePeriodController(FinancePeriodService financePeriodService) {
        this.financePeriodService = financePeriodService;
    }

    @GetMapping
    public ApiResponse<PageResponse<FinancePeriodResponse>> search(
        @RequestParam(required = false) Integer fiscalYear,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Boolean active,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(PageResponse.from(financePeriodService.search(fiscalYear, status, active, pageable)));
    }

    @PostMapping
    public ApiResponse<FinancePeriodResponse> create(@Valid @RequestBody FinancePeriodCreateRequest request) {
        return ApiResponse.ok(financePeriodService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<FinancePeriodResponse> update(@PathVariable Long id, @Valid @RequestBody FinancePeriodUpdateRequest request) {
        return ApiResponse.ok(financePeriodService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        financePeriodService.delete(id);
        return ApiResponse.ok(null);
    }
}
