package com.example.autofinderbot.web.controller.car;

import com.example.autofinderbot.web.dto.car.CarRequest;
import com.example.autofinderbot.web.dto.car.CarResponse;
import com.example.autofinderbot.web.dto.car.SimilarCarPricesResponse;
import com.example.autofinderbot.web.service.WebCarService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/cars")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarController {
    WebCarService webCarService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<CarResponse> getCars(
            @ModelAttribute CarRequest request,
            @PageableDefault(sort = "id", direction = DESC) Pageable pageable
    ) {
        return webCarService.getCars(request, pageable);
    }

    @GetMapping("/{id}/similar/prices")
    @SecurityRequirement(name = "bearerAuth")
    public SimilarCarPricesResponse getSimilarCarPrices(@PathVariable long id) {
        return webCarService.getSimilarCarPrices(id);
    }
}
