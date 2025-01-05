package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.repository.CarBrandRepository;
import com.example.autofinderbot.repository.CarModelRepository;
import com.example.autofinderbot.repository.FuelTypeRepository;
import com.example.autofinderbot.repository.GenerationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

class CarFiltersSynchronizationServiceTest extends BaseSpringBootTest {
    @Autowired
    CarBrandRepository carBrandRepository;
    @Autowired
    CarModelRepository carModelRepository;
    @Autowired
    GenerationRepository generationRepository;
    @Autowired
    FuelTypeRepository fuelTypeRepository;

    @Test
    void updateCarFilters_PosTC() {
        Assertions.assertThat(carBrandRepository.count())
                .isPositive();
        Assertions.assertThat(carModelRepository.count())
                .isPositive();
        Assertions.assertThat(generationRepository.count())
                .isPositive();
        Assertions.assertThat(fuelTypeRepository.count())
                .isPositive();
        Assertions.assertThat(carBrandRepository.findAll(PageRequest.of(0,10)))
                .allMatch(brand -> !brand.getModels().isEmpty())
                .flatExtracting(CarBrand::getModels)
                .anyMatch(model -> !model.getGenerations().isEmpty());
    }
}