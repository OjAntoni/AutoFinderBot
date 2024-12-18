package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.CarResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarResponseRepository extends JpaRepository<CarResponse, Long> {
    boolean existsByUrl(String url);
}
