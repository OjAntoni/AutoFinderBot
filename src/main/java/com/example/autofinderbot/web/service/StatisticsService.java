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

        Map<DayOfWeek, Double> avgCounts = offers.stream()
            .collect(Collectors.groupingBy(
                dc -> dc.getDay().getDayOfWeek(),
                Collectors.averagingLong(DayCount::getCount)
            ));

        Map<DayOfWeek, Double> avgPrices = averages.stream()
            .collect(Collectors.groupingBy(
                dp -> dp.getDay().getDayOfWeek(),
                Collectors.averagingDouble(DayPrice::getPrice)
            ));

        double meanCount = avgCounts.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double stdCount = Math.sqrt(avgCounts.values().stream()
            .mapToDouble(v -> Math.pow(v - meanCount, 2))
            .average()
            .orElse(0));

        double meanPrice = avgPrices.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double stdPrice = Math.sqrt(avgPrices.values().stream()
            .mapToDouble(v -> Math.pow(v - meanPrice, 2))
            .average()
            .orElse(0));

        List<DayOfWeek> bestDays = List.of();
        List<DayOfWeek> worstDays = List.of();

        if (stdCount != 0 && stdPrice != 0) {
            Map<DayOfWeek, Double> scores = avgCounts.keySet().stream()
                .filter(avgPrices::containsKey)
                .collect(Collectors.toMap(
                    dow -> dow,
                    dow -> ((avgCounts.get(dow) - meanCount) / stdCount)
                        - ((avgPrices.get(dow) - meanPrice) / stdPrice)
                ));

            bestDays = scores.entrySet().stream()
                .filter(e -> e.getValue() >= 1d)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            worstDays = scores.entrySet().stream()
                .filter(e -> e.getValue() < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            if (worstDays.isEmpty() && !scores.isEmpty()) {
                final double minScore = Collections.min(scores.values());
                worstDays = scores.entrySet().stream()
                    .filter(e -> e.getValue() == minScore)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            }
        }

        return new BrandStatisticsResponse(averages, predictions, offers, bestDays, worstDays);
    }

    private List<DayPrice> forecast(List<DayPrice> history) {
        int n = history.size();
        int period = 7;
        if (n < period) {
            return Collections.emptyList();
        }

        double alpha = 0.3;
        double beta = 0.1;
        double gamma = 0.3;

        List<Double> values = history.stream()
            .map(DayPrice::getPrice)
            .toList();

        double level = values.stream()
            .limit(period)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0);

        int trendSamples = Math.min(n - period, period);
        double trend = 0;
        for (int i = 0; i < trendSamples; i++) {
            trend += (values.get(i + period) - values.get(i)) / period;
        }
        if (trendSamples > 0) {
            trend /= trendSamples;
        }

        double[] season = new double[period];
        for (int i = 0; i < period; i++) {
            season[i] = values.get(i) - level;
        }

        for (int i = 0; i < n; i++) {
            double value = values.get(i);
            double prevLevel = level;
            double prevTrend = trend;
            double prevSeason = season[i % period];

            level = alpha * (value - prevSeason) + (1 - alpha) * (prevLevel + prevTrend);
            trend = beta * (level - prevLevel) + (1 - beta) * prevTrend;
            season[i % period] = gamma * (value - level) + (1 - gamma) * prevSeason;
        }

        LocalDate lastDay = history.get(n - 1).getDay();
        List<DayPrice> result = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            double forecast = level + i * trend + season[(n + i - 1) % period];
            result.add(new DayPrice(lastDay.plusDays(i), forecast));
        }
        return result;
    }
}
