package com.example.autofinderbot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CarBrand {
    @Id
    long id;
    String search_key;
    String name;
    @OneToMany(mappedBy = "carBrandId")
    List<CarModel> models;

    public CarBrand(long id, String search_key, String name) {
        this.id = id;
        this.search_key = search_key;
        this.name = name;
    }
}
