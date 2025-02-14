package com.example.autofinderbot.web.controller;

import com.example.autofinderbot.configuration.BaseRestApiTest;

import com.example.autofinderbot.web.controller.AuthenticationController.LoginRequest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class AuthenticationControllerTest extends BaseRestApiTest {

    @Test
    void authenticateUser_PosTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .body(payload("admin_user", "password"))
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("tokenType", equalTo("Bearer"))
            .body("accessToken", Matchers.notNullValue());
    }

    @Test
    void invalidCredentials_NegTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .body(payload("invalid", "invalid"))
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(401);
    }

    private String payload(String username, String password) throws JsonProcessingException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        return objectMapper.writeValueAsString(loginRequest);
    }
}