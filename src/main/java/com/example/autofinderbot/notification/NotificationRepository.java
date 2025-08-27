package com.example.autofinderbot.notification;

import com.example.autofinderbot.notification.Notification.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByState(State state);
}
