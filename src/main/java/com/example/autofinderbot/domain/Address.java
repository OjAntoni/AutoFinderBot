package com.example.autofinderbot.domain;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@Builder
public class Address {
    String address;
    String city;
    long cityId;
    String region;
    long regionId;
    String shortAddress;
    double latitude;
    double longitude;
}
