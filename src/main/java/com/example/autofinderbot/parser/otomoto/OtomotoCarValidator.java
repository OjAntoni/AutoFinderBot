package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.car.Car;
import org.springframework.stereotype.Component;

@Component
public class OtomotoCarValidator {
    public boolean isValid(Car car) {
        return car.getUrl() != null && car.getDetails() != null && !car.getDetails().isEmpty();
    }
}
