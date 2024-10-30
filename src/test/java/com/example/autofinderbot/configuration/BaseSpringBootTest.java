package com.example.autofinderbot.configuration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public abstract class BaseSpringBootTest {
}
