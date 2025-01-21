package com.example.autofinderbot.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Answer {
    YES_PL("Tak"),
    NO_PL("Nie");

    private final String value;
}
