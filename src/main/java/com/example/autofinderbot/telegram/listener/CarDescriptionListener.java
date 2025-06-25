package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.config.ConditionalOnNotWebOnly;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.service.CarService;
import com.example.autofinderbot.service.MessageService;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.telegram.CommandListener;
import com.example.autofinderbot.telegram.converter.CarMenuKeyboardConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static com.example.autofinderbot.telegram.CommandPath.HIDE_DESCRIPTION;
import static com.example.autofinderbot.telegram.CommandPath.SHOW_DESCRIPTION;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@ConditionalOnNotWebOnly
public class CarDescriptionListener {
    TelegramClient telegramClient;
    UserService userService;
    MessageService messageService;
    CarMenuKeyboardConverter carMenuKeyboardConverter;
    CarService carService;

    @SneakyThrows
    @CommandListener(SHOW_DESCRIPTION)
    public void showDescription(Update update, long car) {
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

        messageService.triggerDescription(user, car);

        String description = carById.getDescription();

        EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(carById.getUrl() + "\n\n" + (description.length() > 3900 ? description.substring(0, 3900) + "..." : description))
            .replyMarkup(carMenuKeyboardConverter.menuKeyboard(user, car))
            .build();

        telegramClient.execute(editMessageText);
    }

    @SneakyThrows
    @CommandListener(HIDE_DESCRIPTION)
    public void hideDescription(Update update, long car){
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

        messageService.triggerDescription(user, car);

        EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(carById.getUrl())
            .replyMarkup(carMenuKeyboardConverter.menuKeyboard(user, car))
            .build();

        telegramClient.execute(editMessageText);
    }
}
