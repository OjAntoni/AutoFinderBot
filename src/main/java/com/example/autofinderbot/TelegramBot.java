package com.example.autofinderbot;

import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.telegram.StrategyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            //todo test purpose + add catching throwable
            strategyContext.executeStrategy(message_text, update);

//            long chat_id = update.getMessage().getChatId();
//            chatId = chat_id;
//
//            SendMessage message = SendMessage // Create a message object
//                    .builder()
//                    .chatId(chat_id)
//                    .text(message_text)
//                    .build();
//            try {
//                telegramClient.execute(message); // Sending our message object to user
//            } catch (TelegramApiException e) {
//                logger.error(e);
//            }
        }
    }

    public void sendAll(List<String> messages) {
        for (String m : messages) {
            SendMessage message = SendMessage // Create a message object
                    .builder()
                    .text(m)
                    .chatId(chatId)
                    .build();
            try {
                telegramClient.execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                logger.error(e);
            }
        }
    }
}
