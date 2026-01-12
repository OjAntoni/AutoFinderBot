package com.example.autofinderbot.car.core;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.car.message.Message;
import com.example.autofinderbot.car.message.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.example.autofinderbot.car.message.Message.State.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarDetailsListenerTest extends BaseTelegramListenerTest {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    CarDetailsListener carDetailsListener;

    @Test
    void showDetails_PosTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);

        carDetailsListener.showDetails(update, 1L);

        assertThat(messageRepository.findByChatIdAndCarId(1L, 1L))
            .extracting(Message::getDetailsState)
            .isEqualTo(DETAILS);

        verify(telegramClient).execute(any(EditMessageText.class));
    }

    @Test
    void throwOnMissingCarDuringShowDetails_NegTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);
        when(update.getCallbackQuery().getId()).thenReturn("1");

        carDetailsListener.showDetails(update, 12345L);

        assertThat(messageRepository.findByChatIdAndCarId(1L, 12345L))
            .isNull();

        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
        verify(telegramClient, never()).execute(any(EditMessageText.class));
    }

    @Test
    void hideDetails_PosTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(3L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);

        carDetailsListener.hideDetails(update, 1L);

        assertThat(messageRepository.findByChatIdAndCarId(3L, 1L))
            .extracting(Message::getDetailsState)
            .isEqualTo(DEFAULT);

        verify(telegramClient).execute(any(EditMessageText.class));
    }

    @Test
    void throwOnMissingCarDuringHideDetails_NegTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(2L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);
        when(update.getCallbackQuery().getId()).thenReturn("1");

        carDetailsListener.hideDetails(update, 12345L);

        assertThat(messageRepository.findByChatIdAndCarId(3L, 1L))
            .extracting(Message::getDetailsState)
            .isEqualTo(DETAILS);

        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
        verify(telegramClient, never()).execute(any(EditMessageText.class));
    }
}

