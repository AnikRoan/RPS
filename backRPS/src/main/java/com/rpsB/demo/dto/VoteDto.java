package com.rpsB.demo.dto;

public record VoteDto(
        Long id,
        Integer value,
        String userEmail,
        String note
) {
}
