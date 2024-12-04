package com.example.autofinderbot.domain;

import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
@EqualsAndHashCode
@ToString
public class CarResponse {
    String title;
    String brand;
    String fuelType;
    long mileage;
    String mileageUnit;
    double price;
    String currency;
    @Exclude
    @NonFinal
    @Setter
    String url;
}
