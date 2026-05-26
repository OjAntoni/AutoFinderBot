package com.example.autofinderbot.user;

public record UserStatisticsResponse (
    long allUsers,
    long onlineToday,
    long onlineThisWeek,
    long hasFilter,
    long hasActiveFilter
){}
