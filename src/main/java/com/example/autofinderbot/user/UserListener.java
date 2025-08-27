package com.example.autofinderbot.user;

import com.example.autofinderbot.common.config.telegram.components.TelegramRelated;
import com.example.autofinderbot.common.config.telegram.listener.UserRedirectedValidator;
import com.example.autofinderbot.filter.UserFilter;
import com.example.autofinderbot.common.config.telegram.listener.CommandListener;
import com.example.autofinderbot.filter.UrlToFiltersConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;
import java.util.List;

import static com.example.autofinderbot.common.config.telegram.listener.CommandPath.*;
import static java.lang.String.valueOf;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Component
@TelegramRelated
//TODO split up into services
public class UserListener {
    UserService userService;
    UserUrlValidator userUrlValidator;
    UrlToFiltersConverter urlToFiltersConverter;
    UserRedirectedValidator userRedirectedValidator;
    TelegramClient telegramClient;

    @SneakyThrows
    @CommandListener(START)
    public void registerUser(User user) {
        SendMessage message = new SendMessage(valueOf(user.getChatId()),
                "Hi, %s! I am you car assistant that will help you to find your dream car ".formatted(user.getFirstname()) +
                "in the fastest way possible. Just set up filter for yourself. That's all :)");
        telegramClient.execute(message);
    }

    @SneakyThrows
    @CommandListener(SET_FILTER)
    public void setFilter(User user){
        user.setRedirectTo(UPLOAD_URL);
        userService.save(user);

        InlineKeyboardButton webAppButton = InlineKeyboardButton.builder()
                .text("Open otomoto")
                .url("https://www.otomoto.pl/osobowe?search%5Badvanced_search_expanded%5D=true")
                .build();

        SendMessage message = new SendMessage(valueOf(user.getChatId()), "Click the button below to open otomoto page, then copy and send your search url here.");
        message.setReplyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(Collections.singletonList(webAppButton))))
                .build());
        telegramClient.execute(message);
    }

    @SneakyThrows
    @CommandListener(UPLOAD_URL)
    public void uploadUrl(String url, User user) {
        userRedirectedValidator.validate(UPLOAD_URL, user);
        userUrlValidator.validate(url);

        user.setRedirectTo(CONFIRM_FILTER);

        UserFilter userFilter = urlToFiltersConverter.parseUrl(url);
        userFilter.setUser(user);
        userFilter.setSearchUrl(url);
        userService.save(user);
        userService.save(userFilter);

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .keyboardRow(new KeyboardRow("Yes", "No"))
                .build();
        SendMessage message = SendMessage.builder()
                .parseMode("Markdown")
                .chatId(valueOf(user.getChatId()))
                .text("You selected the following filters for yourself, are they right? Please type 'yes' or 'no' in the response.\n\n" + userFilter.filterParametersOnly())
                .replyMarkup(replyMarkup)
                .build();

        telegramClient.execute(message);
    }

    @SneakyThrows
    @CommandListener(CONFIRM_FILTER)
    public void confirmFilter(String answer, User user) {
        UserFilter userFilter = userService.findUserFilter(user.getId());

        userRedirectedValidator.validate(CONFIRM_FILTER, user);

        ReplyKeyboardRemove removeReplyKeyboard = ReplyKeyboardRemove.builder().removeKeyboard(true).build();

        if ("yes".equalsIgnoreCase(answer)) {
            userService.removeOldFilter(user);
            user.setRedirectTo(null);
            userFilter.setConfirmed(true);
            userFilter.setActive(true);
            userService.save(userFilter);

            SendMessage message = SendMessage.builder()
                    .chatId(valueOf(user.getChatId()))
                    .text("You filters was successfully saved.")
                    .replyMarkup(removeReplyKeyboard)
                    .build();
            telegramClient.execute(message);
        } else if ("no".equalsIgnoreCase(answer)) {
            user.setRedirectTo(null);
            userService.rollbackToOldFilter(user);
            SendMessage message = SendMessage.builder()
                    .chatId(valueOf(user.getChatId()))
                    .text("Sorry, our app is in development now. Try again.")
                    .replyMarkup(removeReplyKeyboard)
                    .build();
            telegramClient.execute(message);
        } else {
            SendMessage message = SendMessage.builder()
                    .chatId(valueOf(user.getChatId()))
                    .text("Please type 'yes' or 'no'.")
                    .build();
            telegramClient.execute(message);
        }

        userService.save(user);
    }

    @SneakyThrows
    @CommandListener(SHOW_FILTER)
    public void showFilter(User user){
        UserFilter userFilter = userService.findUserFilter(user.getId());
        String textMessage;

        if (userFilter == null) {
            textMessage = "You don't have any filters now.";
        } else {
            textMessage = "Your current filters are:\n\n" + userFilter;
        }

        SendMessage message = new SendMessage(valueOf(user.getChatId()), textMessage);
        message.setParseMode("Markdown");

        telegramClient.execute(message);
    }

    @SneakyThrows
    @CommandListener(STOP_FILTER)
    public void stopFilter(User user){
        UserFilter userFilter = userService.findUserFilter(user.getId());

        if(userFilter == null) {
            SendMessage message = new SendMessage(valueOf(user.getChatId()), "You don't have any filters now.");
            telegramClient.execute(message);
            return;
        }

        if(!userFilter.isActive()) {
            SendMessage message = new SendMessage(valueOf(user.getChatId()), "Your filter is already stopped.");
            telegramClient.execute(message);
            return;
        }

        userService.stopFilter(user);

        SendMessage message = new SendMessage(valueOf(user.getChatId()), "Your filter was stopped. From now you will not receive any notifications. You can start it again at any time.");
        telegramClient.execute(message);
    }

    @SneakyThrows
    @CommandListener(ACTIVATE_FILTER)
    public void activateFilter(User user) {
        UserFilter userFilter = userService.findUserFilter(user.getId());

        if(userFilter == null) {
            SendMessage message = new SendMessage(valueOf(user.getChatId()), "You don't have any filters now.");
            telegramClient.execute(message);
            return;
        }

        if(userFilter.isActive()) {
            SendMessage message = new SendMessage(valueOf(user.getChatId()), "Your filter is already active.");
            telegramClient.execute(message);
            return;
        }

        userService.activateFilter(user);

        SendMessage message = new SendMessage(valueOf(user.getChatId()), "Your filter was activated. From now you will receive notifications about new cars.");
        telegramClient.execute(message);
    }
}
