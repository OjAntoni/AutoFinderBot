package com.example.autofinderbot.synchronization;

import com.example.autofinderbot.car.Car;
import com.example.autofinderbot.parser.service.ScraperService;
import com.example.autofinderbot.car.CarService;
import com.example.autofinderbot.synchronization.report.ReportService;
import com.example.autofinderbot.common.util.DateTimeUtil;
import com.example.autofinderbot.common.util.Logger;
import com.example.autofinderbot.common.event.NewCarsEvent;
import com.example.autofinderbot.synchronization.report.Report;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.autofinderbot.synchronization.report.Report.Operation.DELETE;
import static com.example.autofinderbot.synchronization.report.Report.Operation.INSERT;
import static java.util.concurrent.TimeUnit.MINUTES;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarSynchronizationService {
    private static final int CAR_LIMIT = 100;
    private static final int MAX_PAGE_SIZE = 2;

    ApplicationEventPublisher eventPublisher;
    List<ScraperService<Car>> scrapers;
    CarService carService;
    ReportService reportService;
    DateTimeUtil dateTimeUtil;
    Logger logger;

    @Scheduled(fixedRate = 10, initialDelay = 1, timeUnit = MINUTES)
    void updateCarDatabase() {
        Report report = new Report();
        report.setStartedAt(dateTimeUtil.now());

        List<Car> newCars = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();

        outer:
        for (ScraperService<Car> scraper : scrapers) {
            int page = 1;

            while (newCars.size() < CAR_LIMIT && page <= MAX_PAGE_SIZE) {
                List<Car> cars;

                try {
                    cars = scraper.scrape(scraper.getSearchUrl(page++));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    break;
                }

                logger.debug("Found cars on page %d: %d", page-1, cars.size());
                List<Car> filtered = cars.stream()
                    .collect(Collectors.toMap(
                        Car::getUrl,
                        car -> car
                    ))
                    .values()
                    .stream()
                    .filter(car -> !carService.exists(car.getUrl()))
                    .filter(car -> seenUrls.add(car.getUrl()))
                    .limit(newCars.size() + cars.size() > CAR_LIMIT ? CAR_LIMIT - newCars.size() : cars.size())
                    .toList();

                newCars.addAll(carService.saveAll(filtered));
                logger.debug("Filtered out %d cars from page %d", filtered.size(), page-1);

                if (filtered.size() != cars.size()) break;
                if (newCars.size() > CAR_LIMIT) break outer;
            }
        }

        Mono.fromRunnable(() -> eventPublisher.publishEvent(new NewCarsEvent(this, newCars))).subscribe();

        report.setAffectedRows(newCars.size());
        report.setFinishedAt(dateTimeUtil.now());
        report.setOperation(INSERT);
        report.setTargetIds(newCars.stream().map(Car::getId).toList());
        reportService.save(report);
    }

    @Scheduled(fixedRate = 60, initialDelay = 1, timeUnit = MINUTES)
    void deleteExpiredCars() {
        Report report = new Report();
        report.setStartedAt(dateTimeUtil.now());

        List<Car> expiredCars = carService.findExpired();
        carService.deleteAll(expiredCars);

        report.setAffectedRows(expiredCars.size());
        report.setFinishedAt(dateTimeUtil.now());
        report.setOperation(DELETE);
        reportService.save(report);
    }
}
