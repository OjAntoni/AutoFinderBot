package com.example.autofinderbot;

import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.telegram.StrategyContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Profile("!test")
@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String botToken;
    private long chatId;
    private final Logger logger;
    //todo test purpose
    @Autowired
    StrategyContext strategyContext;

    public TelegramBot(@Value("${telegram.bot.token}") String token, Logger logger, TelegramClient telegramClient) {
        botToken = token;
        this.telegramClient = telegramClient;
        this.logger = logger;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if(update.getMyChatMember() != null ) {
            //TODO handle blocking bot (status = kicked/member)
            logger.debug(update.getMyChatMember().getNewChatMember().getStatus());
        }
        strategyContext.executeStrategy(update);
    }
}
