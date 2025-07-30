package com.example.autofinderbot.parser.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.Car;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OlxScraperServiceTest extends BaseSpringBootTest {
    @Autowired
    @Qualifier("olxScraper")
    ScraperService<Car> olxScraperService;

    @Test
    void scrape() throws IOException {
        List<Car> scrape = olxScraperService.scrape("https://www.olx.pl/motoryzacja/samochody/?search%5Border%5D=created_at:desc");

        assertThat(scrape)
            .allSatisfy(car -> {
                assertThat(car.getTitle()).isNotNull();
                assertThat(car.getPrice()).isPositive();
                assertThat(car.getMileage()).isNotNull();
                assertThat(car.getDescription()).isNotNull();
                assertThat(car.getSource()).isNotNull();
                assertThat(car.getDetails()).isNotEmpty();
            });
    }
}