package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.telegram.exception.InvalidSearchUrlException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class UserUrlValidator {

    @SneakyThrows
    public void validate(String url) {
        if(url == null || url.isEmpty()) {
            throw new InvalidSearchUrlException("url should be present.");
        }
        if(!url.startsWith("https://www.otomoto.pl/")) {
            throw new InvalidSearchUrlException("url should start with 'https://www.otomoto.pl/'.");
        }
        if(!url.contains("order%5D=created_at_first")) {
            throw new InvalidSearchUrlException("cars should be sorted by creation date ascending.");
        }
        if(url.length() > 2048) {
            throw new InvalidSearchUrlException("url is too long.");
        }
    }
}
