package com.example.autofinderbot.telegram;


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

    @CommandListener("update")
    public void update(Update update) {
        System.out.println(update);
    }
}
