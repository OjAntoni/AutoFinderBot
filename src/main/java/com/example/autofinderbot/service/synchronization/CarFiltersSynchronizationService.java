package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.domain.CarBrand;
import com.example.autofinderbot.parser.CarFiltersParser;
import com.example.autofinderbot.service.CarFiltersService;
import com.example.autofinderbot.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarFiltersSynchronizationService {
    CarFiltersParser carFiltersParser;
    CarFiltersService carFiltersService;
    DocumentService documentService;

    @EventListener(ApplicationReadyEvent.class)
    private void updateCarFilters() throws IOException {
        Predicate<Document> validator = carFiltersParser.documentValidator();
        if(!carFiltersService.isFiltersValid()) {
            carFiltersService.deleteFilters();
            Document document = documentService.load("https://www.otomoto.pl/", validator);
            List<CarBrand> carBrands = carFiltersParser.extractCarBrands(document);
            carFiltersService.saveFilters(carBrands);
        }
    }
}
