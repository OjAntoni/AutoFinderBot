package com.example.autofinderbot.web.service;

import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.domain.CarModel;
import com.example.autofinderbot.domain.FuelType;
import com.example.autofinderbot.domain.Generation;
import com.example.autofinderbot.domain.GearboxType;
import com.example.autofinderbot.repository.CarBrandRepository;
import com.example.autofinderbot.repository.CarModelRepository;
import com.example.autofinderbot.repository.FuelTypeRepository;
import com.example.autofinderbot.repository.GenerationRepository;
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
