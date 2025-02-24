package com.example.autofinderbot.web.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiConflictException extends RuntimeException {
    private final int statusCode = HttpStatus.CONFLICT.value();
    private final String message;

    public ApiConflictException(String message) {
        this.message = message;
    }
}
