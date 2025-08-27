package com.example.autofinderbot.user.history;

import com.example.autofinderbot.user.User;
import com.example.autofinderbot.common.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserHistoryService {
    UserHistoryRepository userHistoryRepository;
    UserToUserHistoryMapper userToUserHistoryMapper;
    DateTimeUtil dateTimeUtil;

    public void save(User user, String command, String data) {
        UserHistory userHistory = userToUserHistoryMapper.map(user);
        userHistory.setCommand(command);
        userHistory.setData(data);
        userHistory.setUpdatedAt(dateTimeUtil.now());

        userHistoryRepository.save(userHistory);
    }
}
