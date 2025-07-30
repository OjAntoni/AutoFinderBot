package com.example.autofinderbot.config;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * Annotation that indicates a component should only be loaded when the application
 * is NOT running in web-only mode (--web-only flag is not present).
 * This is used for Telegram-related components that should be excluded in web-only mode.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(TelegramRelatedCondition.class)
public @interface TelegramRelated {
}