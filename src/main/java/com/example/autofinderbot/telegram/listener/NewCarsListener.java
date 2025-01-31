package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.UserFilter;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.shared.NewCarsEvent;
import com.example.autofinderbot.telegram.converter.CallbackDataConverter;
import com.example.autofinderbot.telegram.converter.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static com.example.autofinderbot.telegram.converter.Parameter.of;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NewCarsListener {
    UserService userService;
    TelegramClient telegramClient;
    Logger logger;
    CallbackDataConverter callbackConverter;

    @EventListener
    public void handleEvent(NewCarsEvent event) {
        List<Car> cars = event.getCars();
        //TODO refactor when user will be able to have several active filters
        List<UserFilter> userFilters = userService.findAllActiveUserFilters();

        for (UserFilter filter : userFilters) {
            List<SendMessage> messages = cars.stream()
                    .filter(car -> userService.matches(filter, car))
                    .map(car -> convert(car, filter))
                    .toList();
            logger.info("Filtered out %s cars for user with id %d.", messages.size(), filter.getUser().getId());
            for (SendMessage message : messages) {
                try {
                    telegramClient.executeAsync(message);
                } catch (TelegramApiException e) {
                    logger.error(e);
                }
            }
        }
    }

    private SendMessage convert(Car car, UserFilter filter) {
        return SendMessage.builder()
            .text(car.getUrl())
            .chatId(filter.getUser().getChatId())
            .replyMarkup(
                InlineKeyboardMarkup.builder()
                    .keyboardRow(
                        new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                .text("❤")
                                .callbackData(callbackConverter.convert("/car_like", of("car", car.getId())))
                                .build()
                        )
                    ).build())
            .build();
    }
}
