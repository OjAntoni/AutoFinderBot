package com.example.autofinderbot.common.config.telegram.listener;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.domain.UserHistory;
import com.example.autofinderbot.repository.UserHistoryRepository;
import com.example.autofinderbot.repository.UserRepository;
import com.example.autofinderbot.telegram.exception.InvalidCommandParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class StrategyContextTest extends BaseTelegramListenerTest {
    @Autowired
    StrategyContext strategyContext;

    @MockitoSpyBean
    TestListenerBean testListenerBean;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @BeforeEach
    void setUp() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
    }

    @Test
    void invokeCommand_PosTC() {
        when(update.getMessage().getChatId()).thenReturn(35L);
        when(update.getMessage().getText()).thenReturn("test arg");
        when(update.getMessage().getFrom().getUserName()).thenReturn("username");
        when(update.getMessage().getFrom().getFirstName()).thenReturn("firstname");
        when(update.getMessage().getFrom().getLastName()).thenReturn("lastname");
        when(update.getMessage().getFrom().getLanguageCode()).thenReturn("en");

        strategyContext.executeStrategy(update);

        verify(testListenerBean).test(ArgumentMatchers.any());

        assertThat(userRepository.getByChatId(35L))
            .extracting(
                User::getFirstname,
                User::getLastname,
                User::getUsername,
                User::getLanguageCode
            ).containsExactly(
                "firstname",
                "lastname",
                "username",
                "en");

        assertThat(userHistoryRepository.findAll())
            .filteredOn(userHistory -> userHistory.getChatId() == 35L)
            .hasSize(1)
            .element(0)
            .extracting(
                UserHistory::getChatId,
                UserHistory::getRedirectTo,
                UserHistory::getFirstname,
                UserHistory::getLastname,
                UserHistory::getUsername,
                UserHistory::getLanguageCode,
                UserHistory::getCommand,
                UserHistory::getData
            ).containsExactly(
                35L,
                null,
                "firstname",
                "lastname",
                "username",
                "en",
                "test",
                "test arg");
    }

    @Test
    void catchInnerException_PosTC() {
        when(update.getMessage().getText()).thenReturn("exception");

        assertDoesNotThrow(() -> strategyContext.executeStrategy(update));
    }

    @Test
    void catchTelegramException_PosTC() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("telegramException");

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

        strategyContext.executeStrategy(update);

        verify(testListenerBean).update(update);
    }

    @Test
    void invokeWithMoreArguments_PosTC() throws TelegramApiException {
        when(update.getMessage().getText()).thenReturn("test2 43 56 text");

        strategyContext.executeStrategy(update);

        verify(testListenerBean).test2(43);
        verify(telegramClient, never()).execute(any(SendMessage.class));
    }

    @Test
    void invokeWithLessArguments_NegTC(){
        when(update.getMessage().getText()).thenReturn("test2");

        verify(testListenerBean, never()).test2(anyInt());
        assertThatThrownBy(() -> strategyContext.executeStrategy(update))
            .isInstanceOf(InvalidCommandParameters.class);
    }

    @Test
    void invokeForCallback_PostTC() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("tg_c=callback;id=1");

        strategyContext.executeStrategy(update);

        verify(testListenerBean).callback(eq(1L), any(Update.class));
    }

    @Test
    void invokeForCallbackWithSeveralParameters_PostTC() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("tg_c=callback2;text=example;id=1");

        strategyContext.executeStrategy(update);

        verify(testListenerBean).callback(eq(1L), any(Update.class), eq("example"));
    }

    @Test
    void invokeForUser_PosTC() {
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getText()).thenReturn("user");
        when(update.getMessage().getFrom().getUserName()).thenReturn("username");
        when(update.getMessage().getFrom().getFirstName()).thenReturn("firstname");
        when(update.getMessage().getFrom().getLastName()).thenReturn("lastname");
        when(update.getMessage().getFrom().getLanguageCode()).thenReturn("pl");

        strategyContext.executeStrategy(update);

        verify(testListenerBean).user(argThat(user -> {
            assertThat(user)
                .extracting(
                    User::getChatId,
                    User::getUsername,
                    User::getFirstname,
                    User::getLastname,
                    User::getLanguageCode
                ).containsExactly(
                    1L,
                    "username",
                    "firstname",
                    "lastname",
                    "pl"
                );
            return true;
        }));
    }

    @Test
    void invokeWithRedirection() {
        when(update.getMessage().getChatId()).thenReturn(7L);
        when(update.getMessage().getText()).thenReturn("Test 25");

        strategyContext.executeStrategy(update);

        verify(testListenerBean).redirection(any(User.class), eq("Test"), eq(25L));
    }
}