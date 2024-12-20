package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.CarDetail;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.repository.CarDetailRepository;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.example.autofinderbot.domain.Report.Operation.DELETE;
import static com.example.autofinderbot.domain.Report.Operation.INSERT;
import static java.util.Comparator.comparing;
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

    @Test
    void existsByUrl_PosTC() {
        assertThat(carService.exists("https://www.example.com/audi-a4"))
                .isTrue();
    }

    @Test
    void saveAll_PosTC() {
        List<Car> cars = List.of(
                Car.builder()
                        .url("https://example.com")
                        .brand("brand")
                        .price(1000)
                        .title("title")
                        .mileage(100500)
                        .currency("currency")
                        .fuelType("fuelType")
                        .createdAt(LocalDateTime.now())
                        .details(List.of(new CarDetail("key", "value")))
                        .build(),
                Car.builder()
                        .url("https://example.com")
                        .brand("brand")
                        .price(1000)
                        .title("title")
                        .mileage(100500)
                        .currency("currency")
                        .fuelType("fuelType")
                        .createdAt(LocalDateTime.now())
                        .details(List.of(new CarDetail("key", "value")))
                        .build()
        );

        List<Car> carsReturned = carService.saveAll(cars);

        assertThat(carsReturned)
                .allMatch(c -> !c.getDetails().isEmpty() && c.getDetails().stream().allMatch(detail -> detail.getCarId() > 0));

        assertThat(carRepository.findAll().stream().filter(c -> c.getUrl().equals("https://example.com")))
                .allMatch(c -> c.getDetails().size() == 1)
                .hasSize(2);

        assertThat(carDetailRepository.findAllByCarIdIn(carsReturned.stream().map(Car::getId).toList()))
                .hasSize(2);
    }

    @Test
    void deleteAll_PosTC(){
        carService.deleteAll(List.of(1L, 2L));

        assertThat(carRepository.findAllById(List.of(1L, 2L)))
                .isEmpty();

        assertThat(carDetailRepository.findAllByCarIdIn(List.of(1L, 2L)))
                .isEmpty();
    }

}