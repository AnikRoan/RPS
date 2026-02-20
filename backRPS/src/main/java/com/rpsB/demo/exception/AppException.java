package com.rpsB.demo.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    @JsonProperty("status")
    private HttpStatus status;
    @JsonProperty("message")
    private String message;

    public AppException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
