package com.example.autofinderbot.car;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private static final int MAX_PAGE_SIZE = 100;

    WebCarService webCarService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<CarResponse> getCars(
            @ModelAttribute CarRequest request,
            @PageableDefault(sort = "id", direction = DESC, size = 20) Pageable pageable
    ) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Pageable limited = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        return webCarService.getCars(request, limited);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public CarResponse getCar(@PathVariable long id) {
        return webCarService.getCar(id);
    }

    @GetMapping("/{id}/similar/prices")
    @SecurityRequirement(name = "bearerAuth")
    public SimilarCarPricesResponse getSimilarCarPrices(@PathVariable long id) {
        return webCarService.getSimilarCarPrices(id);
    }
}
