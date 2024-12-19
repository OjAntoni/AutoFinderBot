package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.shared.Logger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
public class CarResponseService {
    CarRepository carRepository;
    Logger logger;

    public Car save(@Valid Car car) {
        logger.debug("Saving car: %s", car);
        return carRepository.save(car);
    }

    public boolean exists(String url) {
        return carRepository.existsByUrl(url);
    }
}
