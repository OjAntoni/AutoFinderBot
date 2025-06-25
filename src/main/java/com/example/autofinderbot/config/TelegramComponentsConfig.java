package com.example.autofinderbot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that conditionally loads all Telegram-related components.
 * These components are only loaded when the application is not running in web-only mode.
 */
@Configuration
@ConditionalOnNotWebOnly
@ComponentScan(basePackages = {
    "com.example.autofinderbot.telegram",
    "com.example.autofinderbot.telegram.converter",
    "com.example.autofinderbot.telegram.listener"
})
public class TelegramComponentsConfig {
    // This class doesn't need any methods, it just serves as a container for the component scan configuration
}