package com.example.autofinderbot.car.dictionary;

import com.example.autofinderbot.car.dictionary.GearboxTypeResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/dictionaries")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DictionaryController {
    DictionaryService dictionaryService;

    @GetMapping("/car-brands")
    @SecurityRequirement(name = "bearerAuth")
    public List<DictionaryResponse> getCarBrands() {
        return dictionaryService.getCarBrands().stream()
                .map(b -> new DictionaryResponse(b.getId(), b.getName()))
                .toList();
    }

    @GetMapping("/fuel-types")
    @SecurityRequirement(name = "bearerAuth")
    public List<DictionaryResponse> getFuelTypes() {
        return dictionaryService.getFuelTypes().stream()
                .map(f -> new DictionaryResponse(f.getId(), f.getName()))
                .toList();
    }

    @GetMapping("/gearbox-types")
    @SecurityRequirement(name = "bearerAuth")
    public List<GearboxTypeResponse> getGearboxTypes() {
        return dictionaryService.getGearboxTypes().stream()
                .map(gt -> new GearboxTypeResponse(gt.getSearchKey(), gt.getName()))
                .toList();
    }

    @GetMapping("/car-brands/{brandId}/models")
    @SecurityRequirement(name = "bearerAuth")
    public List<DictionaryResponse> getCarModels(@PathVariable long brandId) {
        return dictionaryService.getCarModels(brandId).stream()
                .map(m -> new DictionaryResponse(m.getId(), m.getName()))
                .toList();
    }

    @GetMapping("/car-models/{modelId}/generations")
    @SecurityRequirement(name = "bearerAuth")
    public List<DictionaryResponse> getGenerations(@PathVariable long modelId) {
        return dictionaryService.getGenerations(modelId).stream()
                .map(g -> new DictionaryResponse(g.getId(), g.getName()))
                .toList();
    }
}


