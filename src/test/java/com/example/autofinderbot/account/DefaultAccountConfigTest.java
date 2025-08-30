package com.example.autofinderbot.account;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static com.example.autofinderbot.account.Account.Role.ADMIN;
import static org.mockito.Mockito.doReturn;

class DefaultAccountConfigTest extends BaseSpringBootTest {
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