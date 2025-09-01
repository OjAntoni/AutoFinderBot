package com.example.autofinderbot.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@Component
public class JwtTestHelper {

    private final ObjectProvider<TestRestTemplate> restTemplateProvider;

    public JwtTestHelper(ObjectProvider<TestRestTemplate> restTemplateProvider) {
        this.restTemplateProvider = restTemplateProvider;
    }

    public String loginAs(String username, String password) {
        Map<String, String> credentials = Map.of("username", username, "password", password);
        ResponseEntity<Map<String, Object>> response = restTemplateProvider.getObject()
            .exchange(
                "/api/account/login",
                POST,
                new HttpEntity<>(credentials),
                new ParameterizedTypeReference<>() {}
            );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Failed to log in and retrieve token");
        }

        return (String) response.getBody().get("accessToken");
    }

    public HttpHeaders createAuthHeaders(String username, String password) {
        String token = loginAs(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

