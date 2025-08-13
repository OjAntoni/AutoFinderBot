package com.example.autofinderbot.web.controller.dictionary;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.web.dto.dictionary.DictionaryResponse;
import com.example.autofinderbot.web.dto.dictionary.GearboxTypeResponse;
import com.example.autofinderbot.web.util.TestRequestSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

class DictionaryControllerTest extends BaseRestApiTest {

    @Autowired
    TestRequestSender testRequestSender;

    @Test
    void getCarBrands_PosTC() {
        ParameterizedTypeReference<List<DictionaryResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/car-brands", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .containsExactlyInAnyOrder("Audi", "BMW");
    }

    @Test
    void getFuelTypes_PosTC() {
        ParameterizedTypeReference<List<DictionaryResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/fuel-types", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .containsExactlyInAnyOrder("Diesel", "Petrol");
    }

    @Test
    void getGearboxTypes_PosTC() {
        ParameterizedTypeReference<List<GearboxTypeResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<GearboxTypeResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/gearbox-types", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(GearboxTypeResponse::getSearchKey)
            .containsExactlyInAnyOrder("manual", "automatic");
    }

    @Test
    void getCarModels_PosTC() {
        ParameterizedTypeReference<List<DictionaryResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/car-brands/1/models", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .containsExactlyInAnyOrder("A4", "A6");
    }

    @Test
    void getGenerations_PosTC() {
        ParameterizedTypeReference<List<DictionaryResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/car-models/1/generations", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .containsExactlyInAnyOrder("B8", "B9");
    }
}
