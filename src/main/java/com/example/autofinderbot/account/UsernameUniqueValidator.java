package com.example.autofinderbot.account;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UsernameUniqueValidator {
    AccountRepository accountRepository;

    public void validate(String username) {
        if (accountRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
    }
}
