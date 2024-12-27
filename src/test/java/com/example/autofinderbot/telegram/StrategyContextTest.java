package com.example.autofinderbot.telegram;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.telegram.exception.InvalidArgumentsException;
import com.example.autofinderbot.telegram.exception.TelegramCommandNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class StrategyContextTest extends BaseSpringBootTest {
    @Autowired
    StrategyContext strategyContext;

    @MockBean
    TestListenerBean testListenerBean;

    Update update = new Update();

    @Test
    void invokeCommand_PosTC() {
        strategyContext.executeStrategy("test  arg", update);

        Mockito.verify(testListenerBean).test(ArgumentMatchers.any());
    }

    @Test
    void invokeCommandWithMissingArguments_PosTC() {
        assertThatThrownBy(() -> strategyContext.executeStrategy("test", update))
                .isInstanceOf(InvalidArgumentsException.class)
                .hasMessage("Invalid arguments for command test.");
    }

    @Test
    void invokeCommandWithReturn_PosTC() {
        when(testListenerBean.test2(anyInt())).thenCallRealMethod();

        Object returnObj = strategyContext.executeStrategy("test2 3", update);

        Mockito.verify(testListenerBean).test2(anyInt());
        assertThat(returnObj)
            .isEqualTo(9.0);
    }

    @Test
    void throwOnMissingCommand_NegTC() {
        assertThatThrownBy(() -> strategyContext.executeStrategy("missing arg", update))
            .isInstanceOf(TelegramCommandNotFoundException.class)
            .hasMessage("Command missing not found.");
    }

    @Test
    void catchInnerException_PosTC() {
        doCallRealMethod().when(testListenerBean).exception();

        assertThatThrownBy(() -> strategyContext.executeStrategy("exception", update))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test exception");
    }

    @Test
    void invokeWithUpdateInstance() {
        doCallRealMethod().when(testListenerBean).update(any());

        strategyContext.executeStrategy("update", update);
        
        verify(testListenerBean).update(update);
    }
}