package com.example.autofinderbot.common.config.web;

import com.example.autofinderbot.common.exception.ApiConflictException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionErrorHandler {

    @ExceptionHandler(ApiConflictException.class)
    public ResponseEntity<ErrorMessageResponse> handleApiConflictException(ApiConflictException exception) {
        return new ResponseEntity<>(new ErrorMessageResponse(exception.getMessage()), HttpStatusCode.valueOf(exception.getStatusCode()));
    }
}
