package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.shared.Logger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
public class CarService {
    CarRepository carRepository;
    Logger logger;

    @Transactional
    public Car save(@Valid Car car) {
        logger.debug("Saving car: %s", car);
        return carRepository.save(car);
    }

    @Transactional
    public List<Car> saveAll(Collection<Car> cars) {
        logger.debug("Saving all %d cars", cars.size());
        return carRepository.saveAll(cars);
    }

    @Transactional(readOnly = true)
    public boolean exists(String url) {
        return carRepository.existsByUrl(url);
    }
}
