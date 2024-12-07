package com.example.autofinderbot.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;


public enum Details {
    YEAR("year", "Year"),
    COLOR("color", "Color"),
    BRAND("basic_information", "Brand"),
    MODEL("model", "Model"),
    GENERATION("generation", "Generation"),
    VERSION("version", "Version"),
    DESCRIPTION("content-description-section", "Description"),
    SEATS("nr_seats", "Seats"),
    DOORS("door_count", "Doors");


    public final String attribute;
    public final String name;

    private Details(String attribute, String name) {
        this.attribute = attribute;
        this.name = name;
    }

}
