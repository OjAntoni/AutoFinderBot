package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.domain.Address;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.Seller;
import com.example.autofinderbot.parser.CarDetailsResponse;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.Details;
import com.example.autofinderbot.shared.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OlxCarDetailsExtractor {
    Logger logger;
    DocumentService documentService;
    ObjectMapper objectMapper = new ObjectMapper();

    public CarDetailsResponse extract(String url) {
        Document document;
        try {
            document = documentService.load(url, doc -> !doc.select("script[type=application/ld+json]").isEmpty());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new CarDetailsResponse(emptyList(), null, "");
        }

        Element scriptElement = null;
        for (Element sc : document.select("script[type=application/ld+json]")) {
            if (sc.html() != null && !sc.html().isBlank()) {
                scriptElement = sc;
                break;
            }
        }
        if (scriptElement == null) {
            logger.error("Script element with JSON data not found.");
            return new CarDetailsResponse(emptyList(), null, "");
        }

        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(scriptElement.html());
        } catch (JsonProcessingException e) {
            logger.error(e);
            return new CarDetailsResponse(emptyList(), null, "");
        }

        List<CarDetail> carDetails = extractCarProperties(rootNode);
        Seller seller = extractSeller(rootNode);
        String description = Optional.ofNullable(rootNode.path("description").asText(null)).orElse("");
        return new CarDetailsResponse(carDetails, seller, description);
    }

    private List<CarDetail> extractCarProperties(JsonNode rootNode) {
        Map<String, String> properties = new HashMap<>();
        collectFields(rootNode, properties);
        return convert(properties);
    }

    private void collectFields(JsonNode node, Map<String, String> properties) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                JsonNode value = entry.getValue();
                if (value.isValueNode()) {
                    properties.putIfAbsent(entry.getKey(), value.asText());
                } else {
                    collectFields(value, properties);
                }
            });
        } else if (node.isArray()) {
            node.forEach(child -> collectFields(child, properties));
        }
    }

    private List<CarDetail> convert(Map<String, String> specification) {
        Map<String, Details> attributeToDetails = Arrays.stream(Details.values()).collect(Collectors.toMap(Details::getAttribute, d -> d));
        Set<String> attributes = attributeToDetails.keySet();
        return specification.entrySet().stream()
                .filter(entry -> attributes.contains(entry.getKey()))
                .map(entry -> new CarDetail(attributeToDetails.get(entry.getKey()).name, entry.getValue()))
                .collect(Collectors.toList());
    }

    private Seller extractSeller(JsonNode rootNode) {
        JsonNode sellerNode = rootNode.path("seller");
        if (sellerNode.isMissingNode()) {
            return null;
        }
        Seller.SellerType type = Seller.SellerType.PRIVATE;
        if (sellerNode.has("type")) {
            try {
                type = Seller.SellerType.valueOf(sellerNode.get("type").asText());
            } catch (IllegalArgumentException ignored) {}
        }
        Address address = Address.builder()
                .city(Optional.ofNullable(sellerNode.path("address").path("addressLocality").asText(null)).orElse(null))
                .region(Optional.ofNullable(sellerNode.path("address").path("addressRegion").asText(null)).orElse(null))
                .build();
        return Seller.builder()
                .type(type)
                .name(Optional.ofNullable(sellerNode.path("name").asText(null)).orElse(null))
                .address(address)
                .build();
    }
}
