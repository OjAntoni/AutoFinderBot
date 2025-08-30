package com.example.autofinderbot.account;

import com.example.autofinderbot.common.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.autofinderbot.account.Account.Role.ADMIN;

@Configuration
@RequiredArgsConstructor
public class DefaultAccountConfig {
    @Value("${default.username:admin}")
    private String defaultUsername;
    @Value("${default.password:admin}")
    private String defaultPassword;

    private final Logger logger;

    @Bean
    public CommandLineRunner initUsers(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (accountRepository.count() == 0) {
                Account defaultAccount = Account.builder()
                    .password(passwordEncoder.encode(defaultPassword))
                    .username(defaultUsername)
                    .role(ADMIN)
                    .build();

                accountRepository.save(defaultAccount);
                logger.info("Default admin user created: username=%s, password=%s", defaultUsername, defaultPassword);
            }
        };
    }
}
