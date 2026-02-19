package com.rpsB.demo.controller;

import com.rpsB.demo.dto.UserDto;
import com.rpsB.demo.dto.UserUpdateDto;
import com.rpsB.demo.security.oauth2.CurrentUserProvider;
import com.rpsB.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider userProvider;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        return ResponseEntity.ok().body(userService.getMe(userProvider.getAuthUserPrincipalId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMe(@RequestBody UserUpdateDto updateDto) {
        return ResponseEntity.ok().body(userService.updateMe(updateDto,userProvider.getAuthUserPrincipalId()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMe() {
        return ResponseEntity.ok().body(userService.deleteMe(userProvider.getAuthUserPrincipalId()));
    }
}
