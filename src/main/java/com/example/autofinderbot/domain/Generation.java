package com.example.autofinderbot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class Generation {
    @Id
    long id;
    String searchKey;
    String name;
    long carModelId;
}
