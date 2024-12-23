package com.example.autofinderbot.telegram;

import org.springframework.stereotype.Component;

@Component
public class TestListener {
    @CommandListener("test")
    public void test(String var) {
        System.out.println("Test command");
    }

    @CommandListener("test2")
    public void test2(String var) {
        System.out.println("Test command with argument: " + var);
    }
}
