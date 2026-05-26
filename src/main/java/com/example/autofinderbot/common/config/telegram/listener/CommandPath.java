package com.example.autofinderbot.common.config.telegram.listener;

import lombok.Generated;

@Generated
public class CommandPath {
    public static final String START = "/start";

    private static final String FILTER = "filter";
    public static final String SET_FILTER = "/set_" + FILTER;
    public static final String CONFIRM_FILTER = "/confirm_" + FILTER;
    public static final String SHOW_FILTER = "/show_" + FILTER;
    public static final String STOP_FILTER = "/stop_" + FILTER;
    public static final String ACTIVATE_FILTER = "/activate_" + FILTER;

    public static final String UPLOAD_URL = "/upload_url";

    private static final String CAR = "car";
    private static final String CARS = CAR + "s";
    public static final String SELECTED_CARS = "/selected_" + CARS;
    public static final String LIKE_CAR = "/like_" + CAR;
    public static final String DISLIKE_CAR = "/dislike_" + CAR;

    private static final String DESCRIPTION = "description";
    public static final String SHOW_DESCRIPTION = "/show_" + DESCRIPTION;
    public static final String HIDE_DESCRIPTION = "/hide_" + DESCRIPTION;

    private static final String DETAILS = "details";
    public static final String SHOW_DETAILS = "/show_" + DETAILS;
    public static final String HIDE_DETAILS = "/hide_" + DETAILS;
}
