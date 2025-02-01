package com.example.autofinderbot.telegram.converter;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Message;
import com.example.autofinderbot.domain.Message.State;
import com.example.autofinderbot.domain.Seller;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.service.CarService;
import com.example.autofinderbot.service.MessageService;
import com.example.autofinderbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.List;

import static com.example.autofinderbot.domain.Message.State.DESCRIPTION;
import static com.example.autofinderbot.telegram.converter.Parameter.of;
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

    public InlineKeyboardMarkup menuKeyboard(User user, long carId) {
        Car car = carService.findById(carId);
        return menuKeyboard(user, car);
    }

    public InlineKeyboardMarkup menuKeyboard(User user, Car car) {
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

        State messageState = messageService.getMessageState(user.getChatId(), car.getId());
        if (messageState == DESCRIPTION) {
            InlineKeyboardButton hideDescriptionButton = InlineKeyboardButton.builder()
                .text("\uD83D\uDE48")
                .callbackData(callbackDataConverter.convert("/hide_description", of("car", car.getId())))
                .build();
            buttons.add(hideDescriptionButton);
        } else {
            InlineKeyboardButton showDescriptionButton = InlineKeyboardButton.builder()
                .text("\uD83E\uDDD0")
                .callbackData(callbackDataConverter.convert("/show_description", of("car", car.getId())))
                .build();
            buttons.add(showDescriptionButton);
        }

        boolean isLiked = userService.findSelectedCars(user).stream().anyMatch(sc -> sc.getCarId() == car.getId());
        InlineKeyboardButton likeButton = InlineKeyboardButton.builder()
            .text(isLiked ? "\uD83D\uDC4E" : "❤")
            .callbackData(callbackDataConverter.convert(isLiked ? "/car_dislike" : "/car_like", of("car", car.getId())))
            .build();
        buttons.add(likeButton);

        return InlineKeyboardMarkup.builder()
            .keyboardRow(
                new InlineKeyboardRow(buttons)
            ).build();
    }
}
