package com.example.autofinderbot.telegram.exception;

public class InvalidArgumentsException extends TelegramBotException {
    private static final String ERROR_MESSAGE = "Invalid arguments for command %s.";

    public InvalidArgumentsException(String command) {
        super(ERROR_MESSAGE.formatted(command));
    }
}
