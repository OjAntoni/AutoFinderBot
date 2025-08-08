package com.example.autofinderbot.web.specification;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.web.dto.car.CarRequest;
import org.springframework.data.jpa.domain.Specification;

public final class CarSpecifications {
    private CarSpecifications() {}

    public static Specification<Car> build(CarRequest request) {
        return new SpecificationBuilder<Car>()
                .with(request.brand(), b -> (root, query, cb) -> cb.equal(root.get("brand"), b))
                .with(request.fuelType(), f -> (root, query, cb) -> cb.equal(root.get("fuelType"), f))
                .with(request.mileageFrom(), m -> (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("mileage"), m))
                .with(request.mileageTo(), m -> (root, query, cb) -> cb.lessThanOrEqualTo(root.get("mileage"), m))
                .with(request.priceFrom(), p -> (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), p))
                .with(request.priceTo(), p -> (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), p))
                .build();
    }
}
