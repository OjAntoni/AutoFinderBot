package com.example.autofinderbot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CarBrand {
    @Id
    long id;
    String search_key;
    String name;
    @Setter
    @Getter
    @OneToMany(mappedBy = "carBrandId", cascade = ALL)
    List<CarModel> models;

    public CarBrand(long id, String search_key, String name) {
        this.id = id;
        this.search_key = search_key;
        this.name = name;
        this.models = new ArrayList<>();
    }
}
