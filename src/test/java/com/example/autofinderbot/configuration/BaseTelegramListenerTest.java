package com.example.autofinderbot.configuration;

import com.example.autofinderbot.telegram.StrategyContext;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BaseTelegramListenerTest extends BaseSpringBootTest {
    @Autowired
    protected StrategyContext strategyContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    protected Update update;
}
