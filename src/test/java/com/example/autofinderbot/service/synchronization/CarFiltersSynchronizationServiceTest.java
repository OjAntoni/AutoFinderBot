package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.repository.CarBrandRepository;
import com.example.autofinderbot.repository.CarModelRepository;
import com.example.autofinderbot.repository.FuelTypeRepository;
import com.example.autofinderbot.repository.GenerationRepository;
import com.example.autofinderbot.service.CarFiltersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class CarFiltersSynchronizationServiceTest extends BaseSpringBootTest {
    @Autowired
    CarFiltersSynchronizationService carFiltersSynchronizationService;
    @Autowired
    CarBrandRepository carBrandRepository;
    @Autowired
    CarModelRepository carModelRepository;
    @Autowired
    GenerationRepository generationRepository;
    @Autowired
    FuelTypeRepository fuelTypeRepository;
    @MockitoSpyBean
    CarFiltersService carFiltersService;

    @BeforeEach
    void resetMocks() {
        reset(carFiltersService);
    }

    @Test
    void updateCarFilters_PosTC() {
        assertThat(carBrandRepository.count())
                .isPositive();
        assertThat(carModelRepository.count())
                .isPositive();
        assertThat(generationRepository.count())
                .isPositive();
        assertThat(fuelTypeRepository.count())
                .isPositive();
        assertThat(carBrandRepository.findAll(PageRequest.of(0,10)))
                .allMatch(brand -> !brand.getModels().isEmpty())
                .flatExtracting(CarBrand::getModels)
                .anyMatch(model -> !model.getGenerations().isEmpty());
    }

    @Test
    void rollbackOnException_NegTC() {
        doThrow(new RuntimeException()).when(carFiltersService).deleteBrandFilters();
        when(carFiltersService.isBrandFiltersValid()).thenReturn(false);

        carFiltersSynchronizationService.updateCarFilters();

        verify(carFiltersService, times(0)).saveBrandFilters(anyList());
    }
}