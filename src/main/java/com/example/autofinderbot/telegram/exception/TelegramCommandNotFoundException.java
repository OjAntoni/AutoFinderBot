package com.example.autofinderbot.telegram.exception;

public class TelegramCommandNotFoundException extends TelegramBotException {
    private static final String ERROR_MESSAGE = "Command %s not found.";

    public TelegramCommandNotFoundException(String command) {
        super(ERROR_MESSAGE.formatted(command));
    }
}
