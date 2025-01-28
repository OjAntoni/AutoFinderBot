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
    Long cityId;
    String region;
    Long regionId;
    String shortAddress;
    Double latitude;
    Double longitude;
}
