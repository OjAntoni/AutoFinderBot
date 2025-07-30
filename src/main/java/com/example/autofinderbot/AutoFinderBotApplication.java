package com.example.autofinderbot;

import com.example.autofinderbot.shared.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main application class for AutoFinderBot.
 * <p>
 * The application can be started in two modes:
 * 1. Normal mode: All components including Telegram bot are loaded
 * 2. Web-only mode: Only web components are loaded, Telegram bot is disabled
 * <p>
 * To start in web-only mode, pass the --web-only flag as a command line argument.
 */
@SpringBootApplication
public class AutoFinderBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AutoFinderBotApplication.class, args);

        ApplicationArguments appArgs = context.getBean(ApplicationArguments.class);
        boolean webOnly = appArgs.containsOption("web-only");

        Logger logger = context.getBean(Logger.class);
        if (webOnly) {
            logger.info("Running in web-only mode. Telegram bot is disabled.");
        } else {
            logger.info("Running in normal mode. All components including Telegram bot are loaded.");
        }
    }

}
