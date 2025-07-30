package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.parser.service.DocumentService;
import com.example.autofinderbot.parser.service.ScraperService;
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
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.autofinderbot.shared.APIConstants.*;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Service
@Order(HIGHEST_PRECEDENCE)
@Qualifier("otomotoScraper")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OtomotoScraperService implements ScraperService<Car> {
    private static final int THREAD_POOL_SIZE = 30;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    Logger logger;
    CarDetailsExtractor carDetailsExtractor;
    OtomotoCarValidator otomotoCarValidator;
    DocumentService<Document> documentService;
    DateTimeUtil dateTimeUtil;

    private static final Predicate<Document> validator = (doc) -> {
        try {
            Element scriptElement = doc.selectFirst(LISTING_JSON);
            if (scriptElement == null || scriptElement.html().isBlank()) {
                return false;
            }

            String jsonData = scriptElement.html();
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonData);

            JsonNode itemList = rootNode.at(ITEM_CAR_LIST_ELEMENT);
            if (itemList.isMissingNode() || !itemList.isArray()) {
                return false;
            }

            return StreamSupport.stream(itemList.spliterator(), false)
                .allMatch(item -> {
                    JsonNode carInfo = item.at(CAR_INFO);
                    if (carInfo.isMissingNode() || carInfo.isEmpty()) {
                        return false;
                    }
                    return isNonEmptyText(carInfo.path(NAME)) &&
                        isNonEmptyText(carInfo.path(BRAND)) &&
                        isNonEmptyText(carInfo.path(FUEL_TYPE)) &&
                        isNonEmptyText(carInfo.at(MILEAGE_TYPE));
                });

        } catch (JsonProcessingException e) {
            return false;
        }
    };

    private static boolean isNonEmptyText(JsonNode node) {
        return node != null && !node.isNull() && !node.asText().isBlank();
    }

    public List<Car> scrape(String url) throws IOException {
        Document document = documentService.load(url, validator);

        Element scriptElement = document.selectFirst(LISTING_JSON);

        String jsonData = Objects.requireNonNull(scriptElement).html();

        JsonNode rootNode = OBJECT_MAPPER.readTree(jsonData);

        JsonNode itemList = rootNode.at(ITEM_CAR_LIST_ELEMENT);

        Map<String, Car> carNameToCars = new ConcurrentHashMap<>();

        int counter = 0;
        for (JsonNode item : itemList) {
            JsonNode carInfo = carInfo(item);
            JsonNode priceInfo = priceInfo(item);

            Car car = convert(carInfo, priceInfo);

            String carKey = carKey(counter++, car.getTitle());
            carNameToCars.put(carKey, car);
        }

        Elements links = document.select(LINKS);
        counter = 0;
        for (Element link : links) {
            String text = link.text().trim();
            String carKey = carKey(counter, text);
            if (carNameToCars.containsKey(carKey)) {
                String carUrl = link.attr(LINK_URL);
                carNameToCars.get(carKey).setUrl(carUrl);
                counter++;
            }
        }

        Map<String, String> carNamesToUrls = carNameToCars.entrySet().stream()
            .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue().getUrl() != null)
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getUrl()));

        try(ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            List<CompletableFuture<Void>> futures = carNamesToUrls.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() -> {
                    String carKey = entry.getKey();
                    String carUrl = entry.getValue();

                    CarDetailsResponse response = carDetailsExtractor.extract(carUrl);
                    List<CarDetail> carDetails = response.getCarDetails();

                    extractCreationDate(carDetails, carNameToCars.get(carKey));
                    carNameToCars.get(carKey).setDetails(carDetails);
                    carNameToCars.get(carKey).setDescription(response.getDescription());
                    carNameToCars.get(carKey).setSeller(response.getSeller());
                }, executor))
                .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e);
        }

        return carNameToCars.values().stream()
            .filter(otomotoCarValidator::isValid)
            .collect(Collectors.toList());
    }

    @Override
    public String getSearchUrl(int page) {
        return SEARCH_URL(page);
    }

    private String carKey(int i, String title) {
        return title + "|" + i;
    }

    private JsonNode carInfo(JsonNode node) {
        return node.at(CAR_INFO);
    }

    private JsonNode priceInfo(JsonNode node) {
        return node.at(PRICE_INFO);
    }

    private Car convert(JsonNode carInfo, JsonNode priceInfo) {
        String name = carInfo.path(NAME).asText();
        String brand = carInfo.path(BRAND).asText();
        String fuelType = carInfo.path(FUEL_TYPE).asText();
        int mileage = carInfo.at(MILEAGE).asInt();
        String unit = carInfo.at(MILEAGE_TYPE).asText();
        double price = priceInfo.path(PRICE).asDouble();
        String currency = priceInfo.path(CURRENCY).asText();

        return new Car(name, brand, fuelType, mileage, unit, price, currency, Car.Source.OTOMOTO);
    }

    private void extractCreationDate(List<CarDetail> carDetails, Car car) {
        carDetails.stream().filter(cd -> cd.getDetail().equals(Details.CREATED_AT.name))
            .findFirst().ifPresent(cd -> {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(cd.getValue());
                car.setCreatedAt(dateTimeUtil.convert(zonedDateTime));
                carDetails.remove(cd);
            });
    }
}