package com.example.autofinderbot.configuration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Transactional
public abstract class BaseSpringBootTest {
    static final String POSTGRES_IMAGE = "postgres:17.2";
    static final String POSTGRES_DB = "postgres";
    static final String POSTGRES_USERNAME = "postgres";
    static final String POSTGRES_PASSWORD = "postgres";

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(POSTGRES_IMAGE))
            .withDatabaseName(POSTGRES_DB)
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD)
            .withExposedPorts(5430);

    static {
        POSTGRES_CONTAINER.setPortBindings(List.of("5430:5432"));
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry){
        registry.add("telegram.bot.token", () -> "token");

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/test/db.changelog-test.yaml");

        registry.add("app.jwt.secret", () -> "jwtSecret".repeat(30));
    }
}
