package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
public class Car {
    @Id
    long id;
    String title;
    String brand;
    String fuelType;
    long mileage;
    String mileageUnit;
    double price;
    String currency;
    @Exclude
    @Setter
    String url;
    @Setter
    @Exclude
    LocalDateTime createdAt;
    @Exclude
    @Setter
    @OneToMany(mappedBy = "carResponseId", cascade = CascadeType.ALL)
    List<CarDetail> details;

    public Car(String title, String brand, String fuelType, long mileage, String mileageUnit, double price, String currency) {
        this.title = title;
        this.brand = brand;
        this.fuelType = fuelType;
        this.mileage = mileage;
        this.mileageUnit = mileageUnit;
        this.price = price;
        this.currency = currency;
    }
}
