package com.example.autofinderbot.web.controller.car;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.web.dto.car.CarResponse;
import com.example.autofinderbot.web.util.PagedResponse;
import com.example.autofinderbot.web.util.TestRequestSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import static com.example.autofinderbot.web.dto.car.CarResponse.PriceComparison.HIGHER;
import static com.example.autofinderbot.web.dto.car.CarResponse.PriceComparison.UNDEFINED;
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
        assertThat(response.getBody().getTotalElements()).isEqualTo(11L);
        assertThat(response.getBody().getContent().getFirst().getTitle()).isEqualTo("BMW 3 hist 8d");
        assertThat(response.getBody().getContent().getFirst().getPriceComparison()).isEqualTo(UNDEFINED);
        assertThat(response.getBody().getContent().getFirst().getThumbnailUrl()).isEqualTo("https://www.example.com/bmw-3-22-thumb");
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
        assertThat(response.getBody().getTotalElements()).isEqualTo(7);
        assertThat(response.getBody().getContent())
            .extracting(CarResponse::getTitle)
            .containsExactlyInAnyOrder(
                "Audi A4",
                "Mercedes C",
                "Audi A4 similar A",
                "Audi A4 similar B",
                "Audi A4 far mileage",
                "Mercedes C no-mileage",
                "Mercedes C case-key");
    }

    @Test
    void mileageRange_PosTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.asAdmin(
            "/api/cars?mileageFrom=150000&mileageTo=250000", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(11L);
        assertThat(response.getBody().getContent().getFirst().getTitle()).isEqualTo("BMW 3 hist 8d");
    }

    @Test
    void priceRange_PosTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.asAdmin(
            "/api/cars?priceFrom=11000&priceTo=25000", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(13L);
        assertThat(response.getBody().getContent().getFirst().getTitle()).isEqualTo("BMW 3 hist 8d");
    }

    @Test
    void unauthorizedGetCars_NegTC() {
        ParameterizedTypeReference<PagedResponse<CarResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<PagedResponse<CarResponse>> response = testRequestSender.unauthorized(
            "/api/cars", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }
}
