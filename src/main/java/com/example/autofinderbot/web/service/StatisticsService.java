package com.example.autofinderbot.web.service;

import com.example.autofinderbot.shared.DateTimeUtil;
import com.example.autofinderbot.web.dto.statistics.BrandStatisticsResponse;
import com.example.autofinderbot.web.dto.statistics.DayCount;
import com.example.autofinderbot.web.dto.statistics.DayPrice;
import com.example.autofinderbot.web.dto.statistics.UserStatisticsResponse;
import com.example.autofinderbot.web.repository.BrandStatisticsRepository;
import com.example.autofinderbot.web.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StatisticsService {
    UserStatisticsRepository userStatisticsRepository;
    BrandStatisticsRepository brandStatisticsRepository;
    DateTimeUtil dateTimeUtil;

    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics() {
        LocalDateTime startOfToday = dateTimeUtil.now().toLocalDate().atStartOfDay();
        LocalDateTime startOfThisWeek = dateTimeUtil.now().toLocalDate().minusWeeks(1).atStartOfDay();
        return userStatisticsRepository.getUserStatistics(startOfToday, startOfThisWeek);
    }

    @Transactional(readOnly = true)
    public BrandStatisticsResponse getBrandStatistics(String brand) {
        LocalDateTime now = dateTimeUtil.now();
        LocalDateTime startDate = now.minusDays(60).toLocalDate().atStartOfDay();

        List<DayPrice> averages = brandStatisticsRepository.averagePriceByBrand(brand, startDate);
        List<DayCount> offers = brandStatisticsRepository.offersCountByBrand(brand, startDate);
        List<DayPrice> predictions = forecast(averages);

        Map<DayOfWeek, Long> countsByDay = offers.stream()
            .collect(Collectors.groupingBy(dc -> dc.day().getDayOfWeek(), Collectors.summingLong(DayCount::count)));

        double mean = countsByDay.values().stream().mapToLong(Long::longValue).average().orElse(0);
        double variance = countsByDay.values().stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0);
        double stdDev = Math.sqrt(variance);

        List<DayOfWeek> bestDays;
        List<DayOfWeek> worstDays;
        if (stdDev == 0) {
            bestDays = List.of();
            worstDays = List.of();
        } else {
            Map<DayOfWeek, Double> zScores = countsByDay.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue() - mean) / stdDev));

            bestDays = zScores.entrySet().stream()
                .filter(e -> e.getValue() >= 1d)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            worstDays = zScores.entrySet().stream()
                .filter(e -> e.getValue() <= -1d)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        }

        return new BrandStatisticsResponse(averages, predictions, offers, bestDays, worstDays);
    }

    private List<DayPrice> forecast(List<DayPrice> history) {
        int n = history.size();
        if (n < 2) {
            return Collections.emptyList();
        }

        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = history.get(i).price();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = n * sumX2 - sumX * sumX;
        double slope = denominator == 0 ? 0 : (n * sumXY - sumX * sumY) / denominator;
        double intercept = (sumY - slope * sumX) / n;

        LocalDate lastDay = history.get(n - 1).day();
        List<DayPrice> result = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            double x = n + i;
            double y = intercept + slope * x;
            result.add(new DayPrice(lastDay.plusDays(i + 1), y));
        }
        return result;
    }
}
