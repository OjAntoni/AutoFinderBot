package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Notification;
import com.example.autofinderbot.domain.Notification.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByState(State state);
}
