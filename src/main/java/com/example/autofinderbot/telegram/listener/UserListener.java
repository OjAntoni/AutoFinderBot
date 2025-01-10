package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.domain.UserFilter;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.telegram.CommandListener;
import com.example.autofinderbot.telegram.converter.UrlToFiltersConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;
import java.util.List;

import static com.example.autofinderbot.telegram.CommandPath.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Component
public class UserListener {
    UserService userService;
    UserUrlValidator userUrlValidator;
    UrlToFiltersConverter urlToFiltersConverter;
    UserRedirectedValidator userRedirectedValidator;
    TelegramClient telegramClient;

    @SneakyThrows
    @CommandListener("/start")
    public void registerUser(Update update) {
        Long chatId = update.getMessage().getChatId();

        if (userService.existsByChatId(chatId)) {
            return;
        }

        User user = new User();
        user.setChatId(chatId);

        userService.save(user);

        SendMessage message = new SendMessage(chatId.toString(),
                "Hi, %s! I am you car assistant that will help you to find your dream car ".formatted(update.getMessage().getFrom().getUserName()) +
                "in the fastest way possible. Just set up filter for yourself. That's all :)");
        telegramClient.execute(message);
    }

    @SneakyThrows
    @CommandListener(SET_FILTER)
    public void setFilter(Update update){
        Long chatId = update.getMessage().getChatId();
        User user = userService.findByChatId(chatId);

        InlineKeyboardButton webAppButton = InlineKeyboardButton.builder()
                .text("Open otomoto")
                .url("https://www.otomoto.pl/osobowe?search%5Badvanced_search_expanded%5D=true")
                .build();

        SendMessage message = new SendMessage(chatId.toString(), "Click the button below to open otomoto page:");
        message.setReplyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(Collections.singletonList(webAppButton))))
                .build());
        telegramClient.execute(message);

        user.setRedirectTo(UPLOAD_URL);
        userService.save(user);
    }

    @SneakyThrows
    @CommandListener(UPLOAD_URL)
    public void uploadUrl(String url, Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userService.findByChatId(chatId);

        userRedirectedValidator.validate(UPLOAD_URL, user);
        userUrlValidator.validate(url);

        user.setSearchUrl(url);
        user.setRedirectTo(CONFIRM_FILTER);

        UserFilter userFilter = urlToFiltersConverter.parseUrl(url);
        userFilter.setUser(user);
        userService.save(user);
        userService.save(userFilter);

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .keyboardRow(new KeyboardRow("Yes", "No"))
                .build();
        SendMessage message = SendMessage.builder()
                .parseMode("Markdown")
                .chatId(chatId.toString())
                .text("You selected the following filters for yourself, are they right? Please type 'yes' or 'no' in the response.\n\n" + userFilter)
                .replyMarkup(replyMarkup)
                .build();

        telegramClient.execute(message);
    }

    @SneakyThrows
    @CommandListener(CONFIRM_FILTER)
    public void confirmFilter(String answer, Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userService.findByChatId(chatId);
        UserFilter userFilter = userService.findUserFilter(user.getId());

        userRedirectedValidator.validate(CONFIRM_FILTER, user);

        ReplyKeyboardRemove removeReplyKeyboard = ReplyKeyboardRemove.builder().removeKeyboard(true).build();

        if ("yes".equalsIgnoreCase(answer)) {
            user.setRedirectTo(null);
            userFilter.setConfirmed(true);
            userService.save(userFilter);

            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("You filters was successfully saved.")
                    .replyMarkup(removeReplyKeyboard)
                    .build();
            telegramClient.execute(message);
        } else if ("no".equalsIgnoreCase(answer)) {
            user.setRedirectTo(null);
            userService.deleteFilter(userFilter.getId());
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Sorry, our app is in development now. Try again.")
                    .replyMarkup(removeReplyKeyboard)
                    .build();
            telegramClient.execute(message);
        } else {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Please type 'yes' or 'no'.")
                    .build();
            telegramClient.execute(message);
        }

        userService.save(user);
    }

    @SneakyThrows
    @CommandListener(SHOW_FILTER)
    public void showFilter(Update update){
        Long chatId = update.getMessage().getChatId();
        User user = userService.findByChatId(chatId);
        UserFilter userFilter = userService.findUserFilter(user.getId());
        String textMessage;

        if (userFilter == null) {
            textMessage = "You don't have any filters now.";
        } else {
            textMessage = "Your current filters are:\n\n" + userFilter;
        }

        SendMessage message = new SendMessage(chatId.toString(), textMessage);
        message.setParseMode("Markdown");

        telegramClient.execute(message);
    }
}
