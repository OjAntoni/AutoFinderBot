package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.Address;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.Seller;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.Details;
import com.example.autofinderbot.shared.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.autofinderbot.shared.APIConstants.*;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
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
    ObjectMapper objectMapper;

    public CarDetailsResponse extract(String url) {
        Document document;
        try {
            document = documentService.load(url, (doc -> doc.selectFirst(CAR_PAGE_JSON_DATA) != null));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new CarDetailsResponse(emptyList(), null, "");
        }
        Element scriptElement = document.selectFirst(CAR_PAGE_JSON_DATA);
        if (scriptElement == null) {
            IllegalArgumentException exception = new IllegalArgumentException(SCRIPT_ERROR_MESSAGE);
            logger.error(SCRIPT_ERROR_MESSAGE, exception);
            throw exception;
        }

        String jsonData = scriptElement.html();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(jsonData);
        } catch (JsonProcessingException e) {
            logger.error(e);
            return new CarDetailsResponse(emptyList(), null, "");
        }

        JsonNode advertNode = rootNode.at(CAR_PAGE_ADVERT);

        List<CarDetail> carDetails = extractCarProperties(advertNode);
        Seller seller = extractSeller(advertNode);
        String description = extractDescription(advertNode);
        return new CarDetailsResponse(carDetails, seller, description);
    }

    private List<CarDetail> extractCarProperties(JsonNode advertNode) {
        Map<String, String> carProperties = new HashMap<>();

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

        JsonNode extraParameters = advertNode.at("/parametersDict");
        Iterator<Map.Entry<String, JsonNode>> fields = extraParameters.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode currentNode = entry.getValue();

            String label = currentNode.get("label").asText();
            if(carProperties.containsKey(label)) {
                continue;
            }
            JsonNode valuesNode = currentNode.get("values");
            if (valuesNode != null && valuesNode.isArray() && valuesNode.size() == 1) {
                String value = valuesNode.get(0).get("label").asText();
                carProperties.put(label, value);
            }
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

    private Seller extractSeller(JsonNode advertNode) {
        JsonNode sellerNode = advertNode.get("seller");
        JsonNode location = sellerNode.get("location");

        return Seller.builder()
                .type(Seller.SellerType.valueOf(sellerNode.get("type").asText()))
                .name(sellerNode.get("name").asText())
                .address(
                    Address.builder()
                        .address(ofNullable(location.get("address")).map(JsonNode::asText).orElse(null))
                        .city(ofNullable(location.get("city")).map(JsonNode::asText).orElse(null))
                        .cityId(ofNullable(location.get("cityId")).map(JsonNode::asLong).orElse(null))
                        .region(ofNullable(location.get("region")).map(JsonNode::asText).orElse(null))
                        .regionId(ofNullable(location.get("regionId")).map(JsonNode::asLong).orElse(null))
                        .shortAddress(ofNullable(location.get("shortAddress")).map(JsonNode::asText).orElse(null))
                        .latitude(ofNullable(location.at("/map/latitude")).map(JsonNode::asDouble).orElse(null))
                        .longitude(ofNullable(location.at("/map/longitude")).map(JsonNode::asDouble).orElse(null))
                        .build()
                ).build();
    }


    private String extractDescription(JsonNode advertNode) {
        String descriptionHtml = advertNode.get("description").asText();
        Document doc = Jsoup.parse("<body>" + descriptionHtml + "</body>");
        return buildStringFromNode(doc.childNode(0)).toString();
    }

    private static StringBuffer buildStringFromNode(Node node) {
        StringBuffer buffer = new StringBuffer();

        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            buffer.append(textNode.text().trim());
        }

        for (Node childNode : node.childNodes()) {
            buffer.append(buildStringFromNode(childNode));
        }

        if (node instanceof Element) {
            Element element = (Element) node;
            String tagName = element.tagName();
            if ("p".equals(tagName) || "br".equals(tagName)) {
                buffer.append("\n");
            }
        }

        return buffer;
    }
}

