package com.example.autofinderbot.web.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class DayPrice {
    private LocalDate day;
    private double price;

    public DayPrice(Date dateTime, Double price) {
        this.day = dateTime.toLocalDate();
        this.price = price != null ? price : 0.0d;
    }
}
