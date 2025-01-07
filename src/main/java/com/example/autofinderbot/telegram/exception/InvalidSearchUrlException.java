package com.example.autofinderbot.telegram.exception;

public class InvalidSearchUrlException extends TelegramBotException{
    private static final String ERROR_MESSAGE = "Search url is invalid because %s";

    public InvalidSearchUrlException(String reason) {
        super(ERROR_MESSAGE.formatted(reason));
    }
}
