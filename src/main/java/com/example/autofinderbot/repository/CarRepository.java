package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByUrl(String url);
    long countAllByIdIn(Collection<Long> ids);
}
