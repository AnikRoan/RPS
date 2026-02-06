package com.rpsB.demo.service;

import com.rpsB.demo.dto.LoginDto;
import com.rpsB.demo.dto.TokenDto;
import com.rpsB.demo.dto.UserCreateDto;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.enums.Role;
import com.rpsB.demo.security.UserPrincipal;
import com.rpsB.demo.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
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

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public TokenDto login(LoginDto loginDto) {
        String email = userService.resolvEmail(loginDto.email());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, loginDto.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenDto.TokenDtoBuilder tokenDto = TokenDto.builder();
        tokenDto.accessToken(jwtProvider.createAccessToken(authentication));
        tokenDto.refreshToken(jwtProvider.createRefreshToken(authentication));
        return tokenDto.build();

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

    public TokenDto updateRefreshToken(TokenDto tokenDto) {
        String oldAccessToken = tokenDto.accessToken();
        String oldRefreshToken = tokenDto.refreshToken();

        if (oldAccessToken == null) {
            throw new ApiException("Access token is empty");
        }
        if (!jwtProvider.validateToken(oldRefreshToken)) {
            throw new ApiException("Not validated refresh token");
        }
        Authentication authentication = jwtProvider.getAuthenticationExpiredToken(oldAccessToken);

        return TokenDto.builder()
                .accessToken(jwtProvider.createAccessToken(authentication))
                .refreshToken(jwtProvider.createRefreshToken(authentication)).build();
    }

    public UserPrincipal getAuthenticatedUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException("User must be authenticated");
        }

        return (UserPrincipal) authentication.getPrincipal();

    }

}
