package com.example.autofinderbot.service;

import com.example.autofinderbot.TelegramBot;
import com.example.autofinderbot.domain.CarResponse;
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
    private static final Logger log = LoggerFactory.getLogger(ScheduledExecutor.class);
    CarService carService;
    TelegramBot telegramBot;
    DocumentService documentService;

    @SneakyThrows
    @Scheduled(fixedRate = 10, timeUnit = MINUTES)
    void execute() {
        Set<String> old = getOldUrls();
        List<CarResponse> newCars = new ArrayList<>();
        Document document = documentService.load(SEARCH_URL, 30);
        int pages = carService.getPages(document);
        System.out.println("pages: " + pages);
        for (int i = 1; i < pages; i++) {
            List<CarResponse> carResponses = carService.findCars(documentService.load(SEARCH_URL(i)));
            System.out.println("Found car responses: " + carResponses.size());
            List<CarResponse> filtered = carResponses.stream().filter(cr -> !old.contains(cr.url())).toList();
            newCars.addAll(filtered);
            System.out.println("Added filtered");
            if(!validate(carResponses.stream().map(CarResponse::url).toList(), old)) break;
        }

        saveNewUrls(newCars.stream().map(CarResponse::url).toList());
        telegramBot.sendAll(newCars.stream().map(carService::formatCarResponse).toList());
    }

    private boolean validate(List<String> urls, Set<String> old) {
        for (String url : urls) {
            if(old.contains(url)) return false;
        }
        return true;
    }

    private Set<String> getOldUrls() {

        Set<String> lines = new HashSet<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile("old")));) {

            String line = reader.readLine();

            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    private void saveNewUrls(List<String> urls) {
        Set<String> lines = new HashSet<>();

        // Specify the file path outside of the classpath
        File file = new File("old");  // Can use any file path, e.g., "/tmp/old.txt" or "/data/old.txt"
        FileWriter writer;

        try {
            writer = new FileWriter(file, true); // 'true' to append data instead of overwriting
            urls.forEach(url -> {
                try {
                    System.out.println("Writing: " + url);
                    writer.write(url + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
