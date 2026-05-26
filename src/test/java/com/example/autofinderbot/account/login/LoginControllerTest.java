package com.example.autofinderbot.account.login;

import com.example.autofinderbot.configuration.BaseRestApiTest;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.*;

class LoginControllerTest extends BaseRestApiTest {

    @Test
    void authenticateUser_PosTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .body(payload("admin_user", "password"))
        .when()
            .post("/api/account/login")
        .then()
            .statusCode(OK.value())
            .body("tokenType", equalTo("Bearer"))
            .body("accessToken", Matchers.notNullValue())
            .body("expiresAt", Matchers.notNullValue());
    }

    @Test
    void invalidCredentials_NegTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .body(payload("invalid", "invalid"))
        .when()
            .post("/api/account/login")
        .then()
            .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void nullAndEmptyCredentials_NegTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .body(payload(null, ""))
        .when()
            .post("/api/account/login")
        .then()
            .statusCode(BAD_REQUEST.value())
            .body("errors.error", containsInAnyOrder(
                "username must not be empty",
                "password must not be empty"
            ));
    }

    private String payload(String username, String password) throws JsonProcessingException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        return objectMapper.writeValueAsString(loginRequest);
    }
}