package com.example.autofinderbot.web.config;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.web.domain.Account;
import com.example.autofinderbot.web.repository.AccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static com.example.autofinderbot.web.domain.Account.Role.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class DefaultUserConfigTest extends BaseSpringBootTest {
    @MockitoSpyBean
    AccountRepository accountRepository;
    @Autowired
    CommandLineRunner commandLineRunner;

    @Test
    void injectInitialUser() throws Exception {
        doReturn(0L).when(accountRepository).count();

        commandLineRunner.run();

        Assertions.assertThat(accountRepository.findByUsername("admin"))
            .isPresent()
            .get()
            .extracting(
                Account::getUsername,
                Account::getPassword,
                Account::getRole
            ).contains(
                "admin",
                "admin",
                ADMIN
            );
    }
}