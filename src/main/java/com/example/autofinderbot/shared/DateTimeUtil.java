package com.example.autofinderbot.shared;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DateTimeUtil {
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    public LocalDateTime now() {
        return LocalDateTime.now(WARSAW_ZONE);
    }
}
