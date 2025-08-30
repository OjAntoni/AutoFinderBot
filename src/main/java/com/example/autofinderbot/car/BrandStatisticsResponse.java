package com.example.autofinderbot.car;

import java.time.DayOfWeek;
import java.util.List;

public record BrandStatisticsResponse(
    List<DayPrice> averagePrices,
    List<DayPrice> predictedPrices,
    List<DayCount> dailyOffers,
    List<DayOfWeek> bestDays,
    List<DayOfWeek> worstDays
) {}
