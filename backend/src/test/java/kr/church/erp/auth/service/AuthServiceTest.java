package kr.church.erp.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import kr.church.erp.auth.domain.entity.AuthUser;
import kr.church.erp.auth.domain.entity.Permission;
import kr.church.erp.auth.domain.entity.Role;
import kr.church.erp.auth.domain.entity.UserStatus;
import kr.church.erp.auth.domain.repository.AuthUserRepository;
import kr.church.erp.auth.dto.LoginRequest;
import kr.church.erp.auth.exception.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUser user;

    @Mock
    private Role role;

    @Mock
    private Permission permission;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authUserRepository, passwordEncoder);
    }

    @Test
    void loginSuccessThenMeSuccess() {
        when(authUserRepository.findByLoginIdAndDeletedAtIsNull("admin")).thenReturn(Optional.of(user));
        when(user.getStatus()).thenReturn(UserStatus.ACTIVE);
        when(user.getPasswordHash()).thenReturn("encoded");
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(user.getId()).thenReturn(1L);
        when(user.getLoginId()).thenReturn("admin");
        when(user.getName()).thenReturn("������");
        when(user.getRoles()).thenReturn(Set.of(role));
        when(role.getCode()).thenReturn("SYS_ADMIN");
        when(role.getPermissions()).thenReturn(Set.of(permission));
        when(permission.getCode()).thenReturn("AUTH.ME");

        var loginResponse = authService.login(new LoginRequest("admin", "password"));

        assertThat(loginResponse.accessToken()).isNotBlank();
        assertThat(loginResponse.user().name()).isEqualTo("������");

        var me = authService.me("Bearer " + loginResponse.accessToken());
        assertThat(me.loginId()).isEqualTo("admin");
        assertThat(me.roles()).contains("SYS_ADMIN");
        assertThat(me.permissions()).contains("AUTH.ME");
    }

    @Test
    void loginFailWhenPasswordMismatch() {
        when(authUserRepository.findByLoginIdAndDeletedAtIsNull("admin")).thenReturn(Optional.of(user));
        when(user.getStatus()).thenReturn(UserStatus.ACTIVE);
        when(user.getPasswordHash()).thenReturn("encoded");
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin", "wrong")))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("Invalid loginId or password");
    }

    @Test
    void loginFailWhenUserInactive() {
        when(authUserRepository.findByLoginIdAndDeletedAtIsNull("admin")).thenReturn(Optional.of(user));
        when(user.getStatus()).thenReturn(UserStatus.INACTIVE);

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin", "password")))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("User is inactive");
    }

    @Test
    void meFailWhenTokenMissing() {
        assertThatThrownBy(() -> authService.me(null))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("Authorization header is required");
    }
}
