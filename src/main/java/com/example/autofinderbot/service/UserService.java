package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.domain.UserFilter;
import com.example.autofinderbot.exception.InvalidUrlException;
import com.example.autofinderbot.repository.UserFilterRepository;
import com.example.autofinderbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserRepository userRepository;
    UserFilterRepository userFilterRepository;

    @Transactional
    public User save(User user){
        return userRepository.save(user);
    }

    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User findByChatId(long chatId) {
        return userRepository.getByChatId(chatId);
    }

    @Transactional
    public UserFilter save(UserFilter userFilter) throws InvalidUrlException {
        //TODO add validation on non null user in filter
        if (userFilterRepository.existsByUser_Id(userFilter.getUser().getId())) {
            userFilterRepository.deleteByUser_Id(userFilter.getUser().getId());
        }

        return userFilterRepository.save(userFilter);
    }

    public UserFilter findUserFilter(long userId) {
        return userFilterRepository.findByUser_Id(userId);
    }

    public void deleteFilter(long id) {
        userFilterRepository.deleteById(id);
    }

    public boolean existsByChatId(Long chatId) {
        return userRepository.existsByChatId(chatId);
    }
}
