package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.repository.CarRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CarScheduledServiceTest extends BaseSpringBootTest {
    @Autowired
    CarScheduledService carScheduledService;

    @Autowired
    CarRepository carRepository;

    @Test
    void saveAllCars(){
        long count = carRepository.count();

        carScheduledService.execute();

        assertThat(carRepository.count())
                .isEqualTo(count + 30);
    }
}