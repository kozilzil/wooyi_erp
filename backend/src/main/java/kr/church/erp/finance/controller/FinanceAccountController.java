package kr.church.erp.finance.controller;

import jakarta.validation.Valid;
import kr.church.erp.common.api.ApiResponse;
import kr.church.erp.common.api.PageResponse;
import kr.church.erp.finance.dto.FinanceAccountCreateRequest;
import kr.church.erp.finance.dto.FinanceAccountResponse;
import kr.church.erp.finance.dto.FinanceAccountUpdateRequest;
import kr.church.erp.finance.service.FinanceAccountService;
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
@RequestMapping("/api/finance/accounts")
public class FinanceAccountController {

    private final FinanceAccountService financeAccountService;

    public FinanceAccountController(FinanceAccountService financeAccountService) {
        this.financeAccountService = financeAccountService;
    }

    @GetMapping
    public ApiResponse<PageResponse<FinanceAccountResponse>> search(
        @RequestParam(required = false) String accountType,
        @RequestParam(required = false) Boolean active,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(PageResponse.from(financeAccountService.search(accountType, active, keyword, pageable)));
    }

    @PostMapping
    public ApiResponse<FinanceAccountResponse> create(@Valid @RequestBody FinanceAccountCreateRequest request) {
        return ApiResponse.ok(financeAccountService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<FinanceAccountResponse> update(@PathVariable Long id, @Valid @RequestBody FinanceAccountUpdateRequest request) {
        return ApiResponse.ok(financeAccountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        financeAccountService.delete(id);
        return ApiResponse.ok(null);
    }
}
