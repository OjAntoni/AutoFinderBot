package com.example.autofinderbot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    long id;
    @Column(nullable = false)
    long carResponseId;
    @Column(nullable = false)
    String detail;
    @Column(nullable = false)
    String value;

    public CarDetail(String detail, String value) {
        this.detail = detail;
        this.value = value;
    }
}
