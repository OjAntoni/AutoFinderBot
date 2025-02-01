package com.example.autofinderbot.telegram;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class StrategyContextTest extends BaseTelegramListenerTest {
    @Autowired
    StrategyContext strategyContext;

    @MockBean
    TestListenerBean testListenerBean;

    @BeforeEach
    void setUp() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
    }

    @Test
    void invokeCommand_PosTC() {
        when(update.getMessage().getText()).thenReturn("test  arg");

        strategyContext.executeStrategy(update);

        Mockito.verify(testListenerBean).test(ArgumentMatchers.any());
    }

    @Test
    void catchInnerException_PosTC() {
        when(update.getMessage().getText()).thenReturn("exception");
        doCallRealMethod().when(testListenerBean).exception();

        assertThatThrownBy(() -> strategyContext.executeStrategy(update))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test exception");
    }

    @Test
    void catchTelegramException_PosTC() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("telegramException");
        doCallRealMethod().when(testListenerBean).telegramException();

        strategyContext.executeStrategy(update);

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("Error occurred.");
        }));
    }

    @Test
    void deleteMessageWhenCommandNotFound_PosTC() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("notFound");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getMessageId()).thenReturn(123);

        strategyContext.executeStrategy(update);

        verify(telegramClient).execute((DeleteMessage) argThat(message -> {
            DeleteMessage m = (DeleteMessage) message;
            return m.getChatId().equals("1") && m.getMessageId() == 123;
        }));
    }

    @Test
    void invokeWithUpdateInstance_PosTC() {
        when(update.getMessage().getText()).thenReturn("update");
        doCallRealMethod().when(testListenerBean).update(any());

        strategyContext.executeStrategy(update);

        verify(testListenerBean).update(update);
    }

    @Test
    void invokeWithMoreArguments_PosTC() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("test2 43 56 text");

        doCallRealMethod().when(testListenerBean).test2(anyInt());

        strategyContext.executeStrategy(update);

        verify(testListenerBean).test2(43);
        verify(telegramClient, never()).execute(any(SendMessage.class));
    }

    @Test
    void invokeWithLessArguments_NegTC() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("test2");

        doCallRealMethod().when(testListenerBean).test2(anyInt());

        strategyContext.executeStrategy(update);

        verify(testListenerBean, never()).test2(anyInt());
        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("Invalid command parameters.");
        }));
    }

    @Test
    void invokeForCallback_PostTC() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("tg_c=callback;id=1");

        strategyContext.executeStrategy(update);

        Mockito.verify(testListenerBean).callback(eq(1L), any(Update.class));
    }

    @Test
    void invokeForCallbackWithSeveralParameters_PostTC() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("tg_c=callback2;text=example;id=1");

        strategyContext.executeStrategy(update);

        Mockito.verify(testListenerBean).callback(eq(1L), any(Update.class), eq("example"));
    }
}