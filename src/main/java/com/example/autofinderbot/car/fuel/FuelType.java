package com.example.autofinderbot.car.fuel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class FuelType {
    @Id
    long id;
    String searchKey;
    String name;
}
