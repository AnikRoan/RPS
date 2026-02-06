package com.rpsB.demo.dto;

import lombok.Builder;

@Builder
public record TokenDto(
        String accessToken,
        String refreshToken,
        boolean mfaEnabled
) {
}
