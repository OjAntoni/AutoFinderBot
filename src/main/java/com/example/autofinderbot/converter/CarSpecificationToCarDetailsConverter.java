package com.example.autofinderbot.converter;

import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.shared.Details;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Function.*;

@Component
public class CarSpecificationToCarDetailsConverter {
    public List<CarDetail> convert(Map<String, String> specification) {
        Map<String, Details> attributeToDetails = Arrays.stream(Details.values()).collect(Collectors.toMap(Details::getAttribute, identity()));
        Set<String> attributes = Arrays.stream(Details.values()).map(Details::getAttribute).collect(Collectors.toSet());
        return specification.entrySet().stream()
                .filter(entry -> attributes.contains(entry.getKey()))
                .map(entry -> new CarDetail(attributeToDetails.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }
}
