package com.example.autofinderbot.car.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarCarPriceResponse {
    long id;
    String url;
    double price;
    String thumbnailUrl;
}


