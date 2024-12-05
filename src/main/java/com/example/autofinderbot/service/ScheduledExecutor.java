package com.example.autofinderbot.service;

import com.example.autofinderbot.TelegramBot;
import com.example.autofinderbot.domain.CarResponse;
import com.example.autofinderbot.repository.CarFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static java.util.concurrent.TimeUnit.MINUTES;
import static lombok.AccessLevel.PRIVATE;

@Profile("!test")
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduledExecutor {
    int carLimit = 30;
    CarService carService;
    CarFileRepository carFileRepository;
    TelegramBot telegramBot;
    DocumentService documentService;


    @SneakyThrows
    @Scheduled(fixedRate = 10, initialDelay = 1, timeUnit = MINUTES)
    void execute() {
        List<CarResponse> newCars = new ArrayList<>();

        int page = 1;
        while (newCars.size() <= carLimit) {
            List<CarResponse> carResponses = carService.findCars(documentService.load(SEARCH_URL(page++)));

            System.out.println("Found car responses: " + carResponses.size());
            List<CarResponse> filtered = carResponses.stream().filter(cr -> !carFileRepository.contains(cr.getUrl())).toList();

            newCars.addAll(filtered);
            System.out.println("Added filtered");

            if(filtered.size() != carResponses.size()) break;
        }

        carFileRepository.saveUrls(newCars.stream().map(CarResponse::getUrl).toList());
        telegramBot.sendAll(newCars.stream().map(carService::formatCarResponse).toList());
    }
}
