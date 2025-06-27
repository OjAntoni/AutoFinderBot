package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.domain.FuelType;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OlxCarFiltersParser {
    Logger logger;

    public List<CarBrand> extractCarBrands(Document document) {
        try {
            Elements options = document.select("select[name*=marka] option[value]");
            List<CarBrand> brands = new ArrayList<>();
            long idx = 1;
            for (Element option : options) {
                String val = option.attr("value");
                if (val == null || val.isBlank()) continue;
                CarBrand brand = new CarBrand(idx++, val, option.text());
                brands.add(brand);
            }
            return brands;
        } catch (Exception e) {
            logger.error(e);
            return emptyList();
        }
    }

    public List<FuelType> extractFuelTypes(Document document) {
        try {
            Elements options = document.select("select[name*=fuel] option[value]");
            List<FuelType> result = new ArrayList<>();
            long idx = 1;
            for (Element option : options) {
                String val = option.attr("value");
                if (val == null || val.isBlank()) continue;
                result.add(new FuelType(idx++, val, option.text()));
            }
            return result;
        } catch (Exception e) {
            logger.error(e);
            return emptyList();
        }
    }
}
