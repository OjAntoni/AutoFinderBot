package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.domain.CarModel;
import com.example.autofinderbot.domain.FuelType;
import com.example.autofinderbot.domain.Generation;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.shared.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CarFiltersParser {
    private static final String SCRIPT_ERROR_MESSAGE = "Script element with JSON data not found.";
    private static final String FILTERS_ERROR_MESSAGE = "Filters data not found in JSON.";

    Logger logger;

    public Predicate<Document> documentValidator(){
        return (document) -> {
            Element scriptElement = document.selectFirst("script#__NEXT_DATA__");
            if(scriptElement == null) {
                logger.error(SCRIPT_ERROR_MESSAGE);
                return false;
            }
            String jsonContent = scriptElement.html();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(jsonContent);
                if (rootNode.at("/props/pageProps/filters").isMissingNode()){
                    logger.error(FILTERS_ERROR_MESSAGE);
                    return false;
                }
            } catch (JsonProcessingException e) {
                logger.error(e);
                return false;
            }
            return true;
        };
    }

    public List<CarBrand> extractCarBrands(Document document) {
        try {
            Element scriptElement = document.selectFirst("script#__NEXT_DATA__");
            if (scriptElement == null) {
                logger.error(SCRIPT_ERROR_MESSAGE);
                return emptyList();
            }

            String jsonContent = scriptElement.html();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            JsonNode filtersNode = rootNode.at("/props/pageProps/filters");
            if (filtersNode.isMissingNode()) {
                logger.error(FILTERS_ERROR_MESSAGE);
                return emptyList();
            }

            List<CarBrand> carBrands = new ArrayList<>();
            JsonNode carBrandsNode = filtersNode.at("/571/_meta/values");
            JsonNode carModelsNode = rootNode.at("/props/pageProps/filtersValues");

            for (JsonNode group : carBrandsNode) {
                for (JsonNode carBrand : group.get("group_values")) {
                    long brandId = carBrand.get("value_key").asLong();
                    CarBrand brand = new CarBrand(
                            brandId,
                            carBrand.get("search_key").asText(),
                            carBrand.get("name").asText().replaceAll(" \\(\\d+\\)$", "").trim());
                    carBrands.add(brand);
                    for (JsonNode model : carModelsNode.at("/573:571:" + brandId + "/0/group_values")) {
                        long carModelId = model.get("value_key").asLong();
                        CarModel carModel = new CarModel(
                                carModelId,
                                model.get("search_key").asText(),
                                model.get("name").asText().replaceAll(" \\(\\d+\\)$", "").trim(),
                                brandId);
                        for (JsonNode generation : carModelsNode.at("/3018:573:" + carModelId + "/0/group_values")) {
                            carModel.getGenerations().add(
                                    new Generation(
                                            generation.get("value_key").asLong(),
                                            generation.get("search_key").asText(),
                                            generation.get("name").asText().replaceAll("\\s*\\(\\d+\\)$", "").trim(),
                                            carModelId
                                    )
                            );
                        }
                        brand.getModels().add(carModel);
                    }
                }
            }
            return carBrands;
        } catch (Exception e) {
            logger.error(e);
            return emptyList();
        }
    }

    public List<FuelType> extractFuelTypes(Document document) {
        try {
            List<FuelType> fuelTypes = new ArrayList<>();
            Element scriptElement = document.selectFirst("script#__NEXT_DATA__");
            if (scriptElement == null) return emptyList();
            String jsonContent = scriptElement.html();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            JsonNode filtersNode = rootNode.at("/props/pageProps/filters");
            JsonNode petrolTypesNode = filtersNode.at("/581/_meta/values");
            if (!petrolTypesNode.isMissingNode()) {
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
                logger.error("Fuel types not found in filters.");
            }
            return fuelTypes;
        } catch (Exception e) {
            logger.error(e);
            return emptyList();
        }

    }


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
                JsonNode carBrandsNode = filtersNode.at("/571/_meta/values");
                JsonNode carModelsNode = rootNode.at("/props/pageProps/filtersValues");

                for (JsonNode group : carBrandsNode) {
                    for (JsonNode carBrand : group.get("group_values")) {
                        long brandId = carBrand.get("value_key").asLong();
                        CarBrand brand = new CarBrand(
                                brandId,
                                carBrand.get("search_key").asText(),
                                carBrand.get("name").asText().replaceAll(" \\(\\d+\\)$", "").trim());
                        carBrands.add(brand);
                        for (JsonNode model : carModelsNode.at("/573:571:" + brandId + "/0/group_values")) {
                            long carModelId = model.get("value_key").asLong();
                            CarModel carModel = new CarModel(
                                    carModelId,
                                    model.get("search_key").asText(),
                                    model.get("name").asText().replaceAll(" \\(\\d+\\)$", "").trim(),
                                    brandId);
                            for (JsonNode generation : carModelsNode.at("/3018:573:" + carModelId + "/0/group_values")) {
                                carModel.getGenerations().add(
                                        new Generation(
                                                generation.get("value_key").asLong(),
                                                generation.get("search_key").asText(),
                                                generation.get("name").asText().replaceAll("\\s*\\(\\d+\\)$", "").trim(),
                                                carModelId
                                        )
                                );
                            }
                            brand.getModels().add(carModel);
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
