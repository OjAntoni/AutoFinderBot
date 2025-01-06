package com.example.autofinderbot.parser;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarBrand;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarFiltersParserTest extends BaseSpringBootTest {
    @Autowired
    CarFiltersParser carFiltersParser;
    Document document = mock(Document.class);

    @Test
    @SuppressWarnings("ConstantConditions")
    void returnEmptyListOnMissingNode_NegTC() {
        when(document.selectFirst(anyString())).thenAnswer(invocation -> null);

        List<CarBrand> carBrands = carFiltersParser.extractCarBrands(document);

        assertThat(carBrands)
            .isEmpty();
    }
}