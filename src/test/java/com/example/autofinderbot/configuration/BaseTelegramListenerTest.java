package com.example.autofinderbot.configuration;

import com.example.autofinderbot.telegram.StrategyContext;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class BaseTelegramListenerTest extends BaseSpringBootTest {
    @Autowired
    protected StrategyContext strategyContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    protected Update update;
    @MockBean
    protected TelegramClient telegramClient;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(update, telegramClient);
    }
}
