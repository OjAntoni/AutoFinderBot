package com.example.autofinderbot.configuration;

import com.example.autofinderbot.config.TelegramRelatedCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration that ensures Telegram components are loaded during tests.
 * This configuration overrides the NotWebOnlyCondition to always return true in test environment.
 */
@Configuration
@Profile("test")
public class TestTelegramConfig {

    /**
     * Override the NotWebOnlyCondition to always return true in test environment.
     * This ensures that Telegram components are always loaded during tests.
     */
    @Bean
    public TelegramRelatedCondition notWebOnlyCondition() {
        return new TelegramRelatedCondition() {
            @Override
            public boolean matches(org.springframework.context.annotation.ConditionContext context, 
                                  org.springframework.core.type.AnnotatedTypeMetadata metadata) {
                return true; // Always return true in test environment
            }
        };
    }
}