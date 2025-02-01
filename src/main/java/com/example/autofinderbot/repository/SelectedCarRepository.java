package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.SelectedCar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelectedCarRepository extends JpaRepository<SelectedCar, Long> {
    List<SelectedCar> findAllByUserIdOrderByCreatedAtDesc(long userId);
    SelectedCar findByUserIdAndMessageId(long userId, int messageId);
}
