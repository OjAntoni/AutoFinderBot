package com.example.autofinderbot.service;

import com.example.autofinderbot.TelegramBot;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.parser.CarParserService;
import com.example.autofinderbot.repository.CarFileRepository;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static lombok.AccessLevel.PRIVATE;

@Profile("!test")
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduledExecutor {
    private static final int CAR_LIMIT = 30;
    CarParserService carParserService;
    CarFileRepository carFileRepository;
    TelegramBot telegramBot;
    Logger logger;

    @SneakyThrows
//    @Scheduled(fixedRate = 10, initialDelay = 1, timeUnit = MINUTES)
    void execute() {
        List<Car> newCars = new ArrayList<>();

        int page = 1;
        while (newCars.size() <= CAR_LIMIT) {
            List<Car> cars = carParserService.findCars(SEARCH_URL(page++));

            logger.debug("Found car responses: %d", cars.size());
            List<Car> filtered = cars.stream()
                    .filter(cr -> !carFileRepository.contains(cr.getUrl()))
                    .limit(newCars.size()+ cars.size() > CAR_LIMIT ? CAR_LIMIT - newCars.size() : cars.size())
                    .toList();

            newCars.addAll(filtered);
            logger.debug("Added filtered cars: %d", filtered.size());

            if(filtered.size() != cars.size()) break;
        }

        carFileRepository.saveUrls(newCars.stream().map(Car::getUrl).toList());
        telegramBot.sendAll(newCars.stream().map(this::formatCarResponse).toList());
    }

    private String formatCarResponse(Car car) {
        String details = car.getDetails().stream().map(detail -> "%s : %s".formatted(detail.getDetail(), detail.getValue()))
                .collect(Collectors.joining("\n"));
        return  "🚗 " + car.getTitle() + "\n" +
                "🛞 Kilometers: " + car.getMileage() + "\n" +
                "💵 Price: " + car.getPrice() + "\n" +
                "🔗 Link " + car.getUrl() + "\n\n" +
                details;
    }
}
