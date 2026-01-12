package com.example.autofinderbot.car.statistics;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.user.UserStatisticsResponse;
import com.example.autofinderbot.configuration.TestRequestSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

class StatisticsControllerTest extends BaseRestApiTest {
    @Autowired
    private TestRequestSender sender;

    @Test
    void getUserStatistics_PosTC() {
        ResponseEntity<UserStatisticsResponse> response = sender.asAdmin("/api/statistics/users/all", GET, null, UserStatisticsResponse.class);

        assertThat(response.getStatusCode())
            .isEqualTo(OK);

        assertThat(response.getBody())
            .extracting(
                UserStatisticsResponse::allUsers,
                UserStatisticsResponse::hasActiveFilter,
                UserStatisticsResponse::hasFilter,
                UserStatisticsResponse::onlineThisWeek,
                UserStatisticsResponse::onlineToday
            ).containsExactly(
                7L, 2L, 3L, 0L, 0L
            );
    }

    @Test
    void unauthorizedGetUserStatistics_NegTC() {
        ResponseEntity<UserStatisticsResponse> response = sender.unauthorized("/api/statistics/users/all", GET, null, UserStatisticsResponse.class);

        assertThat(response.getStatusCode())
            .isEqualTo(UNAUTHORIZED);
    }

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

