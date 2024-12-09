package com.example.autofinderbot.shared;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class LoggerTest extends BaseSpringBootTest {
    @Autowired
    Logger logger;

    @Test
    void log_PosTC() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = context.getLogger("ROOT");
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        rootLogger.addAppender(listAppender);

        logger.info("Test message");

        ILoggingEvent logEvent = listAppender.list.getFirst();
        String formattedMessage = logEvent.getFormattedMessage();

        assertThat(formattedMessage).isEqualTo("[com.example.autofinderbot.shared.LoggerTest:25] - Test message");
        assertThat(logEvent.getLevel().toString()).isEqualTo("INFO");
    }
}