package com.example.autofinderbot.web.dto.statistics;

import java.time.LocalDate;

public record DayPrice(
    LocalDate day,
    double price
) {}
