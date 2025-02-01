package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByChatIdAndCarId(long chatId, long carId);
}
