package com.example.autofinderbot.account;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class AuthenticationControllerAdvice {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED)
    public void handleUsernameNotFoundException(AuthenticationException e) {}
}
