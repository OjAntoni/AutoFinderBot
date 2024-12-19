package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.repository.CarDetailRepository;
import com.example.autofinderbot.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarScheduledServiceTest extends BaseSpringBootTest {
    @Autowired
    CarScheduledService carScheduledService;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarDetailRepository carDetailRepository;

    @Test
    @Transactional
    void saveAllCars_PosTC(){
        long count = carRepository.count();

        carScheduledService.updateCarDatabase();

        assertThat(carRepository.count())
                .isEqualTo(count + 30);
    }

    @Test
    @Transactional
    void deleteExpiredCars_PosTC(){
        carScheduledService.deleteExpiredCars();

        assertThat(carRepository.findAllById(List.of(4L, 5L, 6L)))
                .isEmpty();

        assertThat(carDetailRepository.findAllByCarIdIn(List.of(4L, 5L, 6L)))
                .isEmpty();
    }
}