package com.example.autofinderbot.telegram;

import com.example.autofinderbot.telegram.exception.InvalidCommandParameters;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StrategyArgumentParser {

    private final Logger logger;

    public Object[] parseArguments(Method method, String[] args, Update update) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Class<?>> nonTelegramParameters = Arrays.stream(parameterTypes)
            .filter(clas -> clas != Update.class)
            .toList();

        if (nonTelegramParameters.size() > args.length) {
            throw new InvalidCommandParameters();
        }

        int argsN = 0;
        Object[] parsedArgs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == Update.class) {
                parsedArgs[i] = update;
                continue;
            }
            parsedArgs[i] = convertArgument(parameterTypes[i], args[argsN]);
            argsN++;
        }
        return parsedArgs;
    }

    public Object[] parseArguments(Method method, Map<String, Object> params, Update update) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<String> parameterNames = Arrays.stream(method.getParameters())
            .map(Parameter::getName)
            .toList();

        List<Class<?>> nonTelegramParameters = Arrays.stream(parameterTypes)
            .filter(clas -> clas != Update.class)
            .toList();

        if (nonTelegramParameters.size() > params.size()) {
            throw new InvalidCommandParameters();
        }

        Object[] parsedArgs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == Update.class) {
                parsedArgs[i] = update;
                continue;
            }
            parsedArgs[i] = convertArgument(parameterTypes[i], params.get(parameterNames.get(i)).toString());
        }
        return parsedArgs;
    }

    private Object convertArgument(Class<?> targetType, String arg) {
        try {
            return switch (targetType.getSimpleName()) {
                case "int", "Integer" -> Integer.parseInt(arg);
                case "double", "Double" -> Double.parseDouble(arg);
                case "boolean", "Boolean" -> Boolean.parseBoolean(arg);
                case "long", "Long" -> Long.parseLong(arg);
                default -> arg;
            };
        } catch (NumberFormatException e) {
            logger.error(e);
            throw new IllegalArgumentException("Invalid argument type for: " + arg);
        }
    }
}
