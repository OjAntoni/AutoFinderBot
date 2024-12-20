package com.example.autofinderbot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Collection;

import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    long affectedRows;
    LocalDateTime createdAt;
    @Column(name = "target_id")
    @ElementCollection
    @CollectionTable(name = "target_ids_2_report", joinColumns = @JoinColumn(name = "report_id"))
    Collection<Long> targetIds;
    @Enumerated(EnumType.STRING)
    Operation operation;

    public enum Operation {
        INSERT, DELETE
    }
}
