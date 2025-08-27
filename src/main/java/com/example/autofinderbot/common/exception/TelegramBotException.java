package com.example.autofinderbot.common.exception;

public abstract class TelegramBotException extends RuntimeException {
    public TelegramBotException(String message) {
        super(message);
    }
}
