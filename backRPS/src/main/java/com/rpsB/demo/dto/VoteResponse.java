package com.rpsB.demo.dto;

public record VoteResponse(
        Long id,
        Integer value,
        String note,
        String userName
) {
}
