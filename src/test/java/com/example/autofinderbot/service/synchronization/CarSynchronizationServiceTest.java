package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.repository.CarDetailRepository;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.repository.ReportRepository;
import com.example.autofinderbot.service.CarService;
import com.example.autofinderbot.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.example.autofinderbot.config.CacheConfig.CAR_URLS_CACHE;
import static com.example.autofinderbot.domain.Report.Operation.DELETE;
import static com.example.autofinderbot.domain.Report.Operation.INSERT;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CarSynchronizationServiceTest extends BaseSpringBootTest {
    @Autowired
    CarSynchronizationService carSynchronizationService;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarService carService;

    @Autowired
    CarDetailRepository carDetailRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    CacheManager cacheManager;

    @SpyBean
    DocumentService documentService;

    @Test
    @DirtiesContext
    void saveAllCars_PosTC(){
        Cache cache = cacheManager.getCache(CAR_URLS_CACHE);
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

        assertThat(carRepository.findAllById(report.get().getTargetIds()).stream().map(Car::getUrl).toList())
                .allMatch(url -> requireNonNull(cache).get(url) != null);
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
        Cache cache = cacheManager.getCache(CAR_URLS_CACHE);
        carService.exists("https://www.otomoto.pl/osobowe/oferta/invalid-url-1");
        carService.exists("https://www.otomoto.pl/osobowe/oferta/invalid-url-2");
        carService.exists("https://www.otomoto.pl/osobowe/oferta/invalid-url-3");

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

        assertThat(Stream.of(
                "https://www.otomoto.pl/osobowe/oferta/invalid-url-1",
                "https://www.otomoto.pl/osobowe/oferta/invalid-url-2",
                "https://www.otomoto.pl/osobowe/oferta/invalid-url-3"))
                .allMatch(url -> Objects.equals(requireNonNull(requireNonNull(cache).get(url)).get(), false));
    }
}