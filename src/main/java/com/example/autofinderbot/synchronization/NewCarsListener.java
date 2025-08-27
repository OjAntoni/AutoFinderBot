package com.example.autofinderbot.synchronization;

import com.example.autofinderbot.common.config.telegram.components.TelegramRelated;
import com.example.autofinderbot.car.Car;
import com.example.autofinderbot.filter.UserFilter;
import com.example.autofinderbot.user.UserService;
import com.example.autofinderbot.common.util.Logger;
import com.example.autofinderbot.common.event.NewCarsEvent;
import com.example.autofinderbot.car.converter.CarMenuKeyboardConverter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@TelegramRelated
public class NewCarsListener {
    UserService userService;
    TelegramClient telegramClient;
    Logger logger;
    CarMenuKeyboardConverter carMenuKeyboardConverter;

    @EventListener
    //TODO place into synchronization module
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
                carMenuKeyboardConverter.menuKeyboard(filter.getUser(), car))
            .build();
    }
}
