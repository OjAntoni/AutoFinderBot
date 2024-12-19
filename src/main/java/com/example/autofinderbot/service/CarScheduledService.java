package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.parser.CarParserService;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarScheduledService {
    private static final int CAR_LIMIT = 30;
    CarParserService carParserService;
    CarService carService;
    Logger logger;

    @Scheduled(fixedRate = 10, initialDelay = 0, timeUnit = SECONDS)
    void execute() {
        List<Car> newCars = new ArrayList<>();
        int page = 1;
        while (newCars.size() <= CAR_LIMIT) {

            List<Car> cars;

            try {
                cars = carParserService.findCars(SEARCH_URL(page++));
            } catch (IOException e) {
                logger.error(e);
                break;
            }

            logger.debug("Found car responses: %d", cars.size());
            List<Car> filtered = cars.stream()
                    .filter(cr -> !carService.exists(cr.getUrl()))
                    .limit(newCars.size()+ cars.size() > CAR_LIMIT ? CAR_LIMIT - newCars.size() : cars.size())
                    .toList();

            newCars.addAll(filtered);
            logger.debug("Added filtered cars: %d", filtered.size());

            if(filtered.size() != cars.size()) break;
        }

        carService.saveAll(newCars);
    }
}
