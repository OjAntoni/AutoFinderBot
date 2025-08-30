package com.example.autofinderbot.car;

/**
 * Request parameters for car listing endpoint.
 */
public record CarRequest(
        String brand,
        String model,
        String generation,
        String fuelType,
        Long mileageFrom,
        Long mileageTo,
        Double priceFrom,
        Double priceTo
) {
}
