package com.example.autofinderbot.web.controller.error;

import com.example.autofinderbot.web.dto.error.ErrorMessageResponse;
import com.example.autofinderbot.web.exception.ApiConflictException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionControllerAdvice {

    @ExceptionHandler(ApiConflictException.class)
    public ResponseEntity<ErrorMessageResponse> handleApiConflictException(ApiConflictException exception) {
        return new ResponseEntity<>(new ErrorMessageResponse(exception.getMessage()), HttpStatusCode.valueOf(exception.getStatusCode()));
    }
}
