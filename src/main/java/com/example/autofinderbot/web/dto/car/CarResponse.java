package com.example.autofinderbot.web.dto.car;

import com.example.autofinderbot.car.detail.CarDetail;
import com.example.autofinderbot.car.seller.Seller;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarResponse {
    long id;
    String url;
    String title;
    String brand;
    String fuelType;
    long mileage;
    String mileageUnit;
    double price;
    String currency;
    List<CarDetail> details;
    Seller seller;
    String description;
    String thumbnailUrl;
    boolean isPriceMedium;
    boolean isPriceLower;
    PriceComparison priceComparison;
    boolean isDamaged;
    List<String> imageUrls;

    public enum PriceComparison {
        LOWER, MEDIUM, HIGHER, UNDEFINED
    }
}
