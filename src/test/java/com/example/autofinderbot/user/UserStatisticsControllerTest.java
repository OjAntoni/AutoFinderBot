package com.example.autofinderbot.user;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.configuration.TestRequestSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

class UserStatisticsControllerTest extends BaseRestApiTest {
    @Autowired
    private TestRequestSender sender;

    @Test
    void getUserStatistics_PosTC() {
        ResponseEntity<UserStatisticsResponse> response = sender.asAdmin(
            "/api/statistics/users/all", GET, null, UserStatisticsResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
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
        ResponseEntity<UserStatisticsResponse> response = sender.unauthorized(
            "/api/statistics/users/all", GET, null, UserStatisticsResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }
}
