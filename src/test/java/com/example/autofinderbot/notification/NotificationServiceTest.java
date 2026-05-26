package com.example.autofinderbot.notification;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.example.autofinderbot.notification.Notification.State.PROCESSED;
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