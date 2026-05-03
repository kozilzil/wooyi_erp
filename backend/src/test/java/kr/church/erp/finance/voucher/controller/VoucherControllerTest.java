package kr.church.erp.finance.voucher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import kr.church.erp.common.config.SecurityConfig;
import kr.church.erp.common.exception.GlobalExceptionHandler;
import kr.church.erp.finance.voucher.dto.VoucherCreateRequest;
import kr.church.erp.finance.voucher.dto.VoucherLineRequest;
import kr.church.erp.finance.voucher.dto.VoucherResponse;
import kr.church.erp.finance.voucher.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = VoucherController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class VoucherControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VoucherService voucherService;

    @Test
    void createSuccess() throws Exception {
        when(voucherService.create(any())).thenReturn(new VoucherResponse(
            1L, "SV-1", "INCOME", "SINGLE", 1L, LocalDate.parse("2026-04-01"),
            "DRAFT", "offering", 1000L, List.of()
        ));

        mockMvc.perform(post("/api/finance/vouchers")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new VoucherCreateRequest(
                    "SINGLE",
                    "INCOME",
                    1L,
                    LocalDate.parse("2026-04-01"),
                    "offering",
                    List.of(new VoucherLineRequest(null, 10L, 1000L, "sunday offering"))
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.bookkeepingMode").value("SINGLE"));
    }

    @Test
    void searchSuccess() throws Exception {
        when(voucherService.search(any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(
                List.of(new VoucherResponse(
                    1L, "DV-1", "GENERAL", "DOUBLE", 1L, LocalDate.parse("2026-04-01"),
                    "DRAFT", "double voucher", 1000L, List.of()
                )),
                PageRequest.of(0, 20),
                1
            ));

        mockMvc.perform(get("/api/finance/vouchers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items[0].bookkeepingMode").value("DOUBLE"));
    }
}
