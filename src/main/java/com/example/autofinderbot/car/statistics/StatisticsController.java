package com.example.autofinderbot.car.statistics;

import com.example.autofinderbot.car.statistics.BrandStatisticsResponse;
import com.example.autofinderbot.user.UserStatisticsResponse;
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
@RequestMapping("/api/statistics")
//TODO split up in user and car statistics controllers
public class StatisticsController {
    StatisticsService statisticsService;

    @GetMapping("/users/all")
    @SecurityRequirement(name = "bearerAuth")
    public UserStatisticsResponse getUserStatistics() {
        return statisticsService.getUserStatistics();
    }

    @GetMapping("/cars/{brand}")
    @SecurityRequirement(name = "bearerAuth")
    public BrandStatisticsResponse getBrandStatistics(@PathVariable String brand) {
        return statisticsService.getBrandStatistics(brand);
    }
}


