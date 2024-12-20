package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;
    @Column(nullable = false)
    long chatId;
    @Column(nullable = false)
    String searchUrl;
}
