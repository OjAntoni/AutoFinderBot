package com.example.autofinderbot.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition that checks if the application is NOT running in web-only mode.
 * This condition is the opposite of WebOnlyCondition.
 */
public class TelegramRelatedCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !context.getEnvironment().containsProperty("web-only");
    }
}