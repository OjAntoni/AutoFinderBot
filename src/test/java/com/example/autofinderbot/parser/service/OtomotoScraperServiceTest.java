package com.example.autofinderbot.parser.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.shared.Details;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

class OtomotoScraperServiceTest extends BaseSpringBootTest {
    @Autowired
    @Qualifier("otomotoScraper")
    ScraperService<Car> otomotoScraperService;

    @MockitoSpyBean
    DocumentService<Document> documentService;

    @Test
    void findCars_PosTC() throws IOException {
        List<Car> cars = otomotoScraperService.scrape(SEARCH_URL(2));

        assertThat(cars)
            .isNotEmpty();
        assertThat(cars)
            .anyMatch(carResponse -> carResponse.getCreatedAt() != null &&
                carResponse.getDetails().stream().noneMatch(cd -> cd.getDetail().equals(Details.CREATED_AT.name)))
            .allMatch(carResponse -> carResponse.getUrl() != null)
            .allMatch(carResponse -> !carResponse.getDetails().isEmpty())
            .allMatch(carResponse -> carResponse.getSeller() != null);
    }

    @Test
    void findCarsWithExceptionsOccurredDuringLPageLoad_PosTC() throws IOException {
        AtomicInteger invocationCounter = new AtomicInteger();

        doAnswer(invocation -> {
            if (invocationCounter.getAndIncrement() == 10) {
                throw new IOException();
            }
            return invocation.callRealMethod();
        }).when(documentService).load(anyString(), any());

        List<Car> cars = otomotoScraperService.scrape(SEARCH_URL);

        assertThat(cars)
            .isNotEmpty()
            .allMatch(car -> !car.getDetails().isEmpty() && car.getCreatedAt() != null)
            .allMatch(car -> car.getSeller() != null);
    }
}