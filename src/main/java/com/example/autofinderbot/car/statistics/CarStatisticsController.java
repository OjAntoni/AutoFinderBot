package com.example.autofinderbot.car.statistics;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics/cars")
public class CarStatisticsController {
    CarStatisticsService carStatisticsService;

    @GetMapping("/{brand}")
    @SecurityRequirement(name = "bearerAuth")
    public BrandStatisticsResponse getBrandStatistics(@PathVariable String brand) {
        return carStatisticsService.getBrandStatistics(brand);
    }
}
