package com.example.autofinderbot.telegram;

import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.telegram.exception.InvalidArgumentsException;
import com.example.autofinderbot.telegram.exception.TelegramCommandNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StrategyContext {

    private final ApplicationContext applicationContext;

    private final Map<String, MethodHandle> strategies = new HashMap<>();

    private final Logger logger;

    @SneakyThrows
    public Object executeStrategy(String strategyName, Object... args) {
        if(strategies.isEmpty()) {
            initializeStrategies();
        }

        MethodHandle handle = strategies.get(strategyName);
        if (handle != null) {
            try {
                return handle.invokeWithArguments(args);
            } catch (ClassCastException | WrongMethodTypeException e) {
                throw new InvalidArgumentsException(strategyName);
            }
        }

        throw new TelegramCommandNotFoundException(strategyName);
    }

    private synchronized void initializeStrategies() {
        if (!strategies.isEmpty()) return;

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
                    } catch (IllegalAccessException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }
}

