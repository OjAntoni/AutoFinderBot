package com.example.autofinderbot.car;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayCount{
    LocalDate day;
    long count;

    public DayCount(Date date, Long count) {
        this.day = date.toLocalDate();
        this.count = count != null ? count : 0L;
    }
}