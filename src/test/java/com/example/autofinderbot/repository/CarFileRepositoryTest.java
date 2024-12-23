package com.example.autofinderbot.repository;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarFileRepositoryTest extends BaseSpringBootTest {

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    CarFileRepository carFileRepository;

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

        assertThat(carFileRepository.contains("https://example.com/four2"))
                .isTrue();
    }

    private void recreateCarFileRepository() {
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) context.getAutowireCapableBeanFactory();
        registry.destroySingleton("carFileRepository");

        CarFileRepository carFileRepositoryRecreated = new CarFileRepository();
        context.getAutowireCapableBeanFactory().autowireBean(carFileRepositoryRecreated);
        context.getAutowireCapableBeanFactory().initializeBean(carFileRepositoryRecreated, "carFileRepository");
        context.getBeanFactory().registerSingleton("carFileRepository", carFileRepositoryRecreated);

        context.getAutowireCapableBeanFactory().autowireBean(this);
    }
}