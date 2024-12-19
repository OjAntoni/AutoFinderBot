package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.Car;
import org.springframework.stereotype.Component;

@Component
class CarValidator {
    public boolean isValid(Car car) {
        return car.getUrl() != null && car.getDetails() != null && !car.getDetails().isEmpty();
    }
}
