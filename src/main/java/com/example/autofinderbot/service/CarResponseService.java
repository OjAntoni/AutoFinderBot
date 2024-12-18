package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.CarResponse;
import com.example.autofinderbot.repository.CarResponseRepository;
import com.example.autofinderbot.shared.Logger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
public class CarResponseService {
    CarResponseRepository carResponseRepository;
    Logger logger;

    public CarResponse save(@Valid CarResponse carResponse) {
        logger.debug("Saving car response: %s", carResponse);
        return carResponseRepository.save(carResponse);
    }
}
