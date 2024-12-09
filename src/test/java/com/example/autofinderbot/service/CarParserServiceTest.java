package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarResponse;
import com.example.autofinderbot.parser.CarParserService;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static org.assertj.core.api.Assertions.assertThat;

class CarParserServiceTest extends BaseSpringBootTest {
    @Autowired
    CarParserService carParserService;

    @Autowired
    DocumentService documentService;

    @Test
    void findCars_PosTC() throws IOException {
        Document document = documentService.load(SEARCH_URL);

        List<CarResponse> cars = carParserService.findCars(document);

        assertThat(cars)
                .isNotEmpty();
        assertThat(cars)
                .allMatch(carResponse -> carResponse.getUrl() != null)
                .allMatch(carResponse -> !carResponse.getDetails().isEmpty());
    }
}