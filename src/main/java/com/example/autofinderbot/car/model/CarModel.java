package com.example.autofinderbot.car.model;

import com.example.autofinderbot.car.generation.Generation;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Getter
public class CarModel {
    @Id
    long id;
    String searchKey;
    String name;
    long carBrandId;
    @Setter
    @Getter
    @OneToMany(mappedBy = "carModelId", cascade = ALL, fetch = FetchType.EAGER) //TODO remove eager
    List<Generation> generations;

    public CarModel(long id, String searchKey, String name, long carBrandId) {
        this.id = id;
        this.searchKey = searchKey;
        this.name = name;
        this.carBrandId = carBrandId;
        this.generations = new ArrayList<>();
    }
}
