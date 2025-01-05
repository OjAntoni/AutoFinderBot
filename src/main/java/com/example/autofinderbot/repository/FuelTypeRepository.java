package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {
}
