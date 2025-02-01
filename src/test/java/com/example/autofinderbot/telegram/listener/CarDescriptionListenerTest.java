package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.domain.Message;
import com.example.autofinderbot.repository.MessageRepository;
import com.example.autofinderbot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.example.autofinderbot.domain.Message.State.DEFAULT;
import static com.example.autofinderbot.domain.Message.State.DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarDescriptionListenerTest extends BaseTelegramListenerTest {
    @SpyBean
    UserService userService;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    CarDescriptionListener carDescriptionListener;

    @Test
    void showDescription_PosTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);

        carDescriptionListener.showDescription(update, 1L);

        assertThat(messageRepository.findByChatIdAndCarId(1L, 1L))
            .extracting(Message::getDescriptionState)
            .isEqualTo(DESCRIPTION);

        verify(telegramClient).execute(any(EditMessageText.class));
    }

    @Test
    void throwOnMissingCarDuringShowDescription_NegTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(1L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);
        when(update.getCallbackQuery().getId()).thenReturn("1");

        carDescriptionListener.showDescription(update, 12345L);

        assertThat(messageRepository.findByChatIdAndCarId(1L, 12345L))
            .isNull();

        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
        verify(telegramClient, never()).execute(any(EditMessageText.class));
    }

    @Test
    void hideDescription_PosTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(2L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);

        carDescriptionListener.hideDescription(update, 1L);

        assertThat(messageRepository.findByChatIdAndCarId(2L, 1L))
            .extracting(Message::getDescriptionState)
            .isEqualTo(DEFAULT);

        verify(telegramClient).execute(any(EditMessageText.class));
    }

    @Test
    void throwOnMissingCarDuringHideDescription_NegTC() throws TelegramApiException {
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(2L);
        when(update.getCallbackQuery().getMessage().getMessageId()).thenReturn(1001);
        when(update.getCallbackQuery().getId()).thenReturn("1");

        carDescriptionListener.hideDescription(update, 12345L);

        assertThat(messageRepository.findByChatIdAndCarId(2L, 1L))
            .extracting(Message::getDescriptionState)
            .isEqualTo(DESCRIPTION);

        verify(telegramClient).execute(any(AnswerCallbackQuery.class));
        verify(telegramClient, never()).execute(any(EditMessageText.class));
    }

}