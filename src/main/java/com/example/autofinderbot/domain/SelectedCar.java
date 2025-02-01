package com.example.autofinderbot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
@ToString
@Builder
public class SelectedCar {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;
    long userId;
    String url;
    long carId;
    LocalDateTime createdAt;
    @Column(name = "name")
    String carName;
    int messageId;
}
