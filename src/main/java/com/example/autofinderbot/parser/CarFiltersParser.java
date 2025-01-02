package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.domain.CarModel;
import com.example.autofinderbot.domain.FuelType;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CarFiltersParser {
        public static void main(String[] args) {
            DocumentService documentService1 = new DocumentService(new Logger());
            try {
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
                List<CarBrand> carBrands = new ArrayList<>();
                List<CarModel> carModels = new ArrayList<>();
                JsonNode carBrandsNode = filtersNode.at("/571/_meta/values");
                JsonNode carModelsNode = rootNode.at("/props/pageProps/filtersValues");

                for (JsonNode group : carBrandsNode) {
                    for (JsonNode carModel : group.get("group_values")) {
                        long brandId = carModel.get("value_key").asLong();
                        carBrands.add(new CarBrand(
                                brandId,
                                carModel.get("search_key").asText(),
                                carModel.get("name").asText().replaceAll(" \\(\\d+\\)$", "").trim()
                                ));
                        for (JsonNode model : carModelsNode.at("/573:571:" + brandId + "/0/group_values")) {
                            carModels.add(new CarModel(
                                    model.get("value_key").asLong(),
                                    model.get("search_key").asText(),
                                    model.get("name").asText().replaceAll(" \\(\\d+\\)$", "").trim(),
                                    brandId
                            ));
                        }
                    }
                }


                List<FuelType> fuelTypes = new ArrayList<>();
                JsonNode petrolTypesNode = filtersNode.at("/581/_meta/values"); // Replace POSSIBLE_ID with the actual ID.
                if (!petrolTypesNode.isMissingNode()) {
                    System.out.println("\nPetrol Types:");
                    for (JsonNode group : petrolTypesNode) {
                        for (JsonNode petrolType : group.get("group_values")) {
                            fuelTypes.add(new FuelType(
                                    petrolType.get("value_key").asLong(),
                                    petrolType.get("search_key").asText(),
                                    petrolType.get("name").asText()
                            ));
                        }
                    }
                } else {
                    System.out.println("\nPetrol types not found in filters.");
                }
                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
