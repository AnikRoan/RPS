package com.rpsB.demo.dto;

public record UserUpdateDto(
        String name,
        String email,
        String password,
        String avatar
) {
}
