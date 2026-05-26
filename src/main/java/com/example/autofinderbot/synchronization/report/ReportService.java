package com.example.autofinderbot.synchronization.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
@Validated
public class ReportService {
    ReportRepository reportRepository;

    @Transactional
    public void save(@Valid Report report) {
        reportRepository.save(report);
    }
}
