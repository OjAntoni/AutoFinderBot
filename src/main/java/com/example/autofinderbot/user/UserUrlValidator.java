package com.example.autofinderbot.user;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
//TODO move to car package
public class UserUrlValidator {

    @SneakyThrows
    public void validate(String url) {
        if(url == null || url.isEmpty()) {
            throw new InvalidSearchUrlException("Url should be present.");
        }
        if(!url.startsWith("https://www.otomoto.pl/")) {
            throw new InvalidSearchUrlException("Url should start with 'https://www.otomoto.pl/'.");
        }
        if(url.length() > 2048) {
            throw new InvalidSearchUrlException("Url is too long.");
        }
    }
}
