package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.car.detail.CarDetail;
import com.example.autofinderbot.car.seller.Seller;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
public class CarDetailsResponse {
    List<CarDetail> carDetails;
    Seller seller;
    String description;
    List<String> imageUrls;
}
