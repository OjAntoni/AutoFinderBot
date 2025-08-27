package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Seller;
import com.example.autofinderbot.parser.service.DocumentService;
import com.example.autofinderbot.common.event.NewCarsEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

import static com.example.autofinderbot.domain.Seller.SellerType.PROFESSIONAL;
import static com.example.autofinderbot.common.util.APIConstants.CAR_PAGE_JSON_DATA;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NewCarsListenerTest extends BaseTelegramListenerTest {
    @Autowired
    NewCarsListener newCarsListener;
    @Autowired
    DocumentService<Document> documentService;

    @Test
    void handleEvent_PosTC() throws TelegramApiException {
        NewCarsEvent event = new NewCarsEvent(this, List.of(
                new Car(
                        "Nowe BMW",
                        "BMW",
                        "Benzyna",
                        18000L,
                        "KM",
                        80000,
                        "PLN",
                        Car.Source.OTOMOTO
                ),
                new Car(
                        "Nowe Audi",
                        "Audi",
                        "Diesel",
                        120000L,
                        "KM",
                        90000,
                        "PLN",
                        Car.Source.OTOMOTO
                ),
                new Car(
                        "Nowe Mercedes",
                        "Mercedes",
                        "Benzyna",
                        135000L,
                        "KM",
                        100000,
                        "PLN",
                        Car.Source.OTOMOTO
                )
        ));

        event.getCars().forEach(car -> {
            car.setDetails(emptyList());
            car.setUrl("some-url");
            car.setSeller(
                Seller.builder()
                    .type(PROFESSIONAL)
                    .build()
            );
        });

        newCarsListener.handleEvent(event);

        verify(telegramClient, times(2)).executeAsync(any(SendMessage.class));
    }

    @Test
    void test() throws IOException {
        Document document = documentService.load("https://www.otomoto.pl/osobowe?search%5Badvanced_search_expanded%5D=true", (doc) -> true);
        Element scriptElement = document.selectFirst(CAR_PAGE_JSON_DATA);
        String jsonContent = scriptElement.html();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        System.out.println("");
    }
}