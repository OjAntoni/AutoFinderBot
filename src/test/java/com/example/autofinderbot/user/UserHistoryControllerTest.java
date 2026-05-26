package com.example.autofinderbot.user;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.user.history.UserHistory;
import com.example.autofinderbot.configuration.PagedResponse;
import com.example.autofinderbot.configuration.TestRequestSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

class UserHistoryControllerTest extends BaseRestApiTest {
    @Autowired
    TestRequestSender testRequestSender;

    @Test
    void getUserHistory_PosTC() {
        ResponseEntity<PagedResponse<UserHistory>> response = testRequestSender.asAdmin("/api/users/history", GET, null, new ParameterizedTypeReference<PagedResponse<UserHistory>>() {});

        assertThat(response.getStatusCode())
            .isEqualTo(OK);

        assertThat(response.getBody())
            .isEmpty();
    }

    @Test
    void unauthorizedGetUserHistory_NegTC(){
        ResponseEntity<PagedResponse<UserHistory>> response = testRequestSender.unauthorized("/api/users/history", GET, null, new ParameterizedTypeReference<PagedResponse<UserHistory>>() {});

        assertThat(response.getStatusCode())
            .isEqualTo(UNAUTHORIZED);
    }
}