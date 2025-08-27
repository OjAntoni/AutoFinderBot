package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.common.config.telegram.components.TelegramRelated;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.user.User;
import com.example.autofinderbot.service.CarService;
import com.example.autofinderbot.service.MessageService;
import com.example.autofinderbot.user.UserService;
import com.example.autofinderbot.common.config.telegram.listener.CommandListener;
import com.example.autofinderbot.telegram.converter.CarDetailsConverter;
import com.example.autofinderbot.telegram.converter.CarMenuKeyboardConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static com.example.autofinderbot.common.config.telegram.listener.CommandPath.HIDE_DETAILS;
import static com.example.autofinderbot.common.config.telegram.listener.CommandPath.SHOW_DETAILS;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@TelegramRelated
public class CarDetailsListener {
    TelegramClient telegramClient;
    UserService userService;
    MessageService messageService;
    CarMenuKeyboardConverter carMenuKeyboardConverter;
    CarDetailsConverter carDetailsConverter;
    CarService carService;

    @SneakyThrows
    @CommandListener(SHOW_DETAILS)
    public void showDetails(Update update, long car) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        User user = userService.findByChatId(chatId);
        Car carById = carService.findById(car);

        if(carById == null) {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Unable to find this car ❌")
                .build();
            telegramClient.execute(answer);
            return;
        }

        messageService.triggerDetails(user, car);

        EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(carById.getUrl() +"\n\n" + carDetailsConverter.convert(carById))
            .replyMarkup(carMenuKeyboardConverter.menuKeyboard(user, carById))
            .build();

        telegramClient.execute(editMessageText);
    }

    @SneakyThrows
    @CommandListener(HIDE_DETAILS)
    public void hideDetails(Update update, long car) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        User user = userService.findByChatId(chatId);
        Car carById = carService.findById(car);

        if(carById == null) {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Unable to find this car ❌")
                .build();
            telegramClient.execute(answer);
            return;
        }

        messageService.triggerDetails(user, car);

        EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(carById.getUrl())
            .replyMarkup(carMenuKeyboardConverter.menuKeyboard(user, carById))
            .build();

        telegramClient.execute(editMessageText);
    }
}
