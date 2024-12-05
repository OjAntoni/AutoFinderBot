package com.example.autofinderbot.repository;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarFileRepositoryTest extends BaseSpringBootTest {
    @TempDir
    static Path tempDir;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    CarFileRepository carFileRepository;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) throws IOException {
        Path tempFilePath = tempDir.resolve("test-file.txt");
        Files.copy(Paths.get("src/test/resources/old"), tempFilePath);
        registry.add("telegram.bot.storage.file", tempFilePath::toString);
    }

    @Test
    void loadCarsOnStartUp_PosTC() {
        assertThat(carFileRepository.contains("https://example.com/one"))
                .isTrue();
        assertThat(carFileRepository.contains("https://example.com/two"))
                .isTrue();
        assertThat(carFileRepository.contains("https://example.com/three"))
                .isTrue();
    }

    @Test
    void saveCarUrls_PosTC() {
        carFileRepository.saveUrls(List.of("https://example.com/four"));

        recreateCarFileRepository();

        assertThat(carFileRepository.contains("https://example.com/four"))
                .isTrue();
    }

    private void recreateCarFileRepository() {
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) context.getAutowireCapableBeanFactory();
        registry.destroySingleton("carFileRepository");

        CarFileRepository carFileRepositoryRecreated = new CarFileRepository(/* Pass required dependencies */);
        context.getAutowireCapableBeanFactory().autowireBean(carFileRepositoryRecreated);
        context.getAutowireCapableBeanFactory().initializeBean(carFileRepositoryRecreated, "carFileRepository");
        context.getBeanFactory().registerSingleton("carFileRepository", carFileRepositoryRecreated);

        context.getAutowireCapableBeanFactory().autowireBean(this);
    }
}