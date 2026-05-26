package com.example.autofinderbot.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = PRIVATE)
public class Account {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;
    @Column(unique = true)
    String username;
    String password;
    @Enumerated(STRING)
    Role role;

    public enum Role {
        ADMIN
    }
}
