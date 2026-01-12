package com.example.autofinderbot.car.core;

import com.example.autofinderbot.car.selection.SelectedCar;
import com.example.autofinderbot.car.selection.SelectedCarRepository;
import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


class CarListenerTest extends BaseTelegramListenerTest {
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    SelectedCarRepository selectedCarRepository;
    @Autowired
    CarListener carListener;
    @Autowired
    UserService userService;

    @Test
    void likeCar() throws TelegramApiException {
        Mockito.when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        Mockito.when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(12345);
        Mockito.when(update.getCallbackQuery().getId()).thenReturn(String.valueOf(12345L));

        carListener.likeCar(update, 6L);

        assertThat(selectedCarRepository.findAllByUserIdOrderByCreatedAtDesc(1L))
            .extracting(
                SelectedCar::getCarId,
                SelectedCar::getMessageId,
                SelectedCar::getCarName,
                SelectedCar::getUrl)
            .containsExactly(
                tuple(6L, 12345, "Toyota Corolla expired 3", "https://www.otomoto.pl/osobowe/oferta/invalid-url-3"),
                tuple(1L, 1, "Audi A4", "https://www.example.com/audi-a4"),
                tuple(2L, 2, "BMW 3", "https://www.example.com/bmw-3"),
                tuple(3L, 3, "Mercedes C", "https://www.example.com/mercedes-c"),
                tuple(4L, 4, "Toyota Corolla expired 1", "https://www.otomoto.pl/osobowe/oferta/invalid-url-1")
            );

        verify(telegramClient, times(2)).execute(any(EditMessageReplyMarkup.class));
        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
    }

    @Test
    void throwOnLikeMissingCar() throws TelegramApiException {
        Mockito.when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        Mockito.when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(12345);
        Mockito.when(update.getCallbackQuery().getId()).thenReturn(String.valueOf(12345L));

        carListener.likeCar(update, 12345L);

        assertThat(selectedCarRepository.findAllByUserIdOrderByCreatedAtDesc(1L))
            .extracting(
                SelectedCar::getCarId,
                SelectedCar::getMessageId,
                SelectedCar::getCarName,
                SelectedCar::getUrl)
            .containsExactly(
                tuple(1L, 1, "Audi A4", "https://www.example.com/audi-a4"),
                tuple(2L, 2, "BMW 3", "https://www.example.com/bmw-3"),
                tuple(3L, 3, "Mercedes C", "https://www.example.com/mercedes-c"),
                tuple(4L, 4, "Toyota Corolla expired 1", "https://www.otomoto.pl/osobowe/oferta/invalid-url-1"),
                tuple(5L, 5, "Toyota Corolla expired 2", "https://www.otomoto.pl/osobowe/oferta/invalid-url-2")
            );

        verify(telegramClient, never()).execute(any(EditMessageReplyMarkup.class));
        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
    }

    @Test
    void dislikeCar() throws TelegramApiException {
        Mockito.when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        Mockito.when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(12345);
        Mockito.when(update.getCallbackQuery().getId()).thenReturn(String.valueOf(12345L));

        carListener.dislikeCar(update, 1L);

        assertThat(selectedCarRepository.findAllByUserIdOrderByCreatedAtDesc(1L))
            .extracting(
                SelectedCar::getCarId,
                SelectedCar::getMessageId,
                SelectedCar::getCarName,
                SelectedCar::getUrl)
            .containsExactly(
                tuple(2L, 2, "BMW 3", "https://www.example.com/bmw-3"),
                tuple(3L, 3, "Mercedes C", "https://www.example.com/mercedes-c"),
                tuple(4L, 4, "Toyota Corolla expired 1", "https://www.otomoto.pl/osobowe/oferta/invalid-url-1"),
                tuple(5L, 5, "Toyota Corolla expired 2", "https://www.otomoto.pl/osobowe/oferta/invalid-url-2")
            );

        verify(telegramClient).execute(any(EditMessageReplyMarkup.class));
        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
    }

    @Test
    void throwOnDislikeMissingCar_NegTC() throws TelegramApiException {
        Mockito.when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        Mockito.when(update.getCallbackQuery().getId()).thenReturn(String.valueOf(12345L));

        carListener.dislikeCar(update, 12345L);

        assertThat(selectedCarRepository.findAllByUserIdOrderByCreatedAtDesc(1L))
            .extracting(
                SelectedCar::getCarId,
                SelectedCar::getMessageId,
                SelectedCar::getCarName,
                SelectedCar::getUrl)
            .containsExactly(
                tuple(1L, 1, "Audi A4", "https://www.example.com/audi-a4"),
                tuple(2L, 2, "BMW 3", "https://www.example.com/bmw-3"),
                tuple(3L, 3, "Mercedes C", "https://www.example.com/mercedes-c"),
                tuple(4L, 4, "Toyota Corolla expired 1", "https://www.otomoto.pl/osobowe/oferta/invalid-url-1"),
                tuple(5L, 5, "Toyota Corolla expired 2", "https://www.otomoto.pl/osobowe/oferta/invalid-url-2")
            );

        verify(telegramClient, never()).execute(any(EditMessageReplyMarkup.class));
        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
    }

    @Test
    void showSelectedCars() throws TelegramApiException {
        Mockito.when(update.getMessage().getChatId()).thenReturn(1L);

        carListener.showSelectedCars(update);

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("""
                1. [Audi A4](https://www.example.com/audi-a4) ❌
                2. [BMW 3](https://www.example.com/bmw-3) ❌
                3. [Mercedes C](https://www.example.com/mercedes-c) ❌
                4. [Toyota Corolla expired 1](https://www.otomoto.pl/osobowe/oferta/invalid-url-1) ❌
                5. [Toyota Corolla expired 2](https://www.otomoto.pl/osobowe/oferta/invalid-url-2) ❌
                """);
        }));
    }
}

