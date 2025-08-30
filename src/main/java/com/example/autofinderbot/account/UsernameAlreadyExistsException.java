package com.example.autofinderbot.account;

import com.example.autofinderbot.common.exception.ApiConflictException;

public class UsernameAlreadyExistsException extends ApiConflictException {
    private static final String MESSAGE = "Username %s already exists";

    public UsernameAlreadyExistsException(String username) {
        super(MESSAGE.formatted(username));
    }
}
