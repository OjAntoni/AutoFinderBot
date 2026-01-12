package com.example.autofinderbot.car.core;

import com.example.autofinderbot.car.detail.CarDetail;
import com.example.autofinderbot.car.statistics.SimilarCarPricesResponse;
import com.example.autofinderbot.common.util.Details;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.autofinderbot.car.core.CarResponse.PriceComparison.*;
import static com.example.autofinderbot.car.core.CarResponse.PriceComparison.UNDEFINED;
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

        Page<Car> cars = carRepository.findAll(spec, pageable);
        Map<Long, Double> averages = averagePricesFor(cars.getContent());
        return cars.map(c -> toResponseWithDerivedFlags(c, averages.get(c.getId())));
    }

    @Transactional(readOnly = true)
    public CarResponse getCar(long id) {
        return carRepository.findById(id)
            .map(car -> {
                Map<Long, Double> avg = averagePricesFor(List.of(car));
                return toResponseWithDerivedFlags(car, avg.get(car.getId()));
            })
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public SimilarCarPricesResponse getSimilarCarPrices(long carId) {
        Car car = carRepository.findById(carId).orElse(null);
        if (car == null) {
            return new SimilarCarPricesResponse(Collections.emptyList(), 0, 0);
        }

        String model = detailValueIgnoreCase(car, Details.MODEL.name);
        String year = detailValueIgnoreCase(car, Details.YEAR.name);

        String brand = car.getBrand();
        long mileage = nonNullMileage(car.getMileage());
        long minMileage = lowerBound(mileage, MILEAGE_TOLERANCE);
        long maxMileage = upperBound(mileage, MILEAGE_TOLERANCE);

        if (brand == null || model == null || year == null) {
            return new SimilarCarPricesResponse(Collections.emptyList(), 0, 0);
        }

        List<Car> similar = carRepository.findSimilarCars(
            brand, model, year, minMileage, maxMileage, carId
        );

        List<SimilarCarPriceResponse> responses = similar.stream()
            .map(c -> new SimilarCarPriceResponse(c.getId(), c.getUrl(), c.getPrice(), c.getThumbnailUrl()))
            .toList();

        double minPrice = responses.stream().mapToDouble(SimilarCarPriceResponse::getPrice).min().orElse(0);
        double maxPrice = responses.stream().mapToDouble(SimilarCarPriceResponse::getPrice).max().orElse(0);

        return new SimilarCarPricesResponse(responses, minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    protected CarResponse toResponseWithDerivedFlags(Car car, Double avgSimilar) {
        Map<String, String> details = carDetailsMap(car);

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

    private String detailValueIgnoreCase(Car car, String key) {
        if (car.getDetails() == null) {
            return null;
        }
        return car.getDetails().stream()
            .filter(Objects::nonNull)
            .filter(cd -> key.equalsIgnoreCase(cd.getDetail()))
            .map(CarDetail::getValue)
            .findFirst()
            .orElse(null);
    }

    private boolean isDamaged(Map<String, String> details) {
        String damagedValue = details.get(Details.DAMAGED.name);
        return !"Nie".equalsIgnoreCase(damagedValue);
    }

    private Map<Long, Double> averagePricesFor(List<Car> cars) {
        if (cars.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = cars.stream().map(Car::getId).toList();
        List<CarAveragePrice> averages = carRepository.avgPriceForSimilarBulk(ids, MILEAGE_TOLERANCE);

        return averages.stream()
            .filter(ap -> ap.getAvgPrice() != null)
            .collect(Collectors.toMap(
                CarAveragePrice::getId,
                CarAveragePrice::getAvgPrice
            ));
    }
}


