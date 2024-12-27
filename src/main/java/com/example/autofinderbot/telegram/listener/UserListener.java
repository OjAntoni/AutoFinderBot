package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.exception.InvalidUrlException;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.telegram.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Component
public class UserListener {
    UserService userService;

    @CommandListener("/register")
    public void registerUser(Update update) throws InvalidUrlException {
        User user = new User();
        user.setChatId(update.getMessage().getChatId());
        //TODO replace with real url
        user.setSearchUrl(SEARCH_URL);
        userService.save(user);
    }
}
