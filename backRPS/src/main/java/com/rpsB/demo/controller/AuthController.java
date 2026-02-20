package com.rpsB.demo.controller;

import com.rpsB.demo.dto.LoginDto;
import com.rpsB.demo.dto.TokenDto;
import com.rpsB.demo.dto.UserCreateDto;
import com.rpsB.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    // private final OauthService oauthService;

    @PostMapping("/login")
    public TokenDto login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/signup")
    public void signup(@RequestBody UserCreateDto createDto) {
        authService.create(createDto);
    }

    @PostMapping("/token")
    public TokenDto refreshToken(@RequestBody TokenDto tokenDto) {
        return authService.updateRefreshToken(tokenDto);
    }
}
