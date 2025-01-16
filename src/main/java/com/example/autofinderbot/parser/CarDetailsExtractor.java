package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.Details;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.autofinderbot.shared.APIConstants.*;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class CarDetailsExtractor {
    private static final String SCRIPT_ERROR_MESSAGE = "Script element with JSON data not found.";
    private static final String AVERT_ERROR_MESSAGE = "Advert data not found in JSON.";
    Logger logger;
    DocumentService documentService;

    public List<CarDetail> extractCarProperties(String url) {
        Document document;
        try {
            document = documentService.load(url, (doc -> doc.selectFirst(CAR_PAGE_JSON_DATA) != null));
        } catch (Throwable e) {
            logger.error(e.getMessage());
            return emptyList();
        }

        Map<String, String> carProperties = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Element scriptElement = document.selectFirst(CAR_PAGE_JSON_DATA);
            if (scriptElement == null) {
                IllegalArgumentException exception = new IllegalArgumentException(SCRIPT_ERROR_MESSAGE);
                logger.error(SCRIPT_ERROR_MESSAGE, exception);
                throw exception;
            }

            String jsonData = scriptElement.html();
            JsonNode rootNode = objectMapper.readTree(jsonData);

            JsonNode advertNode = rootNode.at(CAR_PAGE_ADVERT);

            if (advertNode.isMissingNode()) {
                IllegalArgumentException exception = new IllegalArgumentException(AVERT_ERROR_MESSAGE);
                logger.error(AVERT_ERROR_MESSAGE, exception);
                return emptyList();
            }

            // Extract equipment -> values
            JsonNode equipmentNode = advertNode.path(CAR_PAGE_ADVERT_EQUIPMENT);
            if (equipmentNode.isArray()) {
                for (JsonNode category : equipmentNode) {
                    String categoryKey = category.path(KEY).asText();
                    JsonNode values = category.path(VALUES);

                    for (JsonNode value : values) {
                        String valueKey = value.path(KEY).asText();
                        String valueLabel = value.path(LABEL).asText();
                        carProperties.put(categoryKey + "." + valueKey, valueLabel);
                    }
                }
            }

            // Extract details -> values and keys
            JsonNode detailsNode = advertNode.path(CAR_PAGE_ADVERT_DETAILS);
            if (detailsNode.isArray()) {
                for (JsonNode detail : detailsNode) {
                    String key = detail.path(KEY).asText();
                    String value = detail.path(VALUE).asText();
                    carProperties.put(key, value);
                }
            }

            // Extract creation date
            String creationDate = advertNode.path(CAR_PAGE_ADVERT_CREATED_AT).asText();
            if (!creationDate.isEmpty()) {
                carProperties.put(CAR_PAGE_ADVERT_CREATED_AT, creationDate);
            }

        } catch (IOException e) {
            logger.error(e);
        }

        return convert(carProperties);
    }

    private List<CarDetail> convert(Map<String, String> specification) {
        Map<String, Details> attributeToDetails = Arrays.stream(Details.values()).collect(Collectors.toMap(Details::getAttribute, identity()));
        Set<String> attributes = Arrays.stream(Details.values()).map(Details::getAttribute).collect(Collectors.toSet());
        return specification.entrySet().stream()
                .filter(entry -> attributes.contains(entry.getKey()))
                .map(entry -> new CarDetail(attributeToDetails.get(entry.getKey()).name, entry.getValue()))
                .collect(Collectors.toList());
    }
}

