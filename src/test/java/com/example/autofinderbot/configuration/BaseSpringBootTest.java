package com.example.autofinderbot.configuration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public abstract class BaseSpringBootTest {
    static final String POSTGRES_IMAGE = "postgres:17.2";
    static final String POSTGRES_DB = "postgres";
    static final String POSTGRES_USERNAME = "postgres";
    static final String POSTGRES_PASSWORD = "postgres";

    @TempDir
    static Path tempDir;

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(POSTGRES_IMAGE))
            .withDatabaseName(POSTGRES_DB)
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD)
            .withExposedPorts(5432);

    static {
        POSTGRES_CONTAINER.setPortBindings(List.of("5433:5432"));
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) throws IOException {
        Path tempFilePath = tempDir.resolve("old-test.txt");
        Files.copy(Paths.get("src/test/resources/old"), tempFilePath);
        registry.add("telegram.bot.storage.file", tempFilePath::toString);

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/test/db.changelog-test.yaml");
    }
}
