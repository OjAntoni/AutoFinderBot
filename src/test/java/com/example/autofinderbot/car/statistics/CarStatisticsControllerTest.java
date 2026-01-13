package com.example.autofinderbot.car.statistics;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.configuration.TestRequestSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

class CarStatisticsControllerTest extends BaseRestApiTest {
    @Autowired
    private TestRequestSender sender;

    @Test
    void getBrandStatistics_PosTC() {
        ResponseEntity<BrandStatisticsResponse> response = sender.asAdmin(
            "/api/statistics/cars/BMW", GET, null, BrandStatisticsResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);

        BrandStatisticsResponse body = response.getBody();

        LocalDate today = LocalDate.now(ZoneId.of("Europe/Warsaw"));
        LocalDate twoDaysAgo = today.minusDays(2);

        Assertions.assertNotNull(body);

        assertThat(body.averagePrices())
            .containsExactly(
                new DayPrice(today.minusDays(8), 20000d),
                new DayPrice(today.minusDays(7), 18000d),
                new DayPrice(today.minusDays(6), 20000d),
                new DayPrice(today.minusDays(5), 19000d),
                new DayPrice(today.minusDays(4), 20000d),
                new DayPrice(today.minusDays(3), 21000d),
                new DayPrice(twoDaysAgo, 20000d),
                new DayPrice(today, 20250d)
            );

        assertThat(body.predictedPrices()).hasSize(30);
        assertThat(body.predictedPrices().getFirst().getDay()).isEqualTo(today.plusDays(1));

        assertThat(body.dailyOffers())
            .containsExactly(
                new DayCount(today.minusDays(8), 1L),
                new DayCount(today.minusDays(7), 1L),
                new DayCount(today.minusDays(6), 1L),
                new DayCount(today.minusDays(5), 1L),
                new DayCount(today.minusDays(4), 1L),
                new DayCount(today.minusDays(3), 1L),
                new DayCount(twoDaysAgo, 1L),
                new DayCount(today, 4L)
            );

        DayOfWeek todayDay = today.getDayOfWeek();
        DayOfWeek fiveDaysAgoDay = today.minusDays(5).getDayOfWeek();
        List<DayOfWeek> expectedBestDays = List.of(todayDay, fiveDaysAgoDay);
        EnumSet<DayOfWeek> expectedWorstDays = EnumSet.complementOf(EnumSet.of(todayDay, fiveDaysAgoDay));

        assertThat(body.bestDays())
            .containsExactlyInAnyOrderElementsOf(expectedBestDays);
        assertThat(body.worstDays())
            .containsExactlyInAnyOrderElementsOf(expectedWorstDays);
    }

    @Test
    void unauthorizedGetBrandStatistics_NegTC() {
        ResponseEntity<BrandStatisticsResponse> response = sender.unauthorized(
            "/api/statistics/cars/BMW", GET, null, BrandStatisticsResponse.class
        );

        assertThat(response.getStatusCode())
            .isEqualTo(UNAUTHORIZED);
    }
}

