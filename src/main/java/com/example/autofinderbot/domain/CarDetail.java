package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CarDetail{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Setter
    @Column(nullable = false)
    long carId;
    @Column(nullable = false)
    String detail;
    @Column(nullable = false)
    String value;

    public CarDetail(String detail, String value) {
        this.detail = detail;
        this.value = value;
    }
}
