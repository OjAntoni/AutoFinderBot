package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.exception.InvalidUrlException;
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
    DocumentService documentService;
    UserRepository userRepository;

    @Transactional
    public User save(User user) throws InvalidUrlException {
        if (!documentService.isValid(user.getSearchUrl())) {
            throw new InvalidUrlException(user.getSearchUrl());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }
}
