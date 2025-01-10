package com.example.autofinderbot.telegram.exception;

public class UserIsNotRedirectedException extends TelegramBotException{
    private static final String MESSAGE = "User is not redirected to this method and can't access it.";

    public UserIsNotRedirectedException() {
        super(MESSAGE);
    }
}
