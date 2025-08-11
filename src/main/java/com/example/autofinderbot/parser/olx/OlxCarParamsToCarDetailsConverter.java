package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.GearboxType;
import com.example.autofinderbot.shared.Answer;
import com.example.autofinderbot.shared.Details;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OlxCarParamsToCarDetailsConverter {
    private static final Map<String, Details> KEY_TO_DETAIL;
    static {
        Map<String, Details> m = new HashMap<>();
        m.put("year", Details.YEAR);
        m.put("model", Details.MODEL);
        m.put("petrol", Details.FUEL_TYPE);      // OLX “petrol” → enum FUEL_TYPE (“fuel_type”)
        m.put("milage", Details.MILEAGE);
        m.put("color", Details.COLOR);
        m.put("enginesize", Details.ENGINE_CAPACITY);
        m.put("enginepower", Details.ENGINE_POWER);
        m.put("transmission", Details.GEARBOX);
        m.put("condition", Details.DAMAGED);
        KEY_TO_DETAIL = Collections.unmodifiableMap(m);
    }

    public List<CarDetail> convert(List<OlxCar.Param> params) {
        return params.stream()
            .map(p -> {
                Details detailEnum = KEY_TO_DETAIL.get(p.getKey());
                if (detailEnum == null) {
                    // skip unknown keys
                    return null;
                }
                // Determine the stored value
                String storedValue = switch (detailEnum) {
                    case DAMAGED ->
                        // OLX normalizedValue "notdamaged" -> not damaged (NIE), else TAK
                        "notdamaged".equalsIgnoreCase(p.getNormalizedValue())
                            ? Answer.NO_PL.getValue()
                            : Answer.YES_PL.getValue();
                    case GEARBOX ->
                        // OLX normalizedValue "automatic" -> Automatyczna, else Manualna
                        "automatic".equalsIgnoreCase(p.getNormalizedValue())
                            ? GearboxType.AUTOMATIC.getName()
                            : GearboxType.MANUAL.getName();
                    default ->
                        // default: use the raw or normalized value as you prefer
                        p.getValue();
                };
                return new CarDetail(detailEnum.name, storedValue);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
