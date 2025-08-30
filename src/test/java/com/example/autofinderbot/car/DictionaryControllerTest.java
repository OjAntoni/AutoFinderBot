package com.example.autofinderbot.car;

import com.example.autofinderbot.configuration.BaseRestApiTest;
import com.example.autofinderbot.car.brand.CarBrand;
import com.example.autofinderbot.car.model.CarModel;
import com.example.autofinderbot.car.generation.Generation;
import com.example.autofinderbot.car.brand.CarBrandRepository;
import com.example.autofinderbot.configuration.TestRequestSender;
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

    @Autowired
    CarBrandRepository carBrandRepository;

    @Test
    void getCarBrands_PosTC() {
        ParameterizedTypeReference<List<DictionaryResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/car-brands", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .contains("Audi", "BMW");
    }

    @Test
    void getFuelTypes_PosTC() {
        ParameterizedTypeReference<List<DictionaryResponse>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/fuel-types", GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .contains("Diesel", "Benzyna");
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
        CarBrand audi = carBrandRepository.findAllBySearchKeyIn(List.of("audi")).getFirst();
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/car-brands/%d/models".formatted(audi.getId()), GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .contains("A4", "A6");
    }

    @Test
    void getGenerations_PosTC() {
        ParameterizedTypeReference<List<DictionaryResponse>> type = new ParameterizedTypeReference<>() {};
        CarModel audiModel = carBrandRepository.findAllBySearchKeyIn(List.of("audi")).getFirst()
            .getModels().stream().filter(model -> model.getName().equals("A4")).toList().getFirst();
        List<Generation> audiGenerations = audiModel.getGenerations();
        ResponseEntity<List<DictionaryResponse>> response = testRequestSender.asAdmin(
            "/api/dictionaries/car-models/%d/generations".formatted(audiModel.getId()), GET, null, type);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(DictionaryResponse::getName)
            .containsExactlyInAnyOrderElementsOf(audiGenerations.stream().map(Generation::getName).toList());
    }
}
