package com.example.autofinderbot.service;

import com.example.autofinderbot.common.config.telegram.components.TelegramRelated;
import com.example.autofinderbot.domain.Notification;
import com.example.autofinderbot.repository.NotificationRepository;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static com.example.autofinderbot.domain.Notification.State.NEW;
import static com.example.autofinderbot.domain.Notification.State.PROCESSED;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@TelegramRelated
public class NotificationService {
    TelegramClient telegramClient;
    NotificationRepository notificationRepository;
    Logger logger;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    void sendNotifications() {
        notificationRepository.findAllByState(NEW)
            .forEach(notification -> {
                notification.getUsers()
                    .forEach(user -> {
                        SendMessage message = SendMessage.builder()
                            .parseMode("Markdown")
                            .chatId(user.getChatId())
                            .text(markup(notification))
                            .build();
                        try {
                            telegramClient.execute(message);
                        } catch (TelegramApiException e) {
                            logger.warn("Error for user with id " + user.getId() + ": " + e.getMessage());
                        }
                    });
                notification.setState(PROCESSED);
                notificationRepository.save(notification);
            });
    }

    private String markup(Notification notification) {
        return String.format("*%s*\n\n%s", notification.getTitle(), notification.getDescription());
    }
}
