package com.example.autofinderbot.web.service;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.mapper.CarMapper;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.specification.CarSpecifications;
import com.example.autofinderbot.web.dto.car.CarRequest;
import com.example.autofinderbot.web.dto.car.CarResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WebCarService {
    CarRepository carRepository;
    CarMapper carMapper;

    public Page<CarResponse> getCars(CarRequest request, Pageable pageable) {
        Specification<Car> spec = CarSpecifications.build(request);
        return carRepository.findAll(spec, pageable)
                .map(carMapper::toResponse);
    }
}
