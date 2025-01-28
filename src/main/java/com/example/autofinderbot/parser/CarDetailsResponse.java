package com.example.autofinderbot.parser;

import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.Seller;
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
}
