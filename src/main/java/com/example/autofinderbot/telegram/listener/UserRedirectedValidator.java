package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.telegram.exception.UserIsNotRedirectedException;
import org.springframework.stereotype.Component;

@Component
public class UserRedirectedValidator {
    public void validate(String path, User user) {
        if (!path.equals(user.getRedirectTo())) {
            throw new UserIsNotRedirectedException();
        }
    }
}
