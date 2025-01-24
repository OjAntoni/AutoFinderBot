package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.repository.CarDetailRepository;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.shared.DateTimeUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
public class CarService {
    CarRepository carRepository;
    CarDetailRepository carDetailRepository;
    DateTimeUtil dateTimeUtil;
    @NonFinal
    @Value("${synchronization.cars.expired-after.days:14}")
    int intervalDays;

    @Transactional
    public List<Car> saveAll(@NotNull Collection<@Valid Car> cars) {
        List<Car> savedCars = carRepository.saveAll(cars);
        List<CarDetail> carDetails = savedCars.stream()
                .peek(car -> car.getDetails().forEach(cd -> cd.setCarId(car.getId())))
                .flatMap(car -> car.getDetails().stream())
                .toList();
        carDetailRepository.saveAll(carDetails);

        return savedCars;
    }

    @Transactional(readOnly = true)
    public boolean exists(@NotNull String url) {
        return carRepository.existsByUrl(url);
    }

    @Transactional(readOnly = true)
    public List<Car> findExpired() {
        LocalDateTime daysAgo = dateTimeUtil.now().minusDays(intervalDays);
        return carRepository.findAllByCreatedAtBefore(daysAgo);
    }

    @Transactional
    public void deleteAll(Collection<Long> ids){
        carRepository.deleteAllById(ids);
    }
}
