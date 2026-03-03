package com.rpsB.demo.service;

import com.rpsB.demo.dto.LoginDto;
import com.rpsB.demo.dto.TokenDto;
import com.rpsB.demo.dto.UserCreateDto;
import com.rpsB.demo.entity.RefreshToken;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.enums.Role;
import com.rpsB.demo.exception.AppException;
import com.rpsB.demo.security.UserPrincipal;
import com.rpsB.demo.security.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Test")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserService userService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "enforceLimit", 3);
    }

    @Test
    void login() {
        //GIVEN
        LoginDto loginDto = new LoginDto("test@gmail.com", "1234", "");
        User user = User.builder()
                .id(1L)
                .email(loginDto.email())
                .password("encoded")
                .role(Role.USER)
                .build();
        UserPrincipal principal = new UserPrincipal(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtProvider.createAccessToken(authentication)).thenReturn("access-token");
        when(jwtProvider.createRefreshToken(authentication)).thenReturn("refresh-token");
        when(jwtProvider.getJtiAllowExpired("refresh-token")).thenReturn("jti-123");
        when(jwtProvider.getExpiration("refresh-token"))
                .thenReturn(Date.from(Instant.now().plusSeconds(3600)));
        //WHEN
        TokenDto result = authService.login(loginDto);
        //THEN
        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());

        verify(refreshTokenService).enforceLimit(eq(1L), anyInt());
        verify(refreshTokenService).save(eq("jti-123"), any(), eq(1L));
    }

    @Test
    void create() {
        //GIVEN
        UserCreateDto dto = new UserCreateDto(
                "John",
                "john@gmail.com",
                "1234"
        );
        when(passwordEncoder.encode("1234")).thenReturn("encoded");
        //WHEN
        authService.create(dto);
        //THEN
        verify(userService).save(argThat(user ->
                user.getName().equals("John") &&
                        user.getEmail().equals("john@gmail.com") &&
                        user.getPassword().equals("encoded") &&
                        user.getRole() == Role.USER
        ));
    }

    @Test
    void updateRefreshToken_exception() {
        //GIVEN
        TokenDto dto = TokenDto.builder()
                .refreshToken("bad-token")
                .build();
        when(jwtProvider.validateToken("bad-token")).thenReturn(false);
        //THEN
        assertThrows(AppException.class, () -> authService.updateRefreshToken(dto));
    }

    @Test
    void updateRefreshToken_shouldReturnNewTokens() {
        //GIVEN
        TokenDto dto = TokenDto.builder()
                .refreshToken("old-refresh")
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .role(Role.USER)
                .build();

        RefreshToken storedToken = new RefreshToken();
        storedToken.setUser(user);

        when(jwtProvider.validateToken("old-refresh")).thenReturn(true);
        when(jwtProvider.getJtiAllowExpired("old-refresh")).thenReturn("old-jti");
        when(refreshTokenService.validateAndGet("old-jti")).thenReturn(storedToken);
        when(jwtProvider.createAccessToken(any())).thenReturn("new-access");
        when(jwtProvider.createRefreshToken(any())).thenReturn("new-refresh");
        when(jwtProvider.getJtiAllowExpired("new-refresh")).thenReturn("new-jti");
        when(jwtProvider.getExpiration("new-refresh"))
                .thenReturn(Date.from(Instant.now().plusSeconds(3600)));

        //WHEN
        TokenDto result = authService.updateRefreshToken(dto);

        //THEN
        assertEquals("new-access", result.accessToken());
        assertEquals("new-refresh", result.refreshToken());

        verify(refreshTokenService).revoke(storedToken);
        verify(refreshTokenService).save(eq("new-jti"), any(), eq(1L));
    }
}