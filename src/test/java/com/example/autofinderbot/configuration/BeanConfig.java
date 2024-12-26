package com.example.autofinderbot.configuration;

import com.example.autofinderbot.TelegramBot;
import com.example.autofinderbot.parser.CarParserService;
import com.example.autofinderbot.repository.CarFileRepository;
import com.example.autofinderbot.service.ScheduledExecutor;
import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.telegram.TestListenerBean;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BeanConfig {

    @Bean
    ScheduledExecutor scheduledExecutor(
        CarParserService carParserService,
        CarFileRepository carFileRepository,
        Logger logger
    ) {
        return new ScheduledExecutor(carParserService, carFileRepository, Mockito.mock(TelegramBot.class), logger);
    }

    @Bean
    TestListenerBean testListenerBean() {
        return Mockito.mock(TestListenerBean.class);
    }
}
