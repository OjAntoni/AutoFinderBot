package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.parser.CarParserService;
import com.example.autofinderbot.service.CarService;
import com.example.autofinderbot.service.ReportService;
import com.example.autofinderbot.shared.DateTimeUtil;
import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.shared.NewCarsEvent;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.autofinderbot.domain.Report.Operation.DELETE;
import static com.example.autofinderbot.domain.Report.Operation.INSERT;
import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static java.util.concurrent.TimeUnit.MINUTES;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarSynchronizationService {
    private static final int CAR_LIMIT = 60;
    private static final int MAX_PAGE_SIZE = CAR_LIMIT / 30 + 1;

    ApplicationEventPublisher eventPublisher;
    CarParserService carParserService;
    CarService carService;
    ReportService reportService;
    DateTimeUtil dateTimeUtil;
    Logger logger;

    @Scheduled(fixedRate = 10, initialDelay = 1, timeUnit = MINUTES)
    void updateCarDatabase() {
        Report report = new Report();
        report.setStartedAt(dateTimeUtil.now());

        List<Car> newCars = new ArrayList<>();
        int page = 1;
        while (newCars.size() < CAR_LIMIT && page <= MAX_PAGE_SIZE) {

            List<Car> cars;

            try {
                cars = carParserService.findCars(SEARCH_URL(page++));
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
                    .limit(newCars.size() + cars.size() > CAR_LIMIT ? CAR_LIMIT - newCars.size() : cars.size())
                    .toList();

            newCars.addAll(carService.saveAll(filtered));
            logger.debug("Filtered out %d cars from page %d", filtered.size(), page-1);

            if(filtered.size() != cars.size()) break;
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
        List<Long> expiredCarIds = expiredCars.stream().map(Car::getId).toList();
        carService.deleteAll(expiredCarIds);

        report.setAffectedRows(expiredCars.size());
        report.setFinishedAt(dateTimeUtil.now());
        report.setOperation(DELETE);
        reportService.save(report);
    }
}
