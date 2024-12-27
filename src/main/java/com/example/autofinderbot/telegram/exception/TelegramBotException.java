package com.example.autofinderbot.telegram.exception;

public abstract class TelegramBotException extends RuntimeException {
    public TelegramBotException(String message) {
        super(message);
    }
}
