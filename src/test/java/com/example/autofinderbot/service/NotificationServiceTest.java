package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.Notification;
import com.example.autofinderbot.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.example.autofinderbot.domain.Notification.State.PROCESSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationServiceTest extends BaseSpringBootTest {
    @Autowired
    NotificationRepository notificationRepository;
    @MockitoBean
    NotificationService notificationService;

    @Test
    void sendNotificationOnStartUp_PosTC(){
        verify(notificationService, times(1)).sendNotifications();
        assertThat(notificationRepository.findById(2L))
                .isPresent()
                .get()
                .extracting(Notification::getState)
                .isEqualTo(PROCESSED);
    }
}