package com.rpsB.demo.service;

import com.rpsB.demo.dto.LoginDto;
import com.rpsB.demo.dto.TokenDto;
import com.rpsB.demo.dto.UserCreateDto;
import com.rpsB.demo.entity.RefreshToken;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.enums.Role;
import com.rpsB.demo.security.UserPrincipal;
import com.rpsB.demo.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${token.enforce.limit}")
    private int enforceLimit;

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        String email = userService.resolvEmail(loginDto.email());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, loginDto.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtProvider.createAccessToken(authentication);
        String refreshToken = jwtProvider.createRefreshToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        refreshTokenService.enforceLimit(principal.getId(), enforceLimit);

        refreshTokenService.save(jwtProvider.getJtiAllowExpired(refreshToken),
                jwtProvider.getExpiration(refreshToken),
                principal.getId());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    public void create(UserCreateDto createDto) {
        //TODO: maby return TokenDto
        User user = User.builder()
                .name(createDto.name())
                .email(createDto.email())
                .password(passwordEncoder.encode(createDto.password()))
                .role(Role.USER)
                .build();
        userService.save(user);

    }

    @Transactional
    public TokenDto updateRefreshToken(TokenDto tokenDto) {

        if (!jwtProvider.validateToken(tokenDto.refreshToken())) {
            throw new ApiException("Not validated refresh token");
        }
        String jti = jwtProvider.getJtiAllowExpired(tokenDto.refreshToken());

        RefreshToken storedToken = refreshTokenService.validateAndGet(jti);

        UserPrincipal principal = new UserPrincipal(storedToken.getUser());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        // rotation (очень важно)
        refreshTokenService.revoke(storedToken);

        String newAccessToken = jwtProvider.createAccessToken(authentication);
        String newRefreshToken = jwtProvider.createRefreshToken(authentication);

        refreshTokenService.enforceLimit(principal.getId(), enforceLimit);

        refreshTokenService.save(
                jwtProvider.getJtiAllowExpired(newRefreshToken),
                jwtProvider.getExpiration(newRefreshToken),
                principal.getId()
        );

        return TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

//    public UserPrincipal getAuthenticatedUserPrincipal() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ApiException("User must be authenticated");
//        }
//
//        return (UserPrincipal) authentication.getPrincipal();
//    }
}
