package com.example.autofinderbot.web.specification;

import com.example.autofinderbot.common.util.Details;
import com.example.autofinderbot.car.Car;
import com.example.autofinderbot.car.detail.CarDetail;
import com.example.autofinderbot.web.dto.car.CarRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public final class CarSpecifications {
    private CarSpecifications() {}

    public static Specification<Car> build(CarRequest request) {
        return new SpecificationBuilder<Car>()
                .with(request.brand(), CarSpecifications::hasBrand)
                .with(request.model(), CarSpecifications::hasModel)
                .with(request.generation(), CarSpecifications::hasGeneration)
                .with(request.fuelType(), f -> (root, query, cb) -> cb.equal(root.get("fuelType"), f))
                .with(request.mileageFrom(), m -> (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("mileage"), m))
                .with(request.mileageTo(), m -> (root, query, cb) -> cb.lessThanOrEqualTo(root.get("mileage"), m))
                .with(request.priceFrom(), p -> (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), p))
                .with(request.priceTo(), p -> (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), p))
                .build();
    }

    public static Specification<Car> hasBrand(String brand) {
        return (root, query, cb) -> cb.equal(root.get("brand"), brand);
    }

    public static Specification<Car> hasModel(String modelName) {
        return (root, query, cb) -> {
            Objects.requireNonNull(query).distinct(true); // avoid duplicates after joins
            Join<Car, CarDetail> d = root.join("details", JoinType.INNER);
            return cb.and(
                cb.equal(d.get("detail"), Details.MODEL.name),
                cb.equal(d.get("value"), modelName)
            );
        };
    }

    public static Specification<Car> hasGeneration(String generationName) {
        return (root, query, cb) -> {
            Objects.requireNonNull(query).distinct(true);
            Join<Car, CarDetail> d = root.join("details", JoinType.INNER);
            return cb.and(
                cb.equal(d.get("detail"), Details.GENERATION.name),
                cb.equal(d.get("value"), generationName)
            );
        };
    }
}
