package com.example.autofinderbot.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CarPropertiesExtractor {

    /**
     * Extracts car properties from the script element in the HTML document.
     *
     * @param document Jsoup Document object containing the HTML page.
     * @return HashMap with the extracted properties as key-value pairs.
     */
    public Map<String, String> extractCarProperties(Document document) {
        Map<String, String> carProperties = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Select the script element with JSON data
            Element scriptElement = document.selectFirst("script#__NEXT_DATA__");
            if (scriptElement == null) {
                throw new IllegalArgumentException("Script element with JSON data not found.");
            }

            // Parse the JSON content
            String jsonData = scriptElement.html();
            JsonNode rootNode = objectMapper.readTree(jsonData);

            // Navigate to the "advert" object
            JsonNode advertNode = rootNode.at("/props/pageProps/advert");

            if (advertNode.isMissingNode()) {
                throw new IllegalArgumentException("Advert data not found in JSON.");
            }

            // Extract equipment -> values
            JsonNode equipmentNode = advertNode.path("equipment");
            if (equipmentNode.isArray()) {
                for (JsonNode category : equipmentNode) {
                    String categoryKey = category.path("key").asText();
                    JsonNode values = category.path("values");

                    for (JsonNode value : values) {
                        String valueKey = value.path("key").asText();
                        String valueLabel = value.path("label").asText();
                        carProperties.put(categoryKey + "." + valueKey, valueLabel);
                    }
                }
            }

            // Extract details -> values and keys
            JsonNode detailsNode = advertNode.path("details");
            if (detailsNode.isArray()) {
                for (JsonNode detail : detailsNode) {
                    String key = detail.path("key").asText();
                    String value = detail.path("value").asText();
                    carProperties.put(key, value);
                }
            }

            // Extract creation date
            String creationDate = advertNode.path("createdAt").asText();
            if (!creationDate.isEmpty()) {
                carProperties.put("createdAt", creationDate);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return carProperties;
    }

}

