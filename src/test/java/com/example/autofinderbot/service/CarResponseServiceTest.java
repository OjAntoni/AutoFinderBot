package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.CarResponse;
import com.example.autofinderbot.repository.CarResponseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarResponseServiceTest extends BaseSpringBootTest {
    @Autowired
    CarResponseService carResponseService;
    @Autowired
    CarResponseRepository carResponseRepository;

    @Test
    void save_PosTC() {
        CarResponse carResponse = CarResponse.builder()
                .url("https://example.com")
                .brand("brand")
                .price(1000)
                .title("title")
                .mileage(100500)
                .currency("currency")
                .fuelType("fuelType")
                .createdAt(LocalDateTime.now())
                .details(List.of(new CarDetail("key", "value")))
                .build();

        carResponseService.save(carResponse);

        assertThat(carResponseRepository.findOne(Example.of(carResponse)))
                .isNotEmpty();
    }

    @Test
    void existsByUrl_PosTC() {
        assertThat(carResponseService.exists("https://www.example.com/audi-a4"))
                .isTrue();
    }


}