package com.example.autofinderbot.car.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByChatIdAndCarId(long chatId, long carId);
}
