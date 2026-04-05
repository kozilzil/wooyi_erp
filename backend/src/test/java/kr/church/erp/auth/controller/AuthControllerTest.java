package kr.church.erp.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.church.erp.auth.dto.AuthMeResponse;
import kr.church.erp.auth.dto.AuthUserSummary;
import kr.church.erp.auth.dto.LoginRequest;
import kr.church.erp.auth.dto.LoginResponse;
import kr.church.erp.auth.exception.AuthException;
import kr.church.erp.auth.service.AuthService;
import kr.church.erp.common.config.SecurityConfig;
import kr.church.erp.common.exception.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void loginSuccess() throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenReturn(new LoginResponse("token-1", new AuthUserSummary(1L, "������")));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin", "password"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("token-1"))
            .andExpect(jsonPath("$.data.user.id").value(1));
    }

    @Test
    void loginFailWhenInvalidCredential() throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new AuthException(HttpStatus.UNAUTHORIZED, "AUTH_FAILED", "Invalid loginId or password"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin", "wrong"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("AUTH_FAILED"));
    }

    @Test
    void meSuccess() throws Exception {
        when(authService.me("Bearer token-1")).thenReturn(
            new AuthMeResponse(1L, "admin", "������", List.of("SYS_ADMIN"), List.of("AUTH.ME"))
        );

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer token-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.loginId").value("admin"));
    }
}
