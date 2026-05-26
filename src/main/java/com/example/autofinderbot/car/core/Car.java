package com.example.autofinderbot.car.core;

import com.example.autofinderbot.car.detail.CarDetail;
import com.example.autofinderbot.car.seller.Seller;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(indexes = {
    @Index(name = "idx_car_brand", columnList = "brand"),
    @Index(name = "idx_car_fuel_type", columnList = "fuelType"),
    @Index(name = "idx_car_mileage", columnList = "mileage"),
    @Index(name = "idx_car_price", columnList = "price")
})
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
    String thumbnailUrl;
    @ElementCollection
    @CollectionTable(
        name = "car_image_urls",
        joinColumns = @JoinColumn(name = "car_id")
    )
    @Column(name = "image_url")
    @BatchSize(size = 100)
    List<String> imageUrls;
    @Exclude
    LocalDateTime createdAt;
    @Exclude
    @OneToMany(mappedBy = "carId", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = EAGER)
    @BatchSize(size = 100)
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


