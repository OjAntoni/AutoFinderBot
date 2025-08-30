package com.example.autofinderbot.car;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarCarPricesResponse {
    List<SimilarCarPriceResponse> cars;
    double minPrice;
    double maxPrice;
}
