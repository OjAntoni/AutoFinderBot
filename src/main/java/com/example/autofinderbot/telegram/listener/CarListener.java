package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.common.config.telegram.components.TelegramRelated;
import com.example.autofinderbot.domain.SelectedCar;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.parser.service.DocumentService;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.shared.RemoveSelectedCarEvent;
import com.example.autofinderbot.common.config.telegram.listener.CommandListener;
import com.example.autofinderbot.telegram.converter.CarMenuKeyboardConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.autofinderbot.common.config.telegram.listener.CommandPath.*;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@TelegramRelated
public class CarListener {
    UserService userService;
    TelegramClient telegramClient;
    Logger logger;
    DocumentService<Document> documentService;
    CarMenuKeyboardConverter carMenuKeyboardConverter;

    @SneakyThrows
    @CommandListener(LIKE_CAR)
    public void likeCar(Update update, long car) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        User user = userService.findByChatId(chatId);

        try {
            userService.likeCar(user, car, messageId);
        } catch (Exception e) {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Unable to find this car ❌")
                .build();
            telegramClient.execute(answer);
            return;
        }

        EditMessageReplyMarkup editMessage = EditMessageReplyMarkup.builder()
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .chatId(update.getCallbackQuery().getMessage().getChatId())
            .replyMarkup(carMenuKeyboardConverter.menuKeyboard(user, car))
            .build();
        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
            .callbackQueryId(update.getCallbackQuery().getId())
            .text("Car was added to favorites ✅")
            .build();

        telegramClient.execute(editMessage);
        telegramClient.execute(answer);
    }

    @EventListener
    public void dislikeOutboundCar(RemoveSelectedCarEvent event) {
        User user = userService.findByChatId(event.getChatId());

        EditMessageReplyMarkup editMessage = EditMessageReplyMarkup.builder()
            .messageId(event.getMessageId())
            .chatId(event.getChatId())
            .replyMarkup(carMenuKeyboardConverter.menuKeyboard(user, event.getCarId()))
            .build();
        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            logger.debug("Unable to edit message", e);
        }
    }

    @SneakyThrows
    @CommandListener(DISLIKE_CAR)
    public void dislikeCar(Update update, long car) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        User user = userService.findByChatId(chatId);

        try {
            userService.dislikeCar(user, car);
        } catch (Exception e) {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Unable to find this car ❌")
                .build();
            telegramClient.execute(answer);
            return;
        }

        EditMessageReplyMarkup editMessage = EditMessageReplyMarkup.builder()
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .chatId(update.getCallbackQuery().getMessage().getChatId())
            .replyMarkup(carMenuKeyboardConverter.menuKeyboard(user, car))
            .build();
        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
            .callbackQueryId(update.getCallbackQuery().getId())
            .text("Car was removed from favorites ✅")
            .build();

        telegramClient.execute(editMessage);
        telegramClient.execute(answer);
    }

    @SneakyThrows
    @CommandListener(SELECTED_CARS)
    public void showSelectedCars(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = userService.findByChatId(chatId);
        List<SelectedCar> cars = userService.findSelectedCars(user);

        if (cars.isEmpty()) {
            SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("You have no favorite cars. \uD83E\uDD7A \n" +
                    "Please like any car to add it to your list.")
                .build();
            telegramClient.execute(sendMessage);
            return;
        }

        Map<Long, Boolean> carIdToState = new ConcurrentHashMap<>();
        cars.forEach(car -> carIdToState.put(car.getCarId(), false));

        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
            List<CompletableFuture<Void>> futures = cars.stream()
                .map(car -> {
                    Long carId = car.getCarId(); // Capture the ID explicitly
                    return CompletableFuture.supplyAsync(() -> documentService.isValid(car.getUrl()), executor)
                        .thenAccept(result -> carIdToState.put(carId, result)); // Use captured carId
                })
                .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error("Error in processing cars", e);
        }

        StringBuilder message = new StringBuilder();

        int i = 1;
        for (SelectedCar car : cars) {
            message.append("%s. [%s](%s) ".formatted(i++, car.getCarName(), car.getUrl())).append(carIdToState.get(car.getCarId()) ? "✅" : "❌").append("\n");
        }
        SendMessage sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(message.toString())
            .parseMode("Markdown")
            .disableWebPagePreview(true)
            .build();
        telegramClient.execute(sendMessage);
    }
}
