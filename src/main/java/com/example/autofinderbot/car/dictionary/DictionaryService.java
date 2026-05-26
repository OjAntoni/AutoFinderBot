package com.example.autofinderbot.car.dictionary;

import com.example.autofinderbot.car.dictionary.GearboxType;
import com.example.autofinderbot.car.brand.CarBrand;
import com.example.autofinderbot.car.model.CarModel;
import com.example.autofinderbot.car.fuel.FuelType;
import com.example.autofinderbot.car.generation.Generation;
import com.example.autofinderbot.car.brand.CarBrandRepository;
import com.example.autofinderbot.car.model.CarModelRepository;
import com.example.autofinderbot.car.fuel.FuelTypeRepository;
import com.example.autofinderbot.car.generation.GenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DictionaryService {
    CarBrandRepository carBrandRepository;
    CarModelRepository carModelRepository;
    GenerationRepository generationRepository;
    FuelTypeRepository fuelTypeRepository;

    @Transactional(readOnly = true)
    public List<CarBrand> getCarBrands() {
        return carBrandRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CarModel> getCarModels(long carBrandId) {
        return carModelRepository.findAllByCarBrandId(carBrandId);
    }

    @Transactional(readOnly = true)
    public List<Generation> getGenerations(long carModelId) {
        return generationRepository.findAllByCarModelId(carModelId);
    }

    @Transactional(readOnly = true)
    public List<FuelType> getFuelTypes() {
        return fuelTypeRepository.findAll();
    }

    public List<GearboxType> getGearboxTypes() {
        return List.of(GearboxType.values());
    }
}


