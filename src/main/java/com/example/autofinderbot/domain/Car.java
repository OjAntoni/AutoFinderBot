package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
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
    Long mileage;
    String mileageUnit;
    double price;
    String currency;
    @Exclude
    String url;
    @Exclude
    LocalDateTime createdAt;
    @Exclude
    @OneToMany(mappedBy = "carId", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = EAGER)
    List<CarDetail> details;
    @OneToOne(cascade = ALL, fetch = EAGER)
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    Seller seller;
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source source;

    public Car(String title, String brand, String fuelType, long mileage, String mileageUnit, double price, String currency, Source source) {
        this.title = title;
        this.brand = brand;
        this.fuelType = fuelType;
        this.mileage = mileage;
        this.mileageUnit = mileageUnit;
        this.price = price;
        this.currency = currency;
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return id == car.id && mileage == car.mileage && Double.compare(price, car.price) == 0 && Objects.equals(title, car.title) && Objects.equals(brand, car.brand) && Objects.equals(fuelType, car.fuelType) && Objects.equals(mileageUnit, car.mileageUnit) && Objects.equals(currency, car.currency) && Objects.equals(url, car.url) && Objects.equals(createdAt, car.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }

    public enum Source {
        OLX,
        OTOMOTO
    }
}
