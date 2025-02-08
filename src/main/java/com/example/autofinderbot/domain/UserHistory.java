package com.example.autofinderbot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserHistory {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;
    long userId;
    long chatId;
    String redirectTo;
    String firstname;
    String lastname;
    String username;
    String languageCode;
    String command;
    String data;
    LocalDateTime updatedAt;
}
