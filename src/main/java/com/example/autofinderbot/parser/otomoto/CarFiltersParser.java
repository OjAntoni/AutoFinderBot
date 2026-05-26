package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.car.brand.CarBrand;
import com.example.autofinderbot.car.fuel.FuelType;
import com.example.autofinderbot.car.generation.Generation;
import com.example.autofinderbot.car.model.CarModel;
import com.example.autofinderbot.common.util.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.autofinderbot.common.util.APIConstants.*;
import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CarFiltersParser {
    private static final String SCRIPT_ERROR_MESSAGE = "Script element with JSON data not found.";
    private static final String FILTERS_ERROR_MESSAGE = "Filters data not found in JSON.";
    private static final String FILTER_MAKE = "filter_enum_make";
    private static final String FILTER_MODEL = "filter_enum_model";
    private static final String FILTER_GENERATION = "filter_enum_generation";
    private static final String FILTER_FUEL_TYPE = "filter_enum_fuel_type";
    private static final String STATES = "states";
    private static final String FILTER_ID = "filterId";
    private static final String CONDITIONS = "conditions";
    private static final String ID = "id";
    private static final Pattern LEGACY_MODEL_ALIAS = Pattern.compile("^([A-Z]+\\d+)(?:\\s+.+)$");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    Logger logger;

    public Predicate<Document> documentValidator() {
        return document -> {
            try {
                JsonNode rootNode = readNextData(document);
                if (filtersNode(rootNode) == null) {
                    logger.error(FILTERS_ERROR_MESSAGE);
                    return false;
                }
                return true;
            } catch (JsonProcessingException e) {
                logger.error(e);
                return false;
            }
        };
    }

    public List<CarBrand> extractCarBrands(Document document) {
        try {
            JsonNode rootNode = readNextData(document);
            JsonNode filtersNode = filtersNode(rootNode);
            if (filtersNode == null) {
                logger.error(FILTERS_ERROR_MESSAGE);
                return emptyList();
            }
            if (isCurrentFiltersNode(filtersNode)) {
                return extractCurrentCarBrands(filtersNode);
            }
            return extractLegacyCarBrands(rootNode, filtersNode);
        } catch (Exception e) {
            logger.error(e);
            return emptyList();
        }
    }

    public List<FuelType> extractFuelTypes(Document document) {
        try {
            JsonNode rootNode = readNextData(document);
            JsonNode filtersNode = filtersNode(rootNode);
            if (filtersNode == null) {
                logger.error(FILTERS_ERROR_MESSAGE);
                return emptyList();
            }
            if (isCurrentFiltersNode(filtersNode)) {
                return extractCurrentFuelTypes(filtersNode);
            }
            return extractLegacyFuelTypes(filtersNode);
        } catch (Exception e) {
            logger.error(e);
            return emptyList();
        }
    }

    private JsonNode readNextData(Document document) throws JsonProcessingException {
        Element scriptElement = document.selectFirst(CAR_PAGE_JSON_DATA);
        if (scriptElement == null) {
            throw new JsonProcessingException(SCRIPT_ERROR_MESSAGE) {};
        }
        return OBJECT_MAPPER.readTree(scriptElement.html());
    }

    private JsonNode filtersNode(JsonNode rootNode) throws JsonProcessingException {
        JsonNode legacyFiltersNode = rootNode.at(CAR_FILTERS);
        if (!legacyFiltersNode.isMissingNode()) {
            return legacyFiltersNode;
        }

        JsonNode currentFiltersNode = urqlFiltersNode(rootNode);
        if (currentFiltersNode != null) {
            return currentFiltersNode;
        }

        JsonNode screenComponentsFiltersNode = rootNode.at("/props/pageProps/screenComponentsFilters");
        if (isCurrentFiltersNode(screenComponentsFiltersNode)) {
            return screenComponentsFiltersNode;
        }

        return null;
    }

    private JsonNode urqlFiltersNode(JsonNode rootNode) throws JsonProcessingException {
        JsonNode urqlStateNode = rootNode.at("/props/pageProps/urqlState");
        if (!urqlStateNode.isObject()) {
            return null;
        }

        JsonNode bestFiltersNode = null;
        int bestStateCount = -1;
        for (JsonNode urqlEntry : urqlStateNode) {
            JsonNode dataNode = urqlEntry.get("data");
            if (dataNode == null || !dataNode.isTextual()) {
                continue;
            }
            JsonNode dataRootNode = OBJECT_MAPPER.readTree(dataNode.asText());
            JsonNode filtersNode = dataRootNode.path("filters");
            if (!isCurrentFiltersNode(filtersNode)) {
                filtersNode = dataRootNode.path("advertSearch").path("filters");
            }
            if (isCurrentFiltersNode(filtersNode) && filtersNode.path(STATES).size() > bestStateCount) {
                bestFiltersNode = filtersNode;
                bestStateCount = filtersNode.path(STATES).size();
            }
        }

        return bestFiltersNode;
    }

    private boolean isCurrentFiltersNode(JsonNode filtersNode) {
        return filtersNode != null && filtersNode.has(STATES);
    }

    private List<CarBrand> extractLegacyCarBrands(JsonNode rootNode, JsonNode filtersNode) {
        List<CarBrand> carBrands = new ArrayList<>();
        JsonNode carBrandsNode = filtersNode.at(CAR_BRANDS);
        JsonNode carModelsNode = rootNode.at(CAR_MODELS);

        for (JsonNode group : carBrandsNode) {
            for (JsonNode carBrand : group.get(GROUP_VALUES)) {
                long brandId = carBrand.get(VALUE_KEY).asLong();
                CarBrand brand = new CarBrand(
                    brandId,
                    carBrand.get(SEARCH_KEY).asText(),
                    removeCounter(carBrand.get(NAME).asText()));
                carBrands.add(brand);
                for (JsonNode model : carModelsNode.at("/573:571:" + brandId + "/0/" + GROUP_VALUES)) {
                    long carModelId = model.get(VALUE_KEY).asLong();
                    CarModel carModel = new CarModel(
                        carModelId,
                        model.get(SEARCH_KEY).asText(),
                        removeCounter(model.get(NAME).asText()),
                        brandId);
                    for (JsonNode generation : carModelsNode.at("/3018:573:" + carModelId + "/0/" + GROUP_VALUES)) {
                        carModel.getGenerations().add(
                            new Generation(
                                generation.get(VALUE_KEY).asLong(),
                                generation.get(SEARCH_KEY).asText(),
                                removeCounter(generation.get(NAME).asText()),
                                carModelId
                            )
                        );
                    }
                    brand.getModels().add(carModel);
                }
            }
        }
        return carBrands;
    }

    private List<FuelType> extractLegacyFuelTypes(JsonNode filtersNode) {
        List<FuelType> fuelTypes = new ArrayList<>();
        JsonNode petrolTypesNode = filtersNode.at(FUEL_TYPES);
        if (petrolTypesNode.isMissingNode()) {
            logger.error("Fuel types not found in filters.");
            return fuelTypes;
        }

        for (JsonNode group : petrolTypesNode) {
            for (JsonNode petrolType : group.get(GROUP_VALUES)) {
                fuelTypes.add(new FuelType(
                    petrolType.get(VALUE_KEY).asLong(),
                    petrolType.get(SEARCH_KEY).asText(),
                    petrolType.get(NAME).asText()
                ));
            }
        }
        return fuelTypes;
    }

    private List<CarBrand> extractCurrentCarBrands(JsonNode filtersNode) {
        Map<String, CarBrand> brandsBySearchKey = new LinkedHashMap<>();
        Map<String, CarModel> modelsByBrandAndSearchKey = new LinkedHashMap<>();

        JsonNode brandState = firstState(filtersNode, FILTER_MAKE);
        if (brandState == null) {
            return emptyList();
        }

        for (JsonNode brandNode : values(brandState)) {
            String brandSearchKey = brandNode.path(ID).asText();
            String brandName = brandNode.path(NAME).asText();
            if (brandSearchKey.isBlank() || brandName.isBlank()) {
                continue;
            }
            brandsBySearchKey.put(brandSearchKey, new CarBrand(
                stableId("brand", brandSearchKey),
                brandSearchKey,
                brandName));
        }

        for (JsonNode modelState : states(filtersNode, FILTER_MODEL)) {
            String brandSearchKey = conditionValue(modelState, FILTER_MAKE);
            CarBrand brand = brandsBySearchKey.get(brandSearchKey);
            if (brand == null) {
                continue;
            }
            for (JsonNode modelNode : values(modelState)) {
                String modelSearchKey = modelNode.path(ID).asText();
                String modelName = modelNode.path(NAME).asText();
                if (modelSearchKey.isBlank() || modelName.isBlank()) {
                    continue;
                }
                CarModel carModel = new CarModel(
                    stableId("model", brandSearchKey, modelSearchKey),
                    modelSearchKey,
                    modelName,
                    brand.getId());
                brand.getModels().add(carModel);
                modelsByBrandAndSearchKey.put(compositeKey(brandSearchKey, modelSearchKey), carModel);
            }
        }

        addLegacyModelAliases(brandsBySearchKey, modelsByBrandAndSearchKey);
        addCurrentGenerations(filtersNode, modelsByBrandAndSearchKey);

        return new ArrayList<>(brandsBySearchKey.values());
    }

    private List<FuelType> extractCurrentFuelTypes(JsonNode filtersNode) {
        JsonNode fuelTypeState = firstState(filtersNode, FILTER_FUEL_TYPE);
        if (fuelTypeState == null) {
            logger.error("Fuel types not found in filters.");
            return emptyList();
        }

        List<FuelType> fuelTypes = new ArrayList<>();
        for (JsonNode fuelTypeNode : values(fuelTypeState)) {
            String fuelTypeSearchKey = fuelTypeNode.path(ID).asText();
            String fuelTypeName = fuelTypeNode.path(NAME).asText();
            if (fuelTypeSearchKey.isBlank() || fuelTypeName.isBlank()) {
                continue;
            }
            fuelTypes.add(new FuelType(
                stableId("fuel", fuelTypeSearchKey),
                fuelTypeSearchKey,
                fuelTypeName));
        }
        return fuelTypes;
    }

    private void addLegacyModelAliases(
        Map<String, CarBrand> brandsBySearchKey,
        Map<String, CarModel> modelsByBrandAndSearchKey
    ) {
        for (Map.Entry<String, CarBrand> entry : brandsBySearchKey.entrySet()) {
            String brandSearchKey = entry.getKey();
            CarBrand brand = entry.getValue();
            List<CarModel> models = new ArrayList<>(brand.getModels());
            for (CarModel model : models) {
                String aliasName = legacyModelAliasName(model.getName());
                if (aliasName == null) {
                    continue;
                }
                String aliasSearchKey = aliasName.toLowerCase(Locale.ROOT);
                String aliasKey = compositeKey(brandSearchKey, aliasSearchKey);
                if (modelsByBrandAndSearchKey.containsKey(aliasKey)) {
                    continue;
                }
                CarModel aliasModel = new CarModel(
                    stableId("model", brandSearchKey, aliasSearchKey),
                    aliasSearchKey,
                    aliasName,
                    brand.getId());
                brand.getModels().add(aliasModel);
                modelsByBrandAndSearchKey.put(aliasKey, aliasModel);
            }
        }
    }

    private void addCurrentGenerations(
        JsonNode filtersNode,
        Map<String, CarModel> modelsByBrandAndSearchKey
    ) {
        for (JsonNode generationState : states(filtersNode, FILTER_GENERATION)) {
            String brandSearchKey = conditionValue(generationState, FILTER_MAKE);
            String modelSearchKey = conditionValue(generationState, FILTER_MODEL);
            CarModel directModel = modelsByBrandAndSearchKey.get(compositeKey(brandSearchKey, modelSearchKey));
            if (directModel == null) {
                continue;
            }

            CarModel aliasModel = null;
            String aliasName = legacyModelAliasName(directModel.getName());
            if (aliasName != null) {
                aliasModel = modelsByBrandAndSearchKey.get(compositeKey(brandSearchKey, aliasName.toLowerCase(Locale.ROOT)));
            }

            for (JsonNode generationNode : values(generationState)) {
                addGeneration(brandSearchKey, directModel, generationNode);
                if (aliasModel != null && aliasModel != directModel) {
                    addGeneration(brandSearchKey, aliasModel, generationNode);
                }
            }
        }
    }

    private void addGeneration(String brandSearchKey, CarModel model, JsonNode generationNode) {
        String generationSearchKey = generationNode.path(ID).asText();
        String generationName = generationNode.path(NAME).asText();
        if (generationSearchKey.isBlank() || generationName.isBlank()) {
            return;
        }
        boolean alreadyAdded = model.getGenerations().stream()
            .anyMatch(generation -> generation.getSearchKey().equals(generationSearchKey));
        if (alreadyAdded) {
            return;
        }
        model.getGenerations().add(new Generation(
            stableId("generation", brandSearchKey, model.getSearchKey(), generationSearchKey),
            generationSearchKey,
            generationName,
            model.getId()));
    }

    private JsonNode firstState(JsonNode filtersNode, String filterId) {
        for (JsonNode state : states(filtersNode, filterId)) {
            return state;
        }
        return null;
    }

    private List<JsonNode> states(JsonNode filtersNode, String filterId) {
        List<JsonNode> states = new ArrayList<>();
        for (JsonNode state : filtersNode.path(STATES)) {
            if (filterId.equals(state.path(FILTER_ID).asText())) {
                states.add(state);
            }
        }
        return states;
    }

    private List<JsonNode> values(JsonNode state) {
        List<JsonNode> values = new ArrayList<>();
        for (JsonNode group : state.path(VALUES)) {
            for (JsonNode value : group.path(VALUES)) {
                values.add(value);
            }
        }
        return values;
    }

    private String conditionValue(JsonNode state, String filterId) {
        for (JsonNode condition : state.path(CONDITIONS)) {
            if (filterId.equals(condition.path(FILTER_ID).asText())) {
                return condition.path(VALUE).asText();
            }
        }
        return null;
    }

    private String legacyModelAliasName(String modelName) {
        Matcher matcher = LEGACY_MODEL_ALIAS.matcher(modelName);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

    private String compositeKey(String brandSearchKey, String modelSearchKey) {
        return brandSearchKey + "\n" + modelSearchKey;
    }

    private static String removeCounter(String value) {
        return value.replaceAll("\\s*\\(\\d+\\)$", "").trim();
    }

    private static long stableId(String... values) {
        UUID uuid = UUID.nameUUIDFromBytes(String.join(":", values).getBytes(StandardCharsets.UTF_8));
        return uuid.getMostSignificantBits() & Long.MAX_VALUE;
    }
}
