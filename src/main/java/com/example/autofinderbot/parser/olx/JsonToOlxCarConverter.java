package com.example.autofinderbot.parser.olx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JsonToOlxCarConverter {

    ObjectMapper mapper;

    /**
     * Converts the given root JsonNode into a list of OlxCar objects,
     * by reading the JSON array at "/listing/listing/ads".
     *
     * @param root the root JSON tree
     * @return list of OlxCar, or empty list if path is missing or not an array
     * @throws JsonProcessingException on mapping failures
     */
    public List<OlxCar> convert(JsonNode root) throws JsonProcessingException {
        JsonNode adsNode = root.at("/listing/listing/ads");
        if (adsNode.isMissingNode() || !adsNode.isArray()) {
            return Collections.emptyList();
        }

        List<OlxCar> cars = new ArrayList<>(adsNode.size());
        for (JsonNode adNode : adsNode) {
            OlxCar car = mapper.treeToValue(adNode, OlxCar.class);
            cars.add(car);
        }
        return cars;
    }
}
