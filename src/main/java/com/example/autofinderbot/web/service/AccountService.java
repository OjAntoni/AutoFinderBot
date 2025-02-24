package com.example.autofinderbot.web.service;

import com.example.autofinderbot.web.domain.Account;
import com.example.autofinderbot.web.dto.account.CreateAccountRequest;
import com.example.autofinderbot.web.repository.AccountRepository;
import com.example.autofinderbot.web.validation.account.UsernameUniqueValidator;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static com.example.autofinderbot.web.domain.Account.Role.ADMIN;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountService {
    AccountRepository accountRepository;
    UsernameUniqueValidator usernameUniqueValidator;

    public void createAccount(CreateAccountRequest createAccountRequest) {
        usernameUniqueValidator.validate(createAccountRequest.username());

        Account account = Account.builder()
            .username(createAccountRequest.username())
            .password(createAccountRequest.password())
            .role(ADMIN)
            .build();

        accountRepository.save(account);
    }
}
