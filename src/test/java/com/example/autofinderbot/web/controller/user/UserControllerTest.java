package com.example.autofinderbot.web.controller.user;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.user.User;
import com.example.autofinderbot.web.util.PagedResponse;
import com.example.autofinderbot.web.util.TestRequestSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

class UserControllerTest extends BaseRestApiTest {

    @Autowired
    TestRequestSender testRequestSender;

    @Test
    void getAllUsers_PosTC() {
        ParameterizedTypeReference<PagedResponse<User>> typeRef = new ParameterizedTypeReference<>() {};

        ResponseEntity<PagedResponse<User>> response = testRequestSender.asAdmin("/api/users", GET, null, typeRef);

        assertThat(response.getStatusCode())
            .isEqualTo(OK);

        assertThat(response.getBody())
            .flatExtracting(
                User::getId,
                User::getFirstname
            ).containsExactly(
                1L, "John",
                2L, "Alice",
                3L, "Bob",
                4L, "Charlie",
                5L, "David",
                6L, "Eve",
                7L, "Eve"
            );
    }

    @Test
    void unauthorizedGetAllUsers_NegTC() {
        ParameterizedTypeReference<PagedResponse<User>> typeRef = new ParameterizedTypeReference<>() {};

        ResponseEntity<PagedResponse<User>> response = testRequestSender.unauthorized("/api/users", GET, null, typeRef);

        assertThat(response.getStatusCode())
            .isEqualTo(UNAUTHORIZED);
    }
}