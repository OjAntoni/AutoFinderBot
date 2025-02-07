package com.example.autofinderbot.telegram;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.telegram.converter.CallbackDataConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.lang.reflect.Method;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StrategyContext {
    private final StrategyUserProvider strategyUserProvider;
    private final Logger logger;
    private final StrategyExecutor strategyExecutor;
    private final CallbackDataConverter callbackDataConverter;
    private final StrategyArgumentParser strategyArgumentParser;
    private final TelegramClient telegramClient;

    @SneakyThrows
    public void executeStrategy(Update update) {
        long chatId;
        String strategyName;
        String[] args = null;
        Map<String, Object> params = null;

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            params = callbackDataConverter.convert(update.getCallbackQuery().getData());
            strategyName = (String) params.remove("tg_c");
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            String input = update.getMessage().getText();
            String[] parts = input.split("\\s+");
            strategyName = parts[0];
            args = parts.length > 1 ? new String[parts.length - 1] : new String[0];
            System.arraycopy(parts, 1, args, 0, args.length);
        } else {
            logger.debug("Update has no message or callback query");
            return;
        }

        User user = strategyUserProvider.getOrCreateUser(chatId, update);

        Method method = strategyExecutor.getMethod(strategyName);
        if (method == null) {
            logger.debug("Telegram command not found: " + strategyName);
            DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(update.getMessage().getChatId())
                .messageId(update.getMessage().getMessageId())
                .build();
            telegramClient.execute(deleteMessage);
            return;
        }

        Object[] parsedArgs = params != null ? strategyArgumentParser.parseArguments(method, params, update)
            : strategyArgumentParser.parseArguments(method, args, update);

        strategyExecutor.execute(strategyName, parsedArgs, update, chatId);
    }
}
