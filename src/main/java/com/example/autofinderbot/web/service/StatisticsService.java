package com.example.autofinderbot.web.service;

import com.example.autofinderbot.shared.DateTimeUtil;
import com.example.autofinderbot.web.dto.statistics.UserStatisticsResponse;
import com.example.autofinderbot.web.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StatisticsService {
    UserStatisticsRepository userStatisticsRepository;
    DateTimeUtil dateTimeUtil;

    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics() {
        LocalDateTime startOfToday = dateTimeUtil.now().toLocalDate().atStartOfDay();
        LocalDateTime startOfThisWeek = dateTimeUtil.now().toLocalDate().minusWeeks(1).atStartOfDay();
        return userStatisticsRepository.getUserStatistics(startOfToday, startOfThisWeek);
    }
}
