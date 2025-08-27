package com.example.autofinderbot.domain;

import com.example.autofinderbot.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;
    String description;
    @OneToMany
    @JoinTable(name = "notification_2_user", joinColumns = @JoinColumn(name = "notification_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<User> users;
    @Enumerated(STRING)
    State state;

    public static enum State {
        NEW, PROCESSED
    }
}
