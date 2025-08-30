package com.example.autofinderbot.account;

import jakarta.validation.constraints.Pattern;

public record CreateAccountRequest(
    @Pattern(regexp = CreateAccountRequest.USERNAME_REGEX, message = CreateAccountRequest.USERNAME_MESSAGE)
    String username,
    @Pattern(regexp = CreateAccountRequest.PASSWORD_REGEX, message = CreateAccountRequest.PASSWORD_MESSAGE)
    String password) {
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{3,20}$";
    private static final String USERNAME_MESSAGE = "must be 3-20 characters long and can only contain letters, digits, underscores, hyphens, or periods";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private static final String PASSWORD_MESSAGE = "must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit";
}
