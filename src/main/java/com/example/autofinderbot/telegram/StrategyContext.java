package com.example.autofinderbot.telegram;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.shared.DateTimeUtil;
import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.telegram.converter.CallbackDataConverter;
import com.example.autofinderbot.telegram.exception.InvalidCommandParameters;
import com.example.autofinderbot.telegram.exception.TelegramBotException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class StrategyContext {

    private final ApplicationContext applicationContext;
    private final Map<String, Method> methodMap = new HashMap<>();
    private final Map<String, MethodHandle> strategies = new HashMap<>();
    private final Logger logger;
    private final UserService userService;
    private final TelegramClient telegramClient;
    private final CallbackDataConverter callbackDataConverter;
    private final DateTimeUtil dateTimeUtil;

    @SneakyThrows
    public void executeStrategy(Update update) {
        if (strategies.isEmpty()) {
            initializeStrategies();
        }

        User user;
        long chatId;
        String input;
        String[] parts;
        String strategyName;
        String[] args = null;
        Map<String, Object> params = null;

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            user = userService.findByChatId(chatId);
            input = update.getCallbackQuery().getData();
            params = callbackDataConverter.convert(input);
            strategyName = (String) params.remove("tg_c");
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            user = userService.findByChatId(chatId);
            input = update.getMessage().getText();
            parts = input.split("\\s+");

            if (user != null && user.getRedirectTo() != null) {
                strategyName = user.getRedirectTo();
                args = parts;
            } else {
                strategyName = parts[0];
                args = Arrays.copyOfRange(parts, 1, parts.length);
            }
        } else {
            logger.debug("Update has no message or callback query");
            return;
        }

        user = getUser(user, update, chatId);

        MethodHandle handle = strategies.get(strategyName);
        Method method = methodMap.get(strategyName);
        boolean telegramBotExceptionOccurred = false;

        if(handle == null) {
            logger.error("Telegram command not found: " + strategyName);
        }

        if (handle != null && method != null) {
            try {
                if(params != null) {
                    Object[] parsedArgs = parseArguments(method, params, update);
                    handle.invokeWithArguments(parsedArgs);
                    return;
                } else {
                    Object[] parsedArgs = parseArguments(method, args, update);
                    handle.invokeWithArguments(parsedArgs);
                    return;
                }
            } catch (ClassCastException | WrongMethodTypeException | IllegalArgumentException e) {
                logger.error(e);
            } catch (TelegramBotException e) {
                telegramBotExceptionOccurred = true;
                if(user != null) {
                    SendMessage sendMessage = SendMessage.builder()
                        .chatId(user.getChatId())
                        .text(e.getMessage())
                        .disableWebPagePreview(true)
                        .build();
                    telegramClient.execute(sendMessage);
                } else {
                    logger.error(e);
                }
            }
        }

        if(!telegramBotExceptionOccurred) {
            logger.debug("Telegram command not found: " + strategyName);
            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .messageId(update.getMessage().getMessageId())
                    .build();
            telegramClient.execute(deleteMessage);
        }
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
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    private Object[] parseArguments(Method method, String[] args, Update update) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Class<?>> nonTelegramParameters = Arrays.stream(parameterTypes).filter(clas -> clas != Update.class).toList();

        if (nonTelegramParameters.size() > args.length) {
            throw new InvalidCommandParameters();
        }

        int argsN = 0;
        Object[] parsedArgs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if(parameterTypes[i] == Update.class) {
                parsedArgs[i] = update;
                continue;
            }
            parsedArgs[i] = convertArgument(parameterTypes[i], args[argsN]);
            argsN++;
        }
        return parsedArgs;
    }

    private Object[] parseArguments(Method method, Map<String, Object> params, Update update) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<String> parameterNames = Arrays.stream(method.getParameters()).map(Parameter::getName).toList();
        List<Class<?>> nonTelegramParameters = Arrays.stream(parameterTypes).filter(clas -> clas != Update.class).toList();

        if (nonTelegramParameters.size() > params.size()) {
            throw new InvalidCommandParameters();
        }

        Object[] parsedArgs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if(parameterTypes[i] == Update.class) {
                parsedArgs[i] = update;
                continue;
            }
            parsedArgs[i] = convertArgument(parameterTypes[i], params.get(parameterNames.get(i)).toString());
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
            } else if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(arg);
            } else {
                return arg;
            }
        } catch (NumberFormatException e) {
            logger.error(e);
            throw new IllegalArgumentException("Invalid argument type for: " + arg);
        }
    }

    private User getUser(User user, Update update, long chatId) {
        if (user == null) {
            user = new User();
            user.setChatId(chatId);
        }
        if (update.hasCallbackQuery()) {
            populateUserData(update.getCallbackQuery(), user);
        } else if (update.hasMessage()) {
            populateUserData(update.getMessage(), user);
        }
        user.setLastActive(dateTimeUtil.now());
        return userService.save(user);
    }

    private void populateUserData(CallbackQuery callbackQuery, User user) {
        user.setFirstname(callbackQuery.getFrom().getFirstName());
        user.setLastname(callbackQuery.getFrom().getLastName());
        user.setUsername(callbackQuery.getFrom().getUserName());
        user.setLanguageCode(callbackQuery.getFrom().getLanguageCode());
    }

    private void populateUserData(Message message, User user) {
        user.setFirstname(message.getFrom().getFirstName());
        user.setLastname(message.getFrom().getLastName());
        user.setUsername(message.getFrom().getUserName());
        user.setLanguageCode(message.getFrom().getLanguageCode());
    }
}
