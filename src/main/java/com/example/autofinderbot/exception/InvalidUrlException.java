package com.example.autofinderbot.exception;

public class InvalidUrlException extends Exception {
    private static final String ERROR_MESSAGE = "Provided URL '%s' is invalid.";

    public InvalidUrlException(String url) {
        super(ERROR_MESSAGE.formatted(url));
    }
}
