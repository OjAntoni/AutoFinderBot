package com.example.autofinderbot.common.util;

import lombok.Getter;

@Getter
public class Parameter {
    private final String name;
    private final Object value;

    private Parameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public static Parameter of(String name, Object value) {
        return new Parameter(name, value);
    }
}
