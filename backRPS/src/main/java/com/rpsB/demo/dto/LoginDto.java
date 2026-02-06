package com.rpsB.demo.dto;

public record LoginDto(
        String email,
        String password,
        String code
) {
}
