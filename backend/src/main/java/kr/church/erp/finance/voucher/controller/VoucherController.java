package kr.church.erp.finance.voucher.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import kr.church.erp.common.api.ApiResponse;
import kr.church.erp.common.api.PageResponse;
import kr.church.erp.finance.voucher.dto.VoucherCreateRequest;
import kr.church.erp.finance.voucher.dto.VoucherResponse;
import kr.church.erp.finance.voucher.dto.VoucherUpdateRequest;
import kr.church.erp.finance.voucher.service.VoucherService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/finance/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ApiResponse<PageResponse<VoucherResponse>> search(
        @RequestParam(required = false) Long periodId,
        @RequestParam(required = false) String voucherType,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(PageResponse.from(voucherService.search(periodId, voucherType, status, fromDate, toDate, pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<VoucherResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(voucherService.get(id));
    }

    @PostMapping
    public ApiResponse<VoucherResponse> create(@Valid @RequestBody VoucherCreateRequest request) {
        return ApiResponse.ok(voucherService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<VoucherResponse> update(@PathVariable Long id, @Valid @RequestBody VoucherUpdateRequest request) {
        return ApiResponse.ok(voucherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        voucherService.delete(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{id}/request-approval")
    public ApiResponse<VoucherResponse> requestApproval(@PathVariable Long id) {
        return ApiResponse.ok(voucherService.requestApproval(id));
    }
}
