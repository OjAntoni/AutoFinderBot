package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.repository.CarDetailRepository;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.example.autofinderbot.domain.Report.Operation.DELETE;
import static com.example.autofinderbot.domain.Report.Operation.INSERT;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

class CarScheduledServiceTest extends BaseSpringBootTest {
    @Autowired
    CarScheduledService carScheduledService;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarDetailRepository carDetailRepository;

    @Autowired
    ReportRepository reportRepository;

    @Test
    void saveAllCars_PosTC(){
        long count = carRepository.count();

        carScheduledService.updateCarDatabase();

        assertThat(carRepository.count())
                .isEqualTo(count + 30);


        Optional<Report> report = reportRepository.findAll().stream().max(comparing(Report::getCreatedAt));

        assertThat(report)
                .isPresent()
                .get()
                .matches(r -> r.getOperation() == INSERT && r.getAffectedRows() == 30 && r.getTargetIds().size() == 30, "Report should contain car limit values");
    }

    @Test
    void deleteExpiredCars_PosTC(){
        carScheduledService.deleteExpiredCars();

        assertThat(carRepository.findAllById(List.of(4L, 5L, 6L)))
                .isEmpty();

        assertThat(carDetailRepository.findAllByCarIdIn(List.of(4L, 5L, 6L)))
                .isEmpty();

        Optional<Report> report = reportRepository.findAll().stream().max(comparing(Report::getCreatedAt));

        assertThat(report)
                .isPresent()
                .get()
                .matches(r -> r.getOperation() == DELETE && r.getAffectedRows() > 0);
    }
}