package com.example.autofinderbot.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Logger {

    private String getStackTraceInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[3];
        return format("%s:%d", caller.getClassName(), caller.getLineNumber());
    }

    public void info(String message, Object... args) {
        log.info("[{}] - {}", getStackTraceInfo(), format(message, args));
    }

    public void warn(String message, Object... args) {
        log.warn("[{}] - {}", getStackTraceInfo(), format(message, args));
    }

    public void error(String message, Throwable exception, Object... args) {
        log.error("[{}] - {}", getStackTraceInfo(), format(message, args), exception);
    }

    public void error(Throwable exception, Object... args) {
        log.error("[{}] - {}", getStackTraceInfo(), format(exception.getMessage(), args), exception);
    }

    public void error(String message, Object... args) {
        log.error("[{}] - {}", getStackTraceInfo(), format(message, args));
    }

    public void debug(String message, Object... args) {
        log.debug("[{}] - {}", getStackTraceInfo(), format(message, args));
    }

    public void trace(String message, Object... args) {
        log.trace("[{}] - {}", getStackTraceInfo(), format(message, args));
    }

    private String format(String text, Object... args) {
        if(args.length == 0) {
            return text;
        }
        return String.format(text, args);
    }
}
