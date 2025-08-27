package com.example.autofinderbot.car;

import com.example.autofinderbot.common.config.cache.CacheProperties;
import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.car.detail.CarDetail;
import com.example.autofinderbot.car.detail.CarDetailRepository;
import com.example.autofinderbot.synchronization.report.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class CarServiceTest extends BaseSpringBootTest {
    @Autowired
    CarService carService;
    @Autowired
    CarRepository carRepository;
    @Autowired
    CarDetailRepository carDetailRepository;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    CacheProperties props;

    @Test
    void existsByUrl_PosTC() {
        assertThat(carService.exists("https://www.example.com/audi-a4"))
                .isTrue();
    }

    @Test
    void saveAll_PosTC() {
        Cache cache = cacheManager.getCache(props.getCarUrl());
        requireNonNull(cache);

        List<Car> cars = List.of(
                Car.builder()
                        .url("https://example1.com")
                        .brand("brand")
                        .price(1000)
                        .title("title")
                        .mileage(100500L)
                        .currency("currency")
                        .fuelType("fuelType")
                        .createdAt(LocalDateTime.now())
                        .details(List.of(new CarDetail("key", "value")))
                        .source(Car.Source.OTOMOTO)
                        .build(),
                Car.builder()
                        .url("https://example2.com")
                        .brand("brand")
                        .price(1000)
                        .title("title")
                        .mileage(100500L)
                        .currency("currency")
                        .fuelType("fuelType")
                        .createdAt(LocalDateTime.now())
                        .details(List.of(new CarDetail("key", "value")))
                        .source(Car.Source.OLX)
                        .build()
        );

        List<Car> carsReturned = carService.saveAll(cars);

        assertThat(carsReturned)
                .allMatch(c -> !c.getDetails().isEmpty() && c.getDetails().stream().allMatch(detail -> detail.getCarId() > 0));

        assertThat(carRepository.findAll().stream().filter(c -> c.getUrl().startsWith("https://example")))
                .allMatch(c -> c.getDetails().size() == 1)
                .hasSize(2);

        assertThat(carDetailRepository.findAllByCarIdIn(carsReturned.stream().map(Car::getId).toList()))
                .hasSize(2);

        assertThat(cars.stream().map(Car::getUrl))
                .allMatch(url -> Objects.equals(requireNonNull(cache.get(url)).get(), true));
    }

    @Test
    void deleteAll_PosTC(){
        Cache cache = cacheManager.getCache(props.getCarUrl());
        requireNonNull(cache).put("https://www.example.com/audi-a4", true);

        carService.deleteAll(carRepository.findAllById(List.of(1L, 2L)));

        assertThat(carRepository.findAllById(List.of(1L, 2L)))
                .isEmpty();

        assertThat(carDetailRepository.findAllByCarIdIn(List.of(1L, 2L)))
                .isEmpty();

        assertThat(cache.get("https://www.example.com/audi-a4"))
                .isNotNull()
                .extracting(ValueWrapper::get)
                .isEqualTo(false);

        assertThat(cache.get("https://www.example.com/bmw-3"))
                .isNull();
    }

}