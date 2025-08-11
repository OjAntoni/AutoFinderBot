package com.example.autofinderbot.web.dto.statistics;

import java.time.LocalDate;

public record DayCount(
    LocalDate day,
    long count
) {}
