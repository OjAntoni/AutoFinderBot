package com.example.autofinderbot.web.service;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.mapper.CarMapper;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.shared.Details;
import com.example.autofinderbot.web.specification.CarSpecifications;
import com.example.autofinderbot.web.dto.car.CarRequest;
import com.example.autofinderbot.web.dto.car.CarResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.autofinderbot.web.dto.car.CarResponse.PriceComparison.*;
import static com.example.autofinderbot.web.dto.car.CarResponse.PriceComparison.UNDEFINED;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WebCarService {

    static final double PRICE_TOLERANCE = 0.05;
    static final double MILEAGE_TOLERANCE = 0.10;

    CarRepository carRepository;
    CarMapper carMapper;

    @Transactional(readOnly = true)
    public Page<CarResponse> getCars(CarRequest request, Pageable pageable) {
        Specification<Car> spec = CarSpecifications.build(request);

        return carRepository.findAll(spec, pageable)
            .map(this::toResponseWithDerivedFlags);
    }

    private CarResponse toResponseWithDerivedFlags(Car car) {
        Map<String, String> details = carDetailsMap(car);

        String model = details.get(Details.MODEL.name);
        String year  = details.get(Details.YEAR.name);

        long mileage = nonNullMileage(car.getMileage());
        long minMileage = lowerBound(mileage, MILEAGE_TOLERANCE);
        long maxMileage = upperBound(mileage, MILEAGE_TOLERANCE);

        Double avgSimilar = averagePriceForSimilar(
            car.getBrand(), model, year, minMileage, maxMileage
        );

        CarResponse response = carMapper.toResponse(car);
        response.setPriceComparison(classifyPrice(car.getPrice(), avgSimilar));
        response.setDamaged(isDamaged(details));

        return response;
    }

    private CarResponse.PriceComparison classifyPrice(Double price, Double average) {
        if (price == null || average == null) {
            return UNDEFINED;
        }
        double lowerBound = average * (1.0 - PRICE_TOLERANCE);
        double upperBound = average * (1.0 + PRICE_TOLERANCE);

        if (price < lowerBound) return LOWER;
        if (price > upperBound) return HIGHER;
        return MEDIUM;
    }

    private Double averagePriceForSimilar(String brand, String model, String year, long minMileage, long maxMileage) {
        if (brand == null || model == null || year == null) {
            return null;
        }
        return carRepository.avgPriceForSimilar(brand, model, year, minMileage, maxMileage);
    }

    private long nonNullMileage(Long mileage) {
        return mileage != null ? mileage : 0L;
    }

    private long lowerBound(long base, double pct) {
        long v = Math.round(base * (1.0 - pct));
        return Math.max(0L, v);
    }

    private long upperBound(long base, double pct) {
        return Math.round(base * (1.0 + pct));
    }

    private Map<String, String> carDetailsMap(Car car) {
        if (car.getDetails() == null) return Collections.emptyMap();
        return car.getDetails().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                CarDetail::getDetail,
                CarDetail::getValue
            ));
    }

    private boolean isDamaged(Map<String, String> details) {
        String damagedValue = details.get(Details.DAMAGED.name);
        return !"Nie".equalsIgnoreCase(damagedValue);
    }
}
