package com.example.autofinderbot.service.synchronization;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.domain.Report;
import com.example.autofinderbot.parser.CarParserService;
import com.example.autofinderbot.repository.CarRepository;
import com.example.autofinderbot.service.CarService;
import com.example.autofinderbot.service.DocumentService;
import com.example.autofinderbot.service.ReportService;
import com.example.autofinderbot.shared.DateTimeUtil;
import com.example.autofinderbot.shared.Logger;
import com.example.autofinderbot.shared.NewCarsEvent;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    ApplicationEventPublisher eventPublisher;
    DocumentService documentService;
    CarParserService carParserService;
    CarService carService;
    ReportService reportService;
    DateTimeUtil dateTimeUtil;
    Logger logger;

    @Scheduled(fixedRate = 10, initialDelay = 1, timeUnit = MINUTES)
    void updateCarDatabase() {
        List<Car> newCars = new ArrayList<>();
        int page = 1;
        while (newCars.size() < CAR_LIMIT) {

            List<Car> cars;

            try {
                cars = carParserService.findCars(SEARCH_URL(page++));
            } catch (IOException e) {
                logger.error(e);
                break;
            }

            logger.debug("Found car responses: %d", cars.size());
            List<Car> filtered = cars.stream()
                    .filter(cr -> !carService.exists(cr.getUrl()))
                    .limit(newCars.size() + cars.size() > CAR_LIMIT ? CAR_LIMIT - newCars.size() : cars.size())
                    .toList();

            newCars.addAll(carService.saveAll(filtered));
            logger.debug("Added filtered cars: %d", filtered.size());

            if(filtered.size() != cars.size()) break;
        }

        logger.info("Sending an event.");
        Mono.fromRunnable(() -> eventPublisher.publishEvent(new NewCarsEvent(this, newCars))).subscribe();

        Report report = new Report();
        report.setAffectedRows(newCars.size());
        report.setCreatedAt(dateTimeUtil.now());
        report.setOperation(INSERT);
        report.setTargetIds(newCars.stream().map(Car::getId).toList());
        reportService.save(report);
    }

    @Scheduled(fixedRate = 60, initialDelay = 30, timeUnit = MINUTES)
    void deleteExpiredCars() {
        Sort sort = Sort.by(Sort.Order.asc("createdAt"));
        int pageSize = 50;
        int page = 0;
        long deletedCars = 0;

        Page<Car> cars = carService.findAll(PageRequest.of(page, pageSize, sort));
        while (cars.hasContent()) {
            List<Long> expiredCarIds = cars.stream()
                    .parallel()
                    .filter(car -> !documentService.isValid(car.getUrl()))
                    .map(Car::getId)
                    .toList();

            carService.deleteAll(expiredCarIds);
            deletedCars += expiredCarIds.size();

            cars = carService.findAll(PageRequest.of(++page, pageSize, sort));
        }

        Report report = new Report();
        report.setAffectedRows(deletedCars);
        report.setCreatedAt(dateTimeUtil.now());
        report.setOperation(DELETE);
        reportService.save(report);
    }
}
