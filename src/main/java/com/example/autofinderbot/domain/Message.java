package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;
    long chatId;
    long carId;
    @Setter
    @Getter
    @Enumerated(STRING)
    State descriptionState;
    @Setter
    @Getter
    @Enumerated(STRING)
    State detailsState;
    
    public enum State {
        DEFAULT,
        DESCRIPTION,
        DETAILS
    }
}
