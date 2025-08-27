package com.example.autofinderbot.car.converter;

import com.example.autofinderbot.car.Car;
import com.example.autofinderbot.car.message.Message.State;
import com.example.autofinderbot.car.seller.Seller;
import com.example.autofinderbot.common.config.telegram.listener.CallbackDataConverter;
import com.example.autofinderbot.user.User;
import com.example.autofinderbot.car.CarService;
import com.example.autofinderbot.car.message.MessageService;
import com.example.autofinderbot.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.example.autofinderbot.car.message.Message.State.DESCRIPTION;
import static com.example.autofinderbot.car.message.Message.State.DETAILS;
import static com.example.autofinderbot.common.config.telegram.listener.CommandPath.*;
import static com.example.autofinderbot.common.util.Parameter.of;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarMenuKeyboardConverter {
    GoogleMapsConverter googleMapsConverter;
    CallbackDataConverter callbackDataConverter;
    MessageService messageService;
    UserService userService;
    CarService carService;

    @Transactional(readOnly = true)
    public InlineKeyboardMarkup menuKeyboard(User user, long carId) {
        Car car = carService.findById(carId);
        return menuKeyboard(user, car);
    }

    public InlineKeyboardMarkup menuKeyboard(User user,  Car car) {
        if(car == null) {
            return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow())
                .build();
        }

        List<InlineKeyboardButton> buttons = new ArrayList<>();

        Seller seller = car.getSeller();
        if(seller != null && seller.getAddress() != null) {
            InlineKeyboardButton mapsButton = InlineKeyboardButton.builder()
                .text("\uD83C\uDF0D")
                .url(googleMapsConverter.url(seller.getAddress().getLatitude(), seller.getAddress().getLongitude()))
                .build();
            buttons.add(mapsButton);
        }

        State messageDescriptionState = messageService.getMessageDescriptionState(user.getChatId(), car.getId());
        if (messageDescriptionState == DESCRIPTION) {
            InlineKeyboardButton hideDescriptionButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDE48")
                .callbackData(callbackDataConverter.convert(HIDE_DESCRIPTION, of("car", car.getId())))
                .build();
            buttons.add(hideDescriptionButton);
        } else {
            InlineKeyboardButton showDescriptionButton = InlineKeyboardButton.builder()
                .text("\uD83E\uDDD0")
                .callbackData(callbackDataConverter.convert(SHOW_DESCRIPTION, of("car", car.getId())))
                .build();
            buttons.add(showDescriptionButton);
        }

        State messageDetailsState = messageService.getMessageDetailsState(user.getChatId(), car.getId());
        if (messageDetailsState == DETAILS) {
            InlineKeyboardButton hideDescriptionButton = InlineKeyboardButton.builder()
                .text("✏️")
                .callbackData(callbackDataConverter.convert(HIDE_DETAILS, of("car", car.getId())))
                .build();
            buttons.add(hideDescriptionButton);
        } else {
            InlineKeyboardButton hideDescriptionButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDCCA")
                .callbackData(callbackDataConverter.convert(SHOW_DETAILS, of("car", car.getId())))
                .build();
            buttons.add(hideDescriptionButton);
        }

        boolean isLiked = userService.findSelectedCars(user).stream().anyMatch(sc -> sc.getCarId() == car.getId());
        InlineKeyboardButton likeButton = InlineKeyboardButton.builder()
            .text(isLiked ? "\uD83D\uDC4E" : "❤")
            .callbackData(callbackDataConverter.convert(isLiked ? DISLIKE_CAR : LIKE_CAR, of("car", car.getId())))
            .build();
        buttons.add(likeButton);

        return InlineKeyboardMarkup.builder()
            .keyboardRow(
                new InlineKeyboardRow(buttons)
            ).build();
    }
}
