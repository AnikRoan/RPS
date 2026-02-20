package com.rpsB.demo.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppExceptionDto {
    @JsonProperty("status")
    private int status;
    @JsonProperty("message")
    private String message;

    public AppExceptionDto(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
