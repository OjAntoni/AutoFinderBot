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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StrategyContext {

    private final ApplicationContext applicationContext;
    private final Map<String, Method> methodMap = new HashMap<>();
    private final Map<String, MethodHandle> strategies = new HashMap<>();
    private final Logger logger;

    @SneakyThrows
    public Object executeStrategy(String input) {
        if (strategies.isEmpty()) {
            initializeStrategies();
        }

        String[] parts = input.split("\\s+");
        String strategyName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        MethodHandle handle = strategies.get(strategyName);
        Method method = methodMap.get(strategyName);

        if (handle != null && method != null) {
            try {
                Object[] parsedArgs = parseArguments(method, args);
                return handle.invokeWithArguments(parsedArgs);
            } catch (ClassCastException | WrongMethodTypeException | IllegalArgumentException e) {
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
                        methodMap.put(annotation.value(), method);
                    } catch (IllegalAccessException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }

    private Object[] parseArguments(Method method, String[] args) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != args.length) {
            throw new IllegalArgumentException(method.getName());
        }

        Object[] parsedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            parsedArgs[i] = convertArgument(parameterTypes[i], args[i]);
        }
        return parsedArgs;
    }

    private Object convertArgument(Class<?> targetType, String arg) {
        try {
            if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(arg);
            } else if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(arg);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(arg);
            } else {
                return arg;
            }
        } catch (NumberFormatException e) {
            logger.error(e);
            throw new IllegalArgumentException("Invalid argument type for: " + arg);
        }
    }
}
