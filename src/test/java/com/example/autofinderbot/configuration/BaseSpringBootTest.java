package com.example.autofinderbot.configuration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public abstract class BaseSpringBootTest {
    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) throws IOException {
        Path tempFilePath = tempDir.resolve("old-test.txt");
        Files.copy(Paths.get("src/test/resources/old"), tempFilePath);
        registry.add("telegram.bot.storage.file", tempFilePath::toString);
    }
}
