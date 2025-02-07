package com.example.autofinderbot;

import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.telegram.StrategyContext;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import static lombok.AccessLevel.PRIVATE;

@Profile("!test")
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    String botToken;
    Logger logger;
    StrategyContext strategyContext;

    public TelegramBot(@Value("${telegram.bot.token}") String token, Logger logger, StrategyContext strategyContext) {
        botToken = token;
        this.logger = logger;
        this.strategyContext = strategyContext;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if(update.getMyChatMember() != null ) {
            //TODO handle blocking bot (status = kicked/member)
            logger.debug(update.getMyChatMember().getNewChatMember().getStatus());
        }
        strategyContext.executeStrategy(update);
    }
}
