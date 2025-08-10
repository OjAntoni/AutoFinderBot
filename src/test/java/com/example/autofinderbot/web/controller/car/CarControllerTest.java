package com.example.autofinderbot.web.controller.car;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.web.dto.car.CarResponse;
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

class CarControllerTest extends BaseRestApiTest {

    @Autowired
    TestRequestSender testRequestSender;

    @Test
    void getCars_PosTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.asAdmin(
            "/api/cars?brand=BMW", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
        assertThat(response.getBody().getContent().getFirst().title()).isEqualTo("BMW 3");
        assertThat(response.getBody().getContent().getFirst().thumbnailUrl()).isEqualTo("https://www.example.com/bmw-3-thumbnail");
    }

    @Test
    void pagination_PosTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.asAdmin(
            "/api/cars?size=2", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSize()).isEqualTo(2);
        assertThat(response.getBody().getContent()).hasSize(2);
    }

    @Test
    void fuelType_PosTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.asAdmin(
            "/api/cars?fuelType=Diesel", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(2);
        assertThat(response.getBody().getContent())
            .extracting(CarResponse::title)
            .containsExactlyInAnyOrder("Audi A4", "Mercedes C");
    }

    @Test
    void mileageRange_PosTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.asAdmin(
            "/api/cars?mileageFrom=150000&mileageTo=250000", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
        assertThat(response.getBody().getContent().getFirst().title()).isEqualTo("BMW 3");
    }

    @Test
    void priceRange_PosTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.asAdmin(
            "/api/cars?priceFrom=11000&priceTo=25000", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
        assertThat(response.getBody().getContent().getFirst().title()).isEqualTo("BMW 3");
    }

    @Test
    void unauthorizedGetCars_NegTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.unauthorized(
            "/api/cars", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }
}
