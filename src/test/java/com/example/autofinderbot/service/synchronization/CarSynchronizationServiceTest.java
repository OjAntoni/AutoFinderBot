package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.repository.CarDetailRepository;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.repository.ReportRepository;
import com.example.autofinderbot.service.synchronization.CarSynchronizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.autofinderbot.domain.Report.Operation.DELETE;
import static com.example.autofinderbot.domain.Report.Operation.INSERT;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

class CarSynchronizationServiceTest extends BaseSpringBootTest {
    private static final int CAR_LIMIT = 60;

    @Autowired
    CarSynchronizationService carSynchronizationService;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarDetailRepository carDetailRepository;

    @Autowired
    ReportRepository reportRepository;

    @Test
//    @Transactional
    void saveAllCars_PosTC(){
        long count = carRepository.count();

        carSynchronizationService.updateCarDatabase();

        assertThat(carRepository.count())
                .isEqualTo(count + CAR_LIMIT);

        Optional<Report> report = reportRepository.findAll().stream().max(comparing(Report::getCreatedAt));

        assertThat(report)
                .isPresent()
                .get()
                .matches(r -> r.getOperation() == INSERT && r.getAffectedRows() == CAR_LIMIT && r.getTargetIds().size() == CAR_LIMIT, "Report should contain car limit values");
    }

    @Test
    void deleteExpiredCars_PosTC(){
        carSynchronizationService.deleteExpiredCars();

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