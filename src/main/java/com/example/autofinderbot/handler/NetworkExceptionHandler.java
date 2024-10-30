package com.example.autofinderbot.handler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.SocketTimeoutException;

@Slf4j
@ControllerAdvice
public class NetworkExceptionHandler {

    @ExceptionHandler(SocketTimeoutException.class)
    void handleSocketTimeoutException(SocketTimeoutException e) {
        log.error(e.getMessage());
    }

    @ExceptionHandler(HttpStatusException.class)
    void handleHttpStatusException(HttpStatusException e) {
        log.error("Failed to load %s, status code is %s".formatted(e.getUrl(), e.getStatusCode()));
    }

}
