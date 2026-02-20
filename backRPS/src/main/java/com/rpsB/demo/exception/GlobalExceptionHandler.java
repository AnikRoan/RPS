package com.rpsB.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<AppExceptionDto> handleAppException(AppException e) {
        AppExceptionDto error = new AppExceptionDto(
                e.getStatus().value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppExceptionDto> handleGenericException(Exception e) {

        AppExceptionDto error = new AppExceptionDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error occurred"
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
