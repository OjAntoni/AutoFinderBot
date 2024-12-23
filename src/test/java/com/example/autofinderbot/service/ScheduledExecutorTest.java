package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledExecutorTest extends BaseSpringBootTest {
    @Autowired
    ScheduledExecutor scheduledExecutor;

    @Test
    void execute_PosTC() {
        assertDoesNotThrow(() -> scheduledExecutor.execute());
    }
}