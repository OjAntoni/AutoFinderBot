package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.car.brand.CarBrand;
import com.example.autofinderbot.car.model.CarModel;
import com.example.autofinderbot.car.fuel.FuelType;
import com.example.autofinderbot.car.generation.Generation;
import com.example.autofinderbot.common.util.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.example.autofinderbot.common.util.APIConstants.*;
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
            Element scriptElement = document.selectFirst(CAR_PAGE_JSON_DATA);
            if(scriptElement == null) {
                logger.error(SCRIPT_ERROR_MESSAGE);
                return false;
            }
            String jsonContent = scriptElement.html();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(jsonContent);
                if (rootNode.at(CAR_FILTERS).isMissingNode()){
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
            Element scriptElement = document.selectFirst(CAR_PAGE_JSON_DATA);
            if (scriptElement == null) {
                logger.error(SCRIPT_ERROR_MESSAGE);
                return emptyList();
            }

            String jsonContent = scriptElement.html();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            JsonNode filtersNode = rootNode.at(CAR_FILTERS);
            if (filtersNode.isMissingNode()) {
                logger.error(FILTERS_ERROR_MESSAGE);
                return emptyList();
            }

            List<CarBrand> carBrands = new ArrayList<>();
            JsonNode carBrandsNode = filtersNode.at(CAR_BRANDS);
            JsonNode carModelsNode = rootNode.at(CAR_MODELS);

            for (JsonNode group : carBrandsNode) {
                for (JsonNode carBrand : group.get(GROUP_VALUES)) {
                    long brandId = carBrand.get(VALUE_KEY).asLong();
                    CarBrand brand = new CarBrand(
                            brandId,
                            carBrand.get(SEARCH_KEY).asText(),
                            carBrand.get(NAME).asText().replaceAll(" \\(\\d+\\)$", "").trim());
                    carBrands.add(brand);
                    for (JsonNode model : carModelsNode.at("/573:571:" + brandId + "/0/" + GROUP_VALUES)) {
                        long carModelId = model.get(VALUE_KEY).asLong();
                        CarModel carModel = new CarModel(
                                carModelId,
                                model.get(SEARCH_KEY).asText(),
                                model.get(NAME).asText().replaceAll(" \\(\\d+\\)$", "").trim(),
                                brandId);
                        for (JsonNode generation : carModelsNode.at("/3018:573:" + carModelId + "/0/" + GROUP_VALUES)) {
                            carModel.getGenerations().add(
                                    new Generation(
                                            generation.get(VALUE_KEY).asLong(),
                                            generation.get(SEARCH_KEY).asText(),
                                            generation.get(NAME).asText().replaceAll("\\s*\\(\\d+\\)$", "").trim(),
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
            Element scriptElement = document.selectFirst(CAR_PAGE_JSON_DATA);
            if (scriptElement == null) return emptyList();
            String jsonContent = scriptElement.html();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            JsonNode filtersNode = rootNode.at(CAR_FILTERS);
            JsonNode petrolTypesNode = filtersNode.at(FUEL_TYPES);
            if (!petrolTypesNode.isMissingNode()) {
                for (JsonNode group : petrolTypesNode) {
                    for (JsonNode petrolType : group.get(GROUP_VALUES)) {
                        fuelTypes.add(new FuelType(
                                petrolType.get(VALUE_KEY).asLong(),
                                petrolType.get(SEARCH_KEY).asText(),
                                petrolType.get(NAME).asText()
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
}
