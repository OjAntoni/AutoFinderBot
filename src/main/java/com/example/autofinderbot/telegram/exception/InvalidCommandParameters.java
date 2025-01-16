package com.example.autofinderbot.telegram.exception;

public class InvalidCommandParameters extends TelegramBotException{
    private static final String MESSAGE = "Invalid command parameters.";

    public InvalidCommandParameters() {
        super(MESSAGE);
    }
}
