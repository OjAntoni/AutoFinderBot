package com.example.autofinderbot.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class StrategyContext {

    private final ApplicationContext applicationContext;

    private final Map<String, MethodHandle> strategies = new HashMap<>();

    @Autowired
    public StrategyContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object executeStrategy(String strategyName, Object... args) {
        MethodHandle handle = strategies.get(strategyName);
        if (handle != null) {
            try {
                return handle.invokeWithArguments(args);
            } catch (Throwable e) {
                throw new RuntimeException("Error invoking strategy: " + strategyName, e);
            }
        }

        // Lazy initialization of strategies
        initializeStrategies();
        handle = strategies.get(strategyName);
        if (handle != null) {
            try {
                return handle.invokeWithArguments(args);
            } catch (Throwable e) {
                throw new RuntimeException("Error invoking strategy: " + strategyName, e);
            }
        }

        throw new IllegalArgumentException("No strategy found for: " + strategyName);
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
                        throw new RuntimeException("Failed to bind method: " + method.getName(), e);
                    }
                }
            }
        }
    }
}

