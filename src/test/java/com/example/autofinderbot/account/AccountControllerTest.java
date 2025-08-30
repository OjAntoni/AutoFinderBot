package com.example.autofinderbot.account;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import static com.example.autofinderbot.account.Account.Role.ADMIN;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.*;

class AccountControllerTest extends BaseRestApiTest {
    @Autowired
    AccountRepository accountRepository;

    @Test
    void createAccount_PosTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwt(1L))
            .body(bodyJson("test", "test1111A"))
        .when()
            .post("/api/account")
        .then()
            .statusCode(OK.value());

        Assertions.assertThat(accountRepository.findByUsername("test"))
            .isPresent()
            .get()
            .extracting(
                Account::getUsername,
                Account::getPassword,
                Account::getRole
            ).contains(
                "test",
                "test",
                ADMIN
            );
    }

    @Test
    void notUniqueUsername_NegTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwt(1L))
            .body(bodyJson("admin_user", "admin1111A"))
        .when()
            .post("/api/account")
        .then()
            .body("message", equalTo("Username admin_user already exists"))
            .statusCode(CONFLICT.value());
    }

    @Test
    void invalidRole_NegTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwt(2L))
            .body(bodyJson("test", "test111A"))
        .when()
            .post("/api/account")
        .then()
            .statusCode(FORBIDDEN.value());
    }

    @Test
    void unauthorized_NegTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .body(bodyJson("test", "test"))
        .when()
            .post("/api/account")
        .then()
            .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void invalidUsernameAndPassword_NegTC() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwt(1L))
            .body(bodyJson("t", "t"))
        .when()
            .post("/api/account")
        .then()
            .statusCode(BAD_REQUEST.value())
            .body("errors.error", containsInAnyOrder(
                "username must be 3-20 characters long and can only contain letters, digits, underscores, hyphens, or periods",
                "password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit"
            ));
    }


    private String bodyJson(String username, String password) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new CreateAccountRequest(username, password));
    }

}