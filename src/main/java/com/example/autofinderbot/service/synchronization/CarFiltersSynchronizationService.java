package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.domain.FuelType;
import com.example.autofinderbot.parser.otomoto.CarFiltersParser;
import com.example.autofinderbot.parser.service.DocumentService;
import com.example.autofinderbot.service.CarFiltersService;
import com.example.autofinderbot.common.util.Logger;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static com.example.autofinderbot.common.util.APIConstants.OTOMOTO_URL;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarFiltersSynchronizationService {
    CarFiltersParser carFiltersParser;
    CarFiltersService carFiltersService;
    DocumentService<Document> documentService;
    TransactionTemplate transactionTemplate;
    Logger logger;

    @EventListener(ApplicationReadyEvent.class)
    void updateCarFilters() {
        Predicate<Document> validator = carFiltersParser.documentValidator();
        AtomicReference<Document> document = new AtomicReference<>();

        updateCarBrandFilters(validator, document);
        updateFuelTypes(validator, document);
    }

    private void updateCarBrandFilters(Predicate<Document> validator, AtomicReference<Document> document) {
        transactionTemplate.execute(status -> {
            try {
                if(!carFiltersService.isBrandFiltersValid()) {
                    carFiltersService.deleteBrandFilters();
                    document.set(documentService.load(OTOMOTO_URL, validator));
                    List<CarBrand> carBrands = carFiltersParser.extractCarBrands(document.get());
                    carFiltersService.saveBrandFilters(carBrands);
                }
            } catch (Exception e) {
                status.setRollbackOnly();
                logger.error("Failed to load filters: " + e.getMessage());
            }
            return null;
        });
    }

    private void updateFuelTypes(Predicate<Document> validator, AtomicReference<Document> document) {
        transactionTemplate.execute(status -> {
            try {
                if(!carFiltersService.isFuelTypesValid()) {
                    if(document.get() == null) document.set(documentService.load(OTOMOTO_URL, validator));
                    carFiltersService.deleteFuelTypes();
                    List<FuelType> fuelTypes = carFiltersParser.extractFuelTypes(document.get());
                    carFiltersService.saveFuelTypes(fuelTypes);
                }
            } catch (Exception e) {
                status.setRollbackOnly();
                logger.error("Failed to load fuel types: " + e.getMessage());
            }
            return null;
        });
    }
}
