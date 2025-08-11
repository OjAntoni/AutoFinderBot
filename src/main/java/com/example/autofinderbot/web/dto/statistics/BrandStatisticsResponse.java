package com.example.autofinderbot.web.dto.statistics;

import java.time.DayOfWeek;
import java.util.List;

public record BrandStatisticsResponse(
    List<DayPrice> averagePrices,
    List<DayPrice> predictedPrices,
    List<DayCount> dailyOffers,
    List<DayOfWeek> bestDays,
    List<DayOfWeek> worstDays
) {}
