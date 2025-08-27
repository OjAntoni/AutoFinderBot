package com.example.autofinderbot.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DateTimeUtil {
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    public LocalDateTime now() {
        return LocalDateTime.now(WARSAW_ZONE);
    }

    public LocalDateTime convert(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(WARSAW_ZONE).toLocalDateTime();
    }

    public LocalDateTime convert(OffsetDateTime offsetDateTime) {
        return offsetDateTime.atZoneSameInstant(WARSAW_ZONE).toLocalDateTime();
    }

    public Date convert(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(WARSAW_ZONE).toInstant());
    }
}
