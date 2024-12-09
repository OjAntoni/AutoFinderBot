package com.example.autofinderbot.shared;

import lombok.Getter;


public enum Details {
    YEAR("year", "Year"),
    COLOR("color", "Color"),
    BRAND("make", "Brand"),
    MODEL("model", "Model"),
    GENERATION("generation", "Generation"),
    VERSION("version", "Version"),
    DESCRIPTION("content-description-section", "Description"),
    SEATS("nr_seats", "Seats"),
    DOORS("door_count", "Doors"),
    CREATED_AT("createdAt", "Production date"),
    FUEL_TYPE("fuel_type", "Fuel type"),
    ENGINE_CAPACITY("engine_capacity", "Engine capacity"),
    ENGINE_POWER("engine_power", "Engine power"),
    MILEAGE("mileage", "Mileage"),
    GEARBOX("gearbox", "Gearbox"),
    URBAN_CONSUMPTION("urban_consumption", "Urban consumption"),
    EXTRA_URBAN_CONSUMPTION("extra_urban_consumption", "Extra urban consumption");

    @Getter
    public final String attribute;
    public final String name;

    private Details(String attribute, String name) {
        this.attribute = attribute;
        this.name = name;
    }

}
