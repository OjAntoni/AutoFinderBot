package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;
    @Column(nullable = false)
    long chatId;
    String redirectTo;
    String firstname;
    String lastname;
    String username;
    String languageCode;
    LocalDateTime lastActive;
}
