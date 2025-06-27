package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.parser.CarDetailsResponse;
import com.example.autofinderbot.parser.CarValidator;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.DateTimeUtil;
import com.example.autofinderbot.shared.Details;
import com.example.autofinderbot.shared.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OlxCarParserService {
    private static final int THREAD_POOL_SIZE = 30;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    Logger logger;
    OlxCarDetailsExtractor carDetailsExtractor;
    CarValidator carValidator;
    DocumentService documentService;
    DateTimeUtil dateTimeUtil;

    private static final Predicate<Document> validator = doc -> {
        Element script = doc.selectFirst("script[type=application/ld+json]");
        if (script == null) {
            return false;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(script.html());
            JsonNode list = root.path("itemListElement");
            return list.isArray() && StreamSupport.stream(list.spliterator(), false)
                    .allMatch(item -> item.has("url"));
        } catch (JsonProcessingException e) {
            return false;
        }
    };

    public List<Car> findCars(String url) throws IOException {
        Document document = documentService.load(url, validator);
        Element script = document.selectFirst("script[type=application/ld+json]");
        JsonNode root = OBJECT_MAPPER.readTree(script.html());
        JsonNode items = root.path("itemListElement");
        Map<String, Car> cars = new ConcurrentHashMap<>();
        int counter = 0;
        for (JsonNode item : items) {
            JsonNode node = item.has("item") ? item.get("item") : item;
            String title = node.path("name").asText();
            String brand = node.at("/brand/name").asText();
            String fuelType = node.path("fuelType").asText();
            long mileage = node.at("/mileageFromOdometer/value").asLong();
            String unit = node.at("/mileageFromOdometer/unitText").asText();
            double price = node.at("/offers/price").asDouble();
            String currency = node.at("/offers/priceCurrency").asText();
            String itemUrl = node.path("url").asText();
            Car car = new Car(title, brand, fuelType, mileage, unit, price, currency);
            car.setUrl(itemUrl);
            cars.put(carKey(counter++, title), car);
        }

        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            List<CompletableFuture<Void>> futures = cars.entrySet().stream()
                    .map(entry -> CompletableFuture.runAsync(() -> {
                        Car car = entry.getValue();
                        CarDetailsResponse response = carDetailsExtractor.extract(car.getUrl());
                        List<CarDetail> details = response.getCarDetails();
                        extractCreationDate(details, car);
                        car.setDetails(details);
                        car.setDescription(response.getDescription());
                        car.setSeller(response.getSeller());
                    }, executor))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e);
        }

        return cars.values().stream()
                .filter(carValidator::isValid)
                .collect(Collectors.toList());
    }

    private void extractCreationDate(List<CarDetail> carDetails, Car car) {
        carDetails.stream().filter(cd -> cd.getDetail().equals(Details.CREATED_AT.name))
                .findFirst().ifPresent(cd -> {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(cd.getValue());
                    car.setCreatedAt(dateTimeUtil.convert(zonedDateTime));
                    carDetails.remove(cd);
                });
    }

    private String carKey(int i, String title) {
        return title + "|" + i;
    }
}
