package com.example.autofinderbot.car.brand;

import com.example.autofinderbot.car.model.CarModel;
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
public class CarBrand {
    @Id
    long id;
    String searchKey;
    String name;
    @Setter
    @Getter
    @OneToMany(mappedBy = "carBrandId", cascade = ALL, fetch = FetchType.EAGER) //TODO remove eager
    List<CarModel> models;

    public CarBrand(long id, String searchKey, String name) {
        this.id = id;
        this.searchKey = searchKey;
        this.name = name;
        this.models = new ArrayList<>();
    }
}
