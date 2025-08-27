package com.example.autofinderbot.filter;

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
public class CarFiltersService {
    CarBrandRepository carBrandRepository;
    CarModelRepository carModelRepository;
    GenerationRepository generationRepository;
    FuelTypeRepository fuelTypeRepository;

    //TODO move to synchronization package (methods)
    @Transactional(readOnly = true)
    public boolean isBrandFiltersValid() {
        return generationRepository.count() > 0 &&
                carModelRepository.count() > 0 &&
                carBrandRepository.count() > 0;
    }

    @Transactional(readOnly = true)
    public boolean isFuelTypesValid() {
        return fuelTypeRepository.count() > 0;
    }

    @Transactional
    public void saveBrandFilters(List<CarBrand> brands) {
        carBrandRepository.saveAll(brands);
    }

    @Transactional
    public void saveFuelTypes(List<FuelType> fuelTypes) {
        fuelTypeRepository.saveAll(fuelTypes);
    }

    @Transactional
    public void deleteBrandFilters() {
        generationRepository.deleteAll();
        carModelRepository.deleteAll();
        carBrandRepository.deleteAll();
    }

    @Transactional
    public void deleteFuelTypes() {
        fuelTypeRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<CarBrand> getBrands(List<String> searchKeys) {
        return carBrandRepository.findAllBySearchKeyIn(searchKeys);
    }

    @Transactional(readOnly = true)
    public List<CarModel> getModels(List<String> searchKeys) {
        return carModelRepository.findAllBySearchKeyIn(searchKeys);
    }

    @Transactional(readOnly = true)
    public List<Generation> getGenerations(List<String> searchKeys) {
        return generationRepository.findAllBySearchKeyIn(searchKeys);
    }

    @Transactional(readOnly = true)
    public List<FuelType> getFuelTypes(List<String> searchKeys) {
        return fuelTypeRepository.findAllBySearchKeyIn(searchKeys);
    }
}
