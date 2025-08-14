package com.example.autofinderbot.web.dto.car;

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
