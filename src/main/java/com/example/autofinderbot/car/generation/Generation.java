package com.example.autofinderbot.car.generation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@Getter
public class Generation {
    @Id
    long id;
    String searchKey;
    String name;
    long carModelId;
}
