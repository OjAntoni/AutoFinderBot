package com.example.autofinderbot.service;

import com.example.autofinderbot.TelegramBot;
import com.example.autofinderbot.domain.CarResponse;
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

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static java.util.concurrent.TimeUnit.MINUTES;
import static lombok.AccessLevel.PRIVATE;

@Profile("!test")
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduledExecutor {
    int carLimit = 30;
    CarParserService carParserService;
    CarFileRepository carFileRepository;
    TelegramBot telegramBot;
    Logger logger;

    @SneakyThrows
    @Scheduled(fixedRate = 10, initialDelay = 1, timeUnit = MINUTES)
    void execute() {
        List<CarResponse> newCars = new ArrayList<>();

        int page = 1;
        while (newCars.size() <= carLimit) {
            List<CarResponse> carResponses = carParserService.findCars(SEARCH_URL(page++));

            logger.debug("Found car responses: %d", carResponses.size());
            List<CarResponse> filtered = carResponses.stream().filter(cr -> !carFileRepository.contains(cr.getUrl())).toList();

            newCars.addAll(filtered);
            logger.debug("Added filtered cars: %d", filtered.size());

            if(filtered.size() != carResponses.size()) break;
        }

        carFileRepository.saveUrls(newCars.stream().map(CarResponse::getUrl).toList());
        telegramBot.sendAll(newCars.stream().map(carParserService::formatCarResponse).toList());
    }
}
