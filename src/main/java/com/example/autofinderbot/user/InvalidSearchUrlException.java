package com.example.autofinderbot.user;

import com.example.autofinderbot.common.exception.TelegramBotException;

public class InvalidSearchUrlException extends TelegramBotException {
    public InvalidSearchUrlException(String reason) {
        super(reason);
    }
}
