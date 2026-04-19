package kr.church.erp.finance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import kr.church.erp.common.config.SecurityConfig;
import kr.church.erp.common.exception.GlobalExceptionHandler;
import kr.church.erp.finance.dto.FinanceAccountCreateRequest;
import kr.church.erp.finance.dto.FinanceAccountResponse;
import kr.church.erp.finance.service.FinanceAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FinanceAccountController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class FinanceAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FinanceAccountService financeAccountService;

    @Test
    void createSuccess() throws Exception {
        when(financeAccountService.create(any()))
            .thenReturn(new FinanceAccountResponse(1L, "1100", "Cash", "ASSET", null, true));

        mockMvc.perform(post("/api/finance/accounts")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new FinanceAccountCreateRequest("1100", "Cash", "ASSET", null, true))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accountCode").value("1100"));
    }

    @Test
    void searchSuccess() throws Exception {
        when(financeAccountService.search(any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(
                List.of(new FinanceAccountResponse(1L, "1100", "Cash", "ASSET", null, true)),
                PageRequest.of(0, 20),
                1
            ));

        mockMvc.perform(get("/api/finance/accounts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items[0].accountCode").value("1100"));
    }
}
