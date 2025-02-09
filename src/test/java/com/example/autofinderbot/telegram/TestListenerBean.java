package com.example.autofinderbot.telegram;


import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.telegram.exception.TelegramBotException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TestListenerBean {
    @CommandListener("test")
    public void test(String var) {
        System.out.println("Test command");
    }

    @CommandListener("test2")
    public double test2(int number) {
        return Math.pow(number, 2);
    }

    @CommandListener("exception")
    public void exception() {
        throw new RuntimeException("Test exception");
    }

    @CommandListener("telegramException")
    public void telegramException() {
        throw new TelegramBotException("Error occurred.") {};
    }

    @CommandListener("update")
    public void update(Update update) {
        System.out.println(update);
    }

    @CommandListener("callback")
    public void callback(long id, Update update) {
        System.out.println(update);
    }

    @CommandListener("callback2")
    public void callback(long id, Update update, String text) {
        System.out.println(update);
    }

    @CommandListener("user")
    public void user(User user) {
        System.out.println(user);
    }

    @CommandListener("redirection")
    public void redirection(User user, String arg, long number) {}
}
