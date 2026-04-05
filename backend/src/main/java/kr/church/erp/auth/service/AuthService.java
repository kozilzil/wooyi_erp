package kr.church.erp.auth.service;

import kr.church.erp.auth.domain.entity.AuthUser;
import kr.church.erp.auth.domain.entity.Permission;
import kr.church.erp.auth.domain.entity.Role;
import kr.church.erp.auth.domain.entity.UserStatus;
import kr.church.erp.auth.domain.repository.AuthUserRepository;
import kr.church.erp.auth.dto.AuthMeResponse;
import kr.church.erp.auth.dto.AuthUserSummary;
import kr.church.erp.auth.dto.LoginRequest;
import kr.church.erp.auth.dto.LoginResponse;
import kr.church.erp.auth.exception.AuthException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, SessionPrincipal> tokenStore = new ConcurrentHashMap<>();

    public AuthService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        AuthUser user = authUserRepository.findByLoginIdAndDeletedAtIsNull(request.loginId())
            .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "AUTH_FAILED", "Invalid loginId or password"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AuthException(HttpStatus.FORBIDDEN, "USER_INACTIVE", "User is inactive");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "AUTH_FAILED", "Invalid loginId or password");
        }

        SessionPrincipal principal = SessionPrincipal.from(user);
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, principal);

        return new LoginResponse(token, new AuthUserSummary(user.getId(), user.getName()));
    }

    public AuthMeResponse me(String bearerToken) {
        String token = extractBearerToken(bearerToken);
        SessionPrincipal principal = tokenStore.get(token);

        if (principal == null) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid or expired access token");
        }

        return new AuthMeResponse(
            principal.id(),
            principal.loginId(),
            principal.name(),
            principal.roles(),
            principal.permissions()
        );
    }

    public void logout(String bearerToken) {
        String token = extractBearerToken(bearerToken);
        SessionPrincipal removed = tokenStore.remove(token);
        if (removed == null) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid or expired access token");
        }
    }

    private String extractBearerToken(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authorization header is required");
        }

        String token = bearerToken.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authorization header is required");
        }

        return token;
    }

    private record SessionPrincipal(
        Long id,
        String loginId,
        String name,
        List<String> roles,
        List<String> permissions,
        Instant issuedAt
    ) {
        static SessionPrincipal from(AuthUser user) {
            List<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .toList();

            List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

            return new SessionPrincipal(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                roles,
                permissions,
                Instant.now()
            );
        }
    }
}
