package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.parser.CarParserService;
import com.example.autofinderbot.parser.olx.OlxCarParserService;
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
import static com.example.autofinderbot.shared.APIConstants.*;
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
    OlxCarParserService olxCarParserService;
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

        if (newCars.size() < CAR_LIMIT) {
            page = 1;
            while (newCars.size() < CAR_LIMIT && page <= MAX_PAGE_SIZE) {
                List<Car> olxCars;
                try {
                    olxCars = olxCarParserService.findCars(OLX_SEARCH_URL(page++));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    break;
                }

                logger.debug("Found OLX cars on page %d: %d", page - 1, olxCars.size());

                List<Car> filtered = olxCars.stream()
                        .filter(car -> car.getUrl() != null && !car.getUrl().contains("otomoto"))
                        .collect(Collectors.toMap(Car::getUrl, c -> c, (a, b) -> a))
                        .values()
                        .stream()
                        .filter(car -> !carService.exists(car.getUrl()))
                        .limit(newCars.size() + olxCars.size() > CAR_LIMIT ? CAR_LIMIT - newCars.size() : olxCars.size())
                        .toList();

                newCars.addAll(carService.saveAll(filtered));
                logger.debug("Filtered out %d OLX cars from page %d", filtered.size(), page - 1);

                if (filtered.size() != olxCars.size()) {
                    break;
                }
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
