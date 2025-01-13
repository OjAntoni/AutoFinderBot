package com.example.autofinderbot.telegram.exception;

public class InvalidSearchUrlException extends TelegramBotException {
    public InvalidSearchUrlException(String reason) {
        super(reason);
    }
}
