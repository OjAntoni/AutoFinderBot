package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
@Builder
@Setter
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;
    String brand;
    String fuelType;
    long mileage;
    String mileageUnit;
    double price;
    String currency;
    @Exclude
    String url;
    @Exclude
    LocalDateTime createdAt;
    @Exclude
    @OneToMany(mappedBy = "carId", cascade = REMOVE, orphanRemoval = true, fetch = EAGER)
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
