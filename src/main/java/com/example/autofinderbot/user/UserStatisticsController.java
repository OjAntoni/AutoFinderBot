package com.example.autofinderbot.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics/users")
public class UserStatisticsController {
    UserStatisticsService userStatisticsService;

    @GetMapping("/all")
    @SecurityRequirement(name = "bearerAuth")
    public UserStatisticsResponse getUserStatistics() {
        return userStatisticsService.getUserStatistics();
    }
}
