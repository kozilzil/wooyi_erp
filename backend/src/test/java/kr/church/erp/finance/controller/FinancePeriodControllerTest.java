package kr.church.erp.finance.controller;

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
import kr.church.erp.finance.dto.FinancePeriodCreateRequest;
import kr.church.erp.finance.dto.FinancePeriodResponse;
import kr.church.erp.finance.service.FinancePeriodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FinancePeriodController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class FinancePeriodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FinancePeriodService financePeriodService;

    @Test
    void createSuccess() throws Exception {
        when(financePeriodService.create(any()))
            .thenReturn(new FinancePeriodResponse(1L, 2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true));

        mockMvc.perform(post("/api/finance/periods")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(
                    new FinancePeriodCreateRequest(2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true)
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.fiscalYear").value(2026));
    }

    @Test
    void searchSuccess() throws Exception {
        when(financePeriodService.search(any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(
                List.of(new FinancePeriodResponse(1L, 2026, 1, LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"), "OPEN", true)),
                PageRequest.of(0, 20),
                1
            ));

        mockMvc.perform(get("/api/finance/periods"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items[0].fiscalYear").value(2026));
    }
}
