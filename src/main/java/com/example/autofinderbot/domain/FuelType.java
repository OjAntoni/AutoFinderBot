package com.example.autofinderbot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FuelType {
    @Id
    long id;
    String searchKey;
    String name;
}
