package com.example.autofinderbot.web.controller.statistics;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.web.dto.statistics.BrandStatisticsResponse;
import com.example.autofinderbot.web.dto.statistics.DayCount;
import com.example.autofinderbot.web.dto.statistics.DayPrice;
import com.example.autofinderbot.web.dto.statistics.UserStatisticsResponse;
import com.example.autofinderbot.web.util.TestRequestSender;
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

        assertThat(body.averagePrices())
            .containsExactly(
                new DayPrice(twoDaysAgo, 20000d),
                new DayPrice(today, 20250d)
            );

        assertThat(body.predictedPrices()).hasSize(30);
        assertThat(body.predictedPrices().getFirst().day()).isEqualTo(today.plusDays(1));

        assertThat(body.dailyOffers())
            .containsExactly(
                new DayCount(twoDaysAgo, 1L),
                new DayCount(today, 4L)
            );

        DayOfWeek todayDow = today.getDayOfWeek();
        DayOfWeek twoDaysAgoDow = twoDaysAgo.getDayOfWeek();

        assertThat(body.bestDays()).isEqualTo(List.of(todayDow));
        assertThat(body.worstDays()).isEqualTo(List.of(twoDaysAgoDow));
    }

    @Test
    void unauthorizedGetBrandStatistics_NegTC() {
        ResponseEntity<BrandStatisticsResponse> response = sender.unauthorized(
            "/api/statistics/cars/BMW", GET, null, BrandStatisticsResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }
}