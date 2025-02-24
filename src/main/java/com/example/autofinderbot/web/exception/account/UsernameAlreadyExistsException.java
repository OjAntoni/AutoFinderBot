package com.example.autofinderbot.web.exception.account;

import com.example.autofinderbot.web.exception.ApiConflictException;

public class UsernameAlreadyExistsException extends ApiConflictException {
    private static final String MESSAGE = "Username %s already exists";

    public UsernameAlreadyExistsException(String username) {
        super(MESSAGE.formatted(username));
    }
}
