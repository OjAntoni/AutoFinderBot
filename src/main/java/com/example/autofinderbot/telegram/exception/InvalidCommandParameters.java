package com.example.autofinderbot.telegram.exception;

import com.example.autofinderbot.common.exception.TelegramBotException;

public class InvalidCommandParameters extends TelegramBotException {
    private static final String MESSAGE = "Invalid command parameters.";

    public InvalidCommandParameters() {
        super(MESSAGE);
    }
}
