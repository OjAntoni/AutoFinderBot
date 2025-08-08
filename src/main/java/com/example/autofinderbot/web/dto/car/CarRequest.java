package com.example.autofinderbot.web.dto.car;

/**
 * Request parameters for car listing endpoint.
 */
public record CarRequest(
        String brand,
        String fuelType,
        Long mileageFrom,
        Long mileageTo,
        Double priceFrom,
        Double priceTo
) {
}
