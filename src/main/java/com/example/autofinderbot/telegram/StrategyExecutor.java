package com.example.autofinderbot.telegram;

import com.example.autofinderbot.config.TelegramRelated;
import com.example.autofinderbot.telegram.exception.TelegramBotException;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@TelegramRelated
@RequiredArgsConstructor
public class StrategyExecutor {
    private final ApplicationContext applicationContext;
    private final Logger logger;
    private final TelegramClient telegramClient;
    private final Map<String, MethodHandle> strategies = new HashMap<>();
    private final Map<String, Method> methodMap = new HashMap<>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public void initializeStrategies() {
        if (initialized.get()) return;

        synchronized (this) {
            if (initialized.get()) return;

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            String[] beanNames = applicationContext.getBeanDefinitionNames();

            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);
                Method[] methods = bean.getClass().getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(CommandListener.class)) {
                        CommandListener annotation = method.getAnnotation(CommandListener.class);
                        try {
                            MethodHandle handle = lookup.unreflect(method).bindTo(bean);
                            strategies.put(annotation.value(), handle);
                            methodMap.put(annotation.value(), method);
                        } catch (IllegalAccessException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
            initialized.set(true);
        }
    }

    public Method getMethod(String strategyName) {
        initializeStrategies();
        return methodMap.get(strategyName);
    }

    @SneakyThrows
    public void execute(String strategyName, Object[] args, Update update, Long chatId) {
        initializeStrategies();

        MethodHandle handle = strategies.get(strategyName);

        if (handle == null) {
            logger.error("Telegram command not found: " + strategyName);
            return;
        }

        try {
            handle.invokeWithArguments(args);
        } catch (TelegramBotException e) {
            SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(e.getMessage())
                .disableWebPagePreview(true)
                .build();
            telegramClient.execute(sendMessage);
        } catch (Throwable e) {
            logger.error(e);
        }
    }
}
