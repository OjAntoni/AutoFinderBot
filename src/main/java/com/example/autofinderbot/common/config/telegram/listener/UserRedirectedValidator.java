package com.example.autofinderbot.common.config.telegram.listener;

import com.example.autofinderbot.user.User;
import org.springframework.stereotype.Component;

@Component
//TODO add property to annotation
public class UserRedirectedValidator {
    public void validate(String path, User user) {
        if (!path.equals(user.getRedirectTo())) {
            throw new UserIsNotRedirectedException();
        }
    }
}
