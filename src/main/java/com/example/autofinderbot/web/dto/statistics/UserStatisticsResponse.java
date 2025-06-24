package com.example.autofinderbot.web.dto.statistics;

public record UserStatisticsResponse (
    long allUsers,
    long onlineToday,
    long onlineThisWeek,
    long hasFilter,
    long hasActiveFilter
){}
