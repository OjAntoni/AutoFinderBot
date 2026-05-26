package com.example.autofinderbot.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RemoveSelectedCarEvent extends ApplicationEvent {
    private final int messageId;
    private final long chatId;
    private final long carId;

    public RemoveSelectedCarEvent(Object source, int messageId, long chatId, long carId) {
        super(source);
        this.messageId = messageId;
        this.chatId = chatId;
        this.carId = carId;
    }
}
