package com.example.autofinderbot.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Logger {

    private String getStackTraceInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[3];
        return String.format("%s:%d", caller.getClassName(), caller.getLineNumber());
    }

    public void info(String message, Object... args) {
        log.info("[{}] - {}", getStackTraceInfo(), String.format(message, args));
    }

    public void warn(String message, Object... args) {
        log.warn("[{}] - {}", getStackTraceInfo(), String.format(message, args));
    }

    public void error(String message, Throwable exception, Object... args) {
        log.error("[{}] - {}", getStackTraceInfo(), String.format(message, args), exception);
    }

    public void error(Throwable exception, Object... args) {
        log.error("[{}] - {}", getStackTraceInfo(), String.format(exception.getMessage(), args), exception);
    }

    public void error(String message, Object... args) {
        log.error("[{}] - {}", getStackTraceInfo(), String.format(message, args));
    }

    public void debug(String message, Object... args) {
        log.debug("[{}] - {}", getStackTraceInfo(), String.format(message, args));
    }

    public void trace(String message, Object... args) {
        log.trace("[{}] - {}", getStackTraceInfo(), String.format(message, args));
    }
}
