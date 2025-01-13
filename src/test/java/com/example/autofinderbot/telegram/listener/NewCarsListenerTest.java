package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.shared.NewCarsEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NewCarsListenerTest extends BaseTelegramListenerTest {
    @Autowired
    NewCarsListener newCarsListener;

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
                        "PLN"
                ),
                new Car(
                        "Nowe Audi",
                        "Audi",
                        "Diesel",
                        120000L,
                        "KM",
                        90000,
                        "PLN"
                ),
                new Car(
                        "Nowe Mercedes",
                        "Mercedes",
                        "Benzyna",
                        135000L,
                        "KM",
                        100000,
                        "PLN"
                )
        ));

        event.getCars().forEach(car -> {
            car.setDetails(emptyList());
            car.setUrl("some-url");
        });

        newCarsListener.handleEvent(event);

        verify(telegramClient, times(2)).executeAsync(any(SendMessage.class));
    }
}