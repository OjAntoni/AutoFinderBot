package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.CarResponse;
import com.example.autofinderbot.service.DocumentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    ObjectMapper objectMapper;
    CarPropertiesToCarDetailsConverter carPropertiesToCarDetailsConverter;
    CarPropertiesExtractor carPropertiesExtractor;
    DocumentService documentService;

    public List<CarResponse> findCars(Document document) throws IOException {
        Element scriptElement = document.selectFirst(LISTING_JSON);
        if (scriptElement == null) {
            throw new IOException("No element found.");
        }

        String jsonData = scriptElement.html();

        JsonNode rootNode = objectMapper.readTree(jsonData);

        JsonNode itemList = rootNode.at(ITEM_CAR_LIST_ELEMENT);

        List<CarResponse> cars = new ArrayList<>();
        Map<String, CarResponse> carNamesToCarResponses = new HashMap<>(); // Map to store car names and URLs

        if (itemList.isArray()) {
            for (JsonNode item : itemList) {
                JsonNode carInfo = carInfo(item);
                JsonNode priceInfo = priceInfo(item);

                CarResponse carResponse = convert(carInfo, priceInfo);

                cars.add(carResponse);
                carNamesToCarResponses.put(carResponse.getTitle(), carResponse);
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
            throw new IOException("Element is not an array.");
        }

        Map<String, String> carNamesToUrls = carNamesToCarResponses.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getUrl()));

        carNamesToUrls.forEach((carName, url) -> {
            try {
                Document carDocument = documentService.load(url);
                Map<String, String> carProperties = carPropertiesExtractor.extractCarProperties(carDocument);
                List<CarDetail> carDetails = carPropertiesToCarDetailsConverter.convert(carProperties);
                carNamesToCarResponses.get(carName).setDetails(carDetails);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return cars;
    }


    private JsonNode carInfo(JsonNode node) {
        return node.at(CAR_INFO);
    }

    private JsonNode priceInfo(JsonNode node) {
        return node.at(PRICE_INFO);
    }

    private CarResponse convert(JsonNode carInfo, JsonNode priceInfo) {
        String name = carInfo.path(NAME).asText();
        String brand = carInfo.path(BRAND).asText();
        String fuelType = carInfo.path(FUEL_TYPE).asText();
        int mileage = carInfo.at(MILEAGE).asInt();
        String unit = carInfo.at(MILEAGE_TYPE).asText();
        double price = priceInfo.path(PRICE).asDouble();
        String currency = priceInfo.path(CURRENCY).asText();

        return new CarResponse(name, brand, fuelType, mileage, unit, price, currency);
    }

    public String formatCarResponse(CarResponse car) {
        return  "🚗 " + car.getTitle() + "\n" +
                "🛞 Kilometers: " + car.getMileage() + "\n" +
                "💵 Price: " + car.getPrice() + "\n" +
                "🔗 Link " + car.getUrl();
    }
}
