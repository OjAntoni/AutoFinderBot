package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarResponse;
import org.springframework.stereotype.Component;

@Component
public class CarResponseValidator {
    public boolean isValid(CarResponse carResponse) {
        return carResponse.getUrl() != null && carResponse.getDetails() != null && !carResponse.getDetails().isEmpty();
    }
}
