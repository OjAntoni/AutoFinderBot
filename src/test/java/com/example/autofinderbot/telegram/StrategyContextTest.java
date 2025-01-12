package com.example.autofinderbot.telegram;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.telegram.exception.InvalidArgumentsException;
import com.example.autofinderbot.telegram.exception.TelegramCommandNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void invokeCommandWithMissingArguments_NegTC() {
        when(update.getMessage().getText()).thenReturn("test");

        assertThatThrownBy(() -> strategyContext.executeStrategy(update))
                .isInstanceOf(InvalidArgumentsException.class)
                .hasMessage("Invalid arguments for command test.");
    }

    @Test
    void throwOnMissingCommand_NegTC() {
        when(update.getMessage().getText()).thenReturn("testABCD");

        assertThatThrownBy(() -> strategyContext.executeStrategy(update))
            .isInstanceOf(TelegramCommandNotFoundException.class)
            .hasMessage("Command testABCD not found.");
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
    void invokeWithUpdateInstance() {
        when(update.getMessage().getText()).thenReturn("update");
        doCallRealMethod().when(testListenerBean).update(any());

        strategyContext.executeStrategy(update);

        verify(testListenerBean).update(update);
    }
}