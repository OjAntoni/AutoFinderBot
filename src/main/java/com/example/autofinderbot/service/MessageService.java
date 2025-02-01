package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.Message;
import com.example.autofinderbot.domain.Message.State;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.autofinderbot.domain.Message.State.*;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MessageService {
    MessageRepository messageRepository;

    @Transactional
    public void triggerDescription(User user, long carId) {
        Message message = messageRepository.findByChatIdAndCarId(user.getChatId(), carId);
        if(message == null) {
            message = Message.builder()
                .chatId(user.getChatId())
                .carId(carId)
                .descriptionState(DEFAULT)
                .detailsState(DEFAULT)
                .build();
        }
        message.setDescriptionState(message.getDescriptionState() == DEFAULT ? DESCRIPTION : DEFAULT);
        message.setDetailsState(DEFAULT);
        messageRepository.save(message);
    }

    @Transactional
    public void triggerDetails(User user, long carId) {
        Message message = messageRepository.findByChatIdAndCarId(user.getChatId(), carId);
        if(message == null) {
            message = Message.builder()
                .chatId(user.getChatId())
                .carId(carId)
                .detailsState(DEFAULT)
                .descriptionState(DEFAULT)
                .build();
        }
        message.setDetailsState(message.getDetailsState() == DEFAULT ? DETAILS : DEFAULT);
        message.setDescriptionState(DEFAULT);
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public State getMessageDescriptionState(long chatId, long carId) {
        Message message = messageRepository.findByChatIdAndCarId(chatId, carId);
        return message != null ? message.getDescriptionState() : DEFAULT;
    }

    @Transactional(readOnly = true)
    public State getMessageDetailsState(long chatId, long carId) {
        Message message = messageRepository.findByChatIdAndCarId(chatId, carId);
        return message != null ? message.getDetailsState() : DEFAULT;
    }
}
