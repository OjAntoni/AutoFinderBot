package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.Details;
import com.example.autofinderbot.shared.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.autofinderbot.shared.APIConstants.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Service
public class CarParserService {
    private static final String SCRIPT_ERROR_MESSAGE = "Script element with JSON data not found.";
    private static final String NOT_AN_ARRAY_ERROR_MESSAGE = "Element is not an array.";
    Logger logger;
    ObjectMapper objectMapper;
    CarDetailsExtractor carDetailsExtractor;
    CarValidator carValidator;
    DocumentService documentService;

    public List<Car> findCars(String url) throws IOException {
        Document document = documentService.load(url, doc -> doc.selectFirst(LISTING_JSON) != null);
        Element scriptElement = document.selectFirst(LISTING_JSON);
        if (scriptElement == null) {
            logger.error(SCRIPT_ERROR_MESSAGE);
            return List.of();
        }

        String jsonData = scriptElement.html();

        JsonNode rootNode = objectMapper.readTree(jsonData);

        JsonNode itemList = rootNode.at(ITEM_CAR_LIST_ELEMENT);

        List<Car> cars = new ArrayList<>();
        Map<String, Car> carNamesToCarResponses = new HashMap<>(); // Map to store car names and URLs

        if (itemList.isArray()) {
            for (JsonNode item : itemList) {
                JsonNode carInfo = carInfo(item);
                JsonNode priceInfo = priceInfo(item);

                Car car = convert(carInfo, priceInfo);

                cars.add(car);
                carNamesToCarResponses.put(car.getTitle(), car);
            }

            Elements links = document.select(LINKS);
            for (Element link : links) {
                String text = link.text().trim();
                if (carNamesToCarResponses.containsKey(text)) {
                    String carUrl = link.attr(LINK_URL);
                    carNamesToCarResponses.get(text).setUrl(carUrl);
                }
            }
        } else {
            logger.error(NOT_AN_ARRAY_ERROR_MESSAGE);
        }

        Map<String, String> carNamesToUrls = carNamesToCarResponses.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getUrl()));

        carNamesToUrls.forEach((carName, carUrl) -> {
            List<CarDetail> carDetails  = carDetailsExtractor.extractCarProperties(carUrl);
            extractCreationDate(carDetails, carNamesToCarResponses.get(carName));
            carNamesToCarResponses.get(carName).setDetails(carDetails);
        });

        return cars.stream()
                .filter(carValidator::isValid)
                .collect(Collectors.toList());
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

        return new Car(name, brand, fuelType, mileage, unit, price, currency);
    }

    private void extractCreationDate(List<CarDetail> carDetails, Car car) {
        carDetails.stream().filter(cd -> cd.getDetail().equals(Details.CREATED_AT.name))
                .findFirst().ifPresent(cd -> {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(cd.getValue());
                    LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
                    car.setCreatedAt(localDateTime);
                    carDetails.remove(cd);
                });
    }
}
