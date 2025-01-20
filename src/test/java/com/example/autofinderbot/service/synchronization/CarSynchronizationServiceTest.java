package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.repository.CarDetailRepository;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.repository.ReportRepository;
import com.example.autofinderbot.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.autofinderbot.domain.Report.Operation.DELETE;
import static com.example.autofinderbot.domain.Report.Operation.INSERT;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @SpyBean
    DocumentService documentService;

    @Test
    @DirtiesContext
    void saveAllCars_PosTC(){
        long count = carRepository.count();

        carSynchronizationService.updateCarDatabase();

        assertThat(carRepository.count())
                .isGreaterThan(count);

        Optional<Report> report = reportRepository.findAll().stream()
                .max(comparing(Report::getStartedAt));

        assertThat(report)
                .isPresent()
                .get()
                .matches(r -> r.getOperation() == INSERT && r.getAffectedRows() > 0 && !r.getTargetIds().isEmpty(),
                        "Report should contain inserted ids.");
    }

    @Test
    @DirtiesContext
    void saveCarsWithExceptionDuringPageLoad() throws IOException {
        reset(documentService);

        long count = carRepository.count();
        AtomicInteger invocationCounter = new AtomicInteger();

        doAnswer(invocation -> {
            int invocations = invocationCounter.incrementAndGet();
            if (invocations == 5 || invocations == 7 || invocations == 45) {
                throw new IOException();
            }
            return invocation.callRealMethod();
        }).when(documentService).load(anyString(), any());

        carSynchronizationService.updateCarDatabase();

        assertThat(carRepository.count())
                .isGreaterThan(count);
    }

    @Test
    void deleteExpiredCars_PosTC(){
        carSynchronizationService.deleteExpiredCars();

        assertThat(carRepository.findAllById(List.of(4L, 5L, 6L)))
                .isEmpty();

        assertThat(carDetailRepository.findAllByCarIdIn(List.of(4L, 5L, 6L)))
                .isEmpty();

        Optional<Report> report = reportRepository.findAll().stream()
                .max(comparing(Report::getStartedAt));

        assertThat(report)
                .isPresent()
                .get()
                .matches(r -> r.getOperation() == DELETE && r.getAffectedRows() > 0);
    }
}