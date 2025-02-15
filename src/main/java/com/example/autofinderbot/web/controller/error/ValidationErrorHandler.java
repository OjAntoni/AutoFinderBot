package com.example.autofinderbot.web.controller.error;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> new ValidationError(error.getField(), "%s %s".formatted(error.getField(), error.getDefaultMessage())))
            .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse("Validation failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Data
    public static class ValidationErrorResponse {
        private String message;
        private List<ValidationError> errors;

        public ValidationErrorResponse(String message, List<ValidationError> errors) {
            this.message = message;
            this.errors = errors;
        }
    }

    @Data
    public static class ValidationError {
        private String field;
        private String error;

        public ValidationError(String field, String error) {
            this.field = field;
            this.error = error;
        }
    }
}
