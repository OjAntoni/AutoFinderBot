package com.example.autofinderbot.car.core;

import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class GoogleMapsConverter {
    private static final String GOOGLE_MAPS_URL_WITH_COORDINATES = "https://www.google.com/maps?q=%s,%s";

    public String url(double latitude, double longitude) {
        return format(GOOGLE_MAPS_URL_WITH_COORDINATES, latitude, longitude);
    }
}


