package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarServiceTest extends BaseSpringBootTest {
    @Autowired
    CarResponseService carResponseService;
    @Autowired
    CarRepository carRepository;

    @Test
    void save_PosTC() {
        Car car = Car.builder()
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

        carResponseService.save(car);

        assertThat(carRepository.findOne(Example.of(car)))
                .isNotEmpty();
    }

    @Test
    void existsByUrl_PosTC() {
        assertThat(carResponseService.exists("https://www.example.com/audi-a4"))
                .isTrue();
    }


}