package com.example.autofinderbot.web.dto.car;

import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.Seller;

import java.util.List;

public record CarResponse(
        long id,
        String title,
        String brand,
        String fuelType,
        long mileage,
        String mileageUnit,
        double price,
        String currency,
        List<CarDetail> details,
        Seller seller,
        String description
) {
}
