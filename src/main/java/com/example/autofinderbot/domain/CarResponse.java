package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

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
@Table(name = "car_response")
public class CarResponse {
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
    @Exclude
    @Setter
    @OneToMany(mappedBy = "carResponseId", cascade = CascadeType.ALL)
    List<CarDetail> details;

    public CarResponse(String title, String brand, String fuelType, long mileage, String mileageUnit, double price, String currency) {
        this.title = title;
        this.brand = brand;
        this.fuelType = fuelType;
        this.mileage = mileage;
        this.mileageUnit = mileageUnit;
        this.price = price;
        this.currency = currency;
    }
}
