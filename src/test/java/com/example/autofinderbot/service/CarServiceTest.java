package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarResponse;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static org.assertj.core.api.Assertions.assertThat;

class CarServiceTest extends BaseSpringBootTest {
    @Autowired
    CarService carService;

    @Autowired
    DocumentService documentService;

    @Test
    void findCars_PosTC() throws IOException {
        Document document = documentService.load(SEARCH_URL);

        List<CarResponse> cars = carService.findCars(document);

        System.out.println(cars.size());
        assertThat(cars)
                .isNotEmpty();
    }

    @Test
    void findCars_PosTC1() throws IOException {
        Document document = documentService.load("https://www.otomoto.pl/osobowe/oferta/skoda-superb-skoda-superb-polski-salon-stan-bdb-dwa-komplet-opon-ID6GU3V3.html");

//        Map<String, String> stringStringMap = carService.extractCarProperties(document);
        System.out.println();
    }
}