package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.Message;
import com.example.autofinderbot.domain.Message.State;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.autofinderbot.domain.Message.State.DEFAULT;
import static com.example.autofinderbot.domain.Message.State.DESCRIPTION;
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
                .state(DEFAULT)
                .build();
        }
        message.setState(message.getState() == DEFAULT ? DESCRIPTION : DEFAULT);
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public State getMessageState(long chatId, long carId) {
        Message message = messageRepository.findByChatIdAndCarId(chatId, carId);
        return message != null ? message.getState() : DEFAULT;
    }
}
