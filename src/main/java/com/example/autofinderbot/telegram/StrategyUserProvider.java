package com.example.autofinderbot.telegram;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.shared.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
public class StrategyUserProvider {
    private final UserService userService;
    private final DateTimeUtil dateTimeUtil;

    /**
     * Retrieves an existing user or creates a new one, ensuring lastActive is always updated.
     */
    public User getOrCreateUser(Update update) {
        User user = userService.findByChatId(getChatId(update));

        if (user == null) {
            user = new User();
            user.setChatId(getChatId(update));
        }

        user.setLastActive(dateTimeUtil.now());
        populateUserData(update, user);

        return userService.save(user);
    }

    private long getChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else {
            throw new IllegalArgumentException("Update has no message or callback query");
        }
    }

    private void populateUserData(Update update, User user) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            user.setFirstname(callbackQuery.getFrom().getFirstName());
            user.setLastname(callbackQuery.getFrom().getLastName());
            user.setUsername(callbackQuery.getFrom().getUserName());
            user.setLanguageCode(callbackQuery.getFrom().getLanguageCode());
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            user.setFirstname(message.getFrom().getFirstName());
            user.setLastname(message.getFrom().getLastName());
            user.setUsername(message.getFrom().getUserName());
            user.setLanguageCode(message.getFrom().getLanguageCode());
        }
    }
}
