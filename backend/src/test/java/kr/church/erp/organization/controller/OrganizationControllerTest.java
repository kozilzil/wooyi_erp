package kr.church.erp.organization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.church.erp.common.config.SecurityConfig;
import kr.church.erp.common.exception.GlobalExceptionHandler;
import kr.church.erp.organization.dto.OrganizationCreateRequest;
import kr.church.erp.organization.dto.OrganizationResponse;
import kr.church.erp.organization.service.OrganizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrganizationController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrganizationService organizationService;

    @Test
    void createSuccess() throws Exception {
        when(organizationService.create(any())).thenReturn(new OrganizationResponse(1L, "ORG001", "����", null, "DEPARTMENT", true));

        mockMvc.perform(post("/api/organizations")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new OrganizationCreateRequest("ORG001", "����", null, "DEPARTMENT", true))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.code").value("ORG001"));
    }

    @Test
    void searchSuccess() throws Exception {
        when(organizationService.search(any(), any(), any()))
            .thenReturn(new PageImpl<>(
                java.util.List.of(new OrganizationResponse(1L, "ORG001", "����", null, "DEPARTMENT", true)),
                PageRequest.of(0, 20),
                1
            ));

        mockMvc.perform(get("/api/organizations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items[0].code").value("ORG001"));
    }
}
