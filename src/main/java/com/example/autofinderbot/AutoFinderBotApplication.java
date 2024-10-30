package com.example.autofinderbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoFinderBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoFinderBotApplication.class, args);
    }

}
