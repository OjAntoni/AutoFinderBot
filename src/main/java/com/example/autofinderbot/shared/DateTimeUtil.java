package com.example.autofinderbot.shared;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class DateTimeUtil {
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    public LocalDateTime now() {
        return LocalDateTime.now(WARSAW_ZONE);
    }

    public LocalDateTime convert(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(WARSAW_ZONE).toLocalDateTime();
    }
}
