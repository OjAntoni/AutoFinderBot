package com.example.autofinderbot.user;

import com.example.autofinderbot.user.history.UserHistory;
import com.example.autofinderbot.user.history.UserHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WebUserHistoryService {
    UserHistoryRepository userHistoryRepository;

    public Page<UserHistory> getUserHistory(Pageable pageable) {
        return userHistoryRepository.findAll(pageable);
    }
}
