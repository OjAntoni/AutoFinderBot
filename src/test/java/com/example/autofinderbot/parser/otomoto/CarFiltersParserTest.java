package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.domain.FuelType;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DirtiesContext
class CarFiltersParserTest extends BaseSpringBootTest {
    @Autowired
    CarFiltersParser carFiltersParser;

    @Test
    @SuppressWarnings("ConstantConditions")
    void returnEmptyListOnMissingNodeDuringCarBrandsUpdate_NegTC() {
        Document document = mock(Document.class);
        when(document.selectFirst(anyString())).thenAnswer(invocation -> null);

        List<CarBrand> carBrands = carFiltersParser.extractCarBrands(document);

        assertThat(carBrands)
            .isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void returnEmptyListOnMissingNodeDuringFuelTypesUpdate_NegTC() {
        Document document = mock(Document.class);
        when(document.selectFirst(anyString())).thenThrow(new RuntimeException("Some exception."));

        List<FuelType> fuelTypes = carFiltersParser.extractFuelTypes(document);

        assertThat(fuelTypes)
                .isEmpty();
    }
}