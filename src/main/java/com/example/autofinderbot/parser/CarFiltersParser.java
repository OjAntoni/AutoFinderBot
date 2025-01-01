package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarModel;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CarFiltersParser {
        public static void main(String[] args) {
            DocumentService documentService1 = new DocumentService(new Logger());
            try {
                // Load the HTML content
                String url = "YOUR_URL_HERE"; // Replace with the target URL
                Document document = documentService1.load("https://www.otomoto.pl/", (doc) -> true);

                // Extract the script element containing the JSON
                Element scriptElement = document.selectFirst("script#__NEXT_DATA__");
                if (scriptElement == null) {
                    System.out.println("Script element not found!");
                    return;
                }

                // Parse the JSON from the script element
                String jsonContent = scriptElement.html();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonContent);

                // Navigate to the "filters" node
                JsonNode filtersNode = rootNode.at("/props/pageProps/filters");
                if (filtersNode.isMissingNode()) {
                    System.out.println("Filters node not found!");
                    return;
                }

                // Extract car models
                List<CarModel> carModels = new ArrayList<>();
                JsonNode carModelsNode = filtersNode.at("/571/_meta/values");
                System.out.println("Car Models:");
                for (JsonNode group : carModelsNode) {
                    for (JsonNode carModel : group.get("group_values")) {
                        String name = carModel.get("name").asText();
                        carModels.add(new CarModel(
                                carModel.get("value_key").asLong(),
                                carModel.get("search_key").asText(),
                                carModel.get("name").asText().replaceAll(" \\(\\d+\\)$", "").trim()
                                ));
//                        System.out.println("- " + name);
                    }
                }

                // Extract petrol types (if there's a similar structure, adapt accordingly)
                // Assuming there's a filter for petrol types with a specific ID, e.g., "petrol_type"
                JsonNode petrolTypesNode = filtersNode.at("/581/_meta/values"); // Replace POSSIBLE_ID with the actual ID.
                if (!petrolTypesNode.isMissingNode()) {
                    System.out.println("\nPetrol Types:");
                    for (JsonNode group : petrolTypesNode) {
                        for (JsonNode petrolType : group.get("group_values")) {
                            String name = petrolType.get("name").asText();
                            System.out.println("- " + name);
                        }
                    }
                } else {
                    System.out.println("\nPetrol types not found in filters.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
