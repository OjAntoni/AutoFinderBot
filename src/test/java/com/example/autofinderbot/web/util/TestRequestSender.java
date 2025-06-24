package com.example.autofinderbot.web.util;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

@Component
public class TestRequestSender {

    private final ObjectProvider<TestRestTemplate> restTemplateProvider;
    private final JwtTestHelper jwtHelper;

    public TestRequestSender(ObjectProvider<TestRestTemplate> restTemplateProvider, JwtTestHelper jwtHelper) {
        this.restTemplateProvider = restTemplateProvider;
        this.jwtHelper = jwtHelper;
    }

    public <T> ResponseEntity<T> unauthorized(
        String url, HttpMethod method, Object body, Class<T> responseType) {

        HttpEntity<?> request = createEntity(body, null);
        return restTemplateProvider.getObject().exchange(url, method, request, responseType);
    }

    public <T> ResponseEntity<T> unauthorized(
        String url, HttpMethod method, Object body, ParameterizedTypeReference<T> responseType
    ) {
        HttpEntity<?> request = createEntity(body, null);
        return restTemplateProvider.getObject().exchange(url, method, request, responseType);
    }

    public <T> ResponseEntity<T> asAdmin(
        String url, HttpMethod method, Object body, Class<T> responseType) {

        return sendWithAuth(url, method, body, responseType, "admin_user", "password");
    }

    public <T> ResponseEntity<T> asAdmin(
        String url, HttpMethod method, Object body, ParameterizedTypeReference<T> responseType
    ) {
        return sendWithAuth(url, method, body, responseType, "admin_user", "password");
    }

    public <T> ResponseEntity<T> as(
        String username, String password, String url, HttpMethod method,
        Object body, Class<T> responseType) {

        return sendWithAuth(url, method, body, responseType, username, password);
    }

    public <T> ResponseEntity<T> as(
        String username, String password,
        String url, HttpMethod method, Object body, ParameterizedTypeReference<T> responseType) {
        return sendWithAuth(url, method, body, responseType, username, password);
    }

    private <T> ResponseEntity<T> sendWithAuth(
        String url, HttpMethod method, Object body,
        Class<T> responseType, String username, String password) {

        HttpHeaders headers = jwtHelper.createAuthHeaders(username, password);
        HttpEntity<?> request = createEntity(body, headers);
        return restTemplateProvider.getObject().exchange(url, method, request, responseType);
    }

    private <T> ResponseEntity<T> sendWithAuth(
        String url, HttpMethod method, Object body,
        ParameterizedTypeReference<T> responseType, String username, String password
    ) {
        HttpHeaders headers = jwtHelper.createAuthHeaders(username, password);
        HttpEntity<?> request = createEntity(body, headers);
        return restTemplateProvider.getObject().exchange(url, method, request, responseType);
    }

    private HttpEntity<?> createEntity(Object body, HttpHeaders headers) {
        HttpHeaders finalHeaders = new HttpHeaders();

        if (body != null) {
            finalHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

        if (headers != null) {
            headers.forEach(finalHeaders::putIfAbsent);
        }

        return new HttpEntity<>(body, finalHeaders);
    }











}

