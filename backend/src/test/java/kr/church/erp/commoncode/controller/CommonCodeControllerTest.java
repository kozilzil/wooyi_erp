package kr.church.erp.commoncode.controller;

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
import kr.church.erp.commoncode.dto.CommonCodeCreateRequest;
import kr.church.erp.commoncode.dto.CommonCodeResponse;
import kr.church.erp.commoncode.service.CommonCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommonCodeController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class CommonCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommonCodeService commonCodeService;

    @Test
    void createSuccess() throws Exception {
        when(commonCodeService.create(any()))
            .thenReturn(new CommonCodeResponse(1L, "ORG_TYPE", "TEAM", "��", 1, true, null));

        mockMvc.perform(post("/api/common-codes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(
                    new CommonCodeCreateRequest("ORG_TYPE", "TEAM", "��", 1, true, null)
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.groupCode").value("ORG_TYPE"));
    }

    @Test
    void groupQuerySuccess() throws Exception {
        when(commonCodeService.getByGroupCode("ORG_TYPE", true))
            .thenReturn(List.of(new CommonCodeResponse(1L, "ORG_TYPE", "TEAM", "��", 1, true, null)));

        mockMvc.perform(get("/api/common-codes/groups/ORG_TYPE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].code").value("TEAM"));
    }

    @Test
    void searchSuccess() throws Exception {
        when(commonCodeService.search(any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(
                List.of(new CommonCodeResponse(1L, "ORG_TYPE", "TEAM", "��", 1, true, null)),
                PageRequest.of(0, 20),
                1
            ));

        mockMvc.perform(get("/api/common-codes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items[0].groupCode").value("ORG_TYPE"));
    }
}
