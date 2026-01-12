package com.example.autofinderbot.car.statistics;

import com.example.autofinderbot.car.statistics.StatisticsService;
import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.common.util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.DayOfWeek.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class StatisticsServiceTest extends BaseSpringBootTest {
    @MockitoBean
    BrandStatisticsRepository brandStatisticsRepository;
    @MockitoBean
    DateTimeUtil dateTimeUtil;
    @Autowired
    StatisticsService statisticsService;

    @Test
    void getBrandStatisticsZeroStd_PosTC() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 8, 0, 0);
        when(dateTimeUtil.now()).thenReturn(now);

        List<DayPrice> averages = List.of(
            new DayPrice(now.toLocalDate().minusDays(1), 100d),
            new DayPrice(now.toLocalDate(), 100d)
        );
        List<DayCount> offers = List.of(
            new DayCount(now.toLocalDate().minusDays(1), 1L),
            new DayCount(now.toLocalDate(), 1L)
        );

        when(brandStatisticsRepository.averagePriceByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(averages);
        when(brandStatisticsRepository.offersCountByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(offers);

        BrandStatisticsResponse response = statisticsService.getBrandStatistics("brand");

        assertThat(response.averagePrices()).isEqualTo(averages);
        assertThat(response.dailyOffers()).isEqualTo(offers);
        assertThat(response.predictedPrices()).isEmpty();
        assertThat(response.bestDays()).isEmpty();
        assertThat(response.worstDays()).isEmpty();
    }

    @Test
    void getBrandStatisticsWithNegativeScore_PosTC() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 8, 0, 0);
        when(dateTimeUtil.now()).thenReturn(now);

        LocalDate base = LocalDate.of(2024, 1, 1); // Monday
        List<DayPrice> averages = List.of(
            new DayPrice(base.plusDays(0), 100d),
            new DayPrice(base.plusDays(1), 200d),
            new DayPrice(base.plusDays(2), 150d),
            new DayPrice(base.plusDays(3), 150d),
            new DayPrice(base.plusDays(4), 150d),
            new DayPrice(base.plusDays(5), 150d),
            new DayPrice(base.plusDays(6), 150d)
        );
        List<DayCount> offers = List.of(
            new DayCount(base.plusDays(0), 1L),
            new DayCount(base.plusDays(1), 3L),
            new DayCount(base.plusDays(2), 2L),
            new DayCount(base.plusDays(3), 2L),
            new DayCount(base.plusDays(4), 2L),
            new DayCount(base.plusDays(5), 2L),
            new DayCount(base.plusDays(6), 2L)
        );

        when(brandStatisticsRepository.averagePriceByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(averages);
        when(brandStatisticsRepository.offersCountByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(offers);

        BrandStatisticsResponse response = statisticsService.getBrandStatistics("brand");

        assertThat(response.predictedPrices()).hasSize(30);
        assertThat(response.bestDays()).isEmpty();
        assertThat(response.worstDays())
            .containsExactlyInAnyOrder(MONDAY);
    }

    @Test
    void getBrandStatisticsNoNegativeScore_PosTC() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 8, 0, 0);
        when(dateTimeUtil.now()).thenReturn(now);

        LocalDate base = LocalDate.of(2024, 1, 1); // Monday
        List<DayPrice> averages = List.of(
            new DayPrice(base.plusDays(0), 500d),
            new DayPrice(base.plusDays(1), 500d),
            new DayPrice(base.plusDays(2), 500d),
            new DayPrice(base.plusDays(3), 500d),
            new DayPrice(base.plusDays(4), 500d),
            new DayPrice(base.plusDays(5), 500d),
            new DayPrice(base.plusDays(6), 100d)
        );
        List<DayCount> offers = List.of(
            new DayCount(base.plusDays(0), 1L),
            new DayCount(base.plusDays(1), 1L),
            new DayCount(base.plusDays(2), 1L),
            new DayCount(base.plusDays(3), 1L),
            new DayCount(base.plusDays(4), 1L),
            new DayCount(base.plusDays(5), 1L),
            new DayCount(base.plusDays(6), 3L)
        );

        when(brandStatisticsRepository.averagePriceByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(averages);
        when(brandStatisticsRepository.offersCountByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(offers);

        BrandStatisticsResponse response = statisticsService.getBrandStatistics("brand");

        assertThat(response.predictedPrices()).hasSize(30);
        assertThat(response.bestDays())
            .containsExactlyInAnyOrder(SUNDAY);
        assertThat(response.worstDays())
            .containsExactlyInAnyOrder(WEDNESDAY, MONDAY, SATURDAY, FRIDAY, TUESDAY, THURSDAY);
    }

    @Test
    void getBrandStatistics_FallbackMinScore_PosTC() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 8, 0, 0);
        when(dateTimeUtil.now()).thenReturn(now);

        LocalDate base = LocalDate.of(2024, 1, 1); // Monday
        List<DayPrice> averages = List.of(
            new DayPrice(base.plusDays(0), 100d),
            new DayPrice(base.plusDays(1), 200d),
            new DayPrice(base.plusDays(2), 300d)
        );
        List<DayCount> offers = List.of(
            new DayCount(base.plusDays(0), 1L),
            new DayCount(base.plusDays(1), 2L),
            new DayCount(base.plusDays(2), 3L)
        );

        when(brandStatisticsRepository.averagePriceByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(averages);
        when(brandStatisticsRepository.offersCountByBrand(eq("brand"), any(LocalDateTime.class)))
            .thenReturn(offers);

        BrandStatisticsResponse response = statisticsService.getBrandStatistics("brand");

        assertThat(response.bestDays()).isEmpty();
        assertThat(response.worstDays())
            .containsExactlyInAnyOrder(
                java.time.DayOfWeek.MONDAY,
                java.time.DayOfWeek.TUESDAY,
                java.time.DayOfWeek.WEDNESDAY
            );
    }
}

