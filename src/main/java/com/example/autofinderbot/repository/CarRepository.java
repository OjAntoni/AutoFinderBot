package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    boolean existsByUrl(String url);
    List<Car> findAllByCreatedAtBefore(LocalDateTime dateTime);
}
