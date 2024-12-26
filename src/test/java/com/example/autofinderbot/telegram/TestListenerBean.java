package com.example.autofinderbot.telegram;


public class TestListenerBean {
    @CommandListener("test")
    public void test(String var) {
        System.out.println("Test command");
    }

    @CommandListener("test2")
    public String test2(String var) {
        return var;
    }
}
