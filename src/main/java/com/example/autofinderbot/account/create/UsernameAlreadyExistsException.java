package com.example.autofinderbot.account.create;

import com.example.autofinderbot.common.exception.ApiConflictException;

class UsernameAlreadyExistsException extends ApiConflictException {
    private static final String MESSAGE = "Username %s already exists";

    public UsernameAlreadyExistsException(String username) {
        super(MESSAGE.formatted(username));
    }
}
