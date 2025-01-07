package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.service.CarFiltersService;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.telegram.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Component
public class UserListener {
    UserService userService;
    UserUrlValidator userUrlValidator;
    CarFiltersService carFiltersService;
    TelegramClient telegramClient;

    @SneakyThrows
    @CommandListener("/register")
    public void registerUser(Update update){
        List<CarBrand> brands = carFiltersService.getBrands();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .replyMarkup(createKeyboardWithRows())
                .text("Please choose an option:")
                .build();

        telegramClient.execute(sendMessage);

//        //TODO replace with real url
//        userUrlValidator.validate(SEARCH_URL);
//        User user = new User();
//        user.setChatId(update.getMessage().getChatId());
//        user.setSearchUrl(SEARCH_URL);
//        userService.save(user);
    }

    public InlineKeyboardMarkup createKeyboardWithRows() {
        // Create buttons
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("Toyota Gen1")
                .callbackData("brand_Toyota|generation_Gen1")
                .build();

        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("BMW Gen2")
                .callbackData("brand_BMW|generation_Gen2")
                .build();

        InlineKeyboardButton skipButton = InlineKeyboardButton.builder()
                .text("Skip")
                .callbackData("action_skip")
                .build();

        // Create rows
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        row1.add(button2);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(skipButton);

        // Set up the keyboard with rows
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(row1))
                .keyboardRow(new InlineKeyboardRow(row2))
                .build();
    }
}
