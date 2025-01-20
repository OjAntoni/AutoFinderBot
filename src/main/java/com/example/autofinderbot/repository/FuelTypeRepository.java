package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {
    List<FuelType> findAllBySearchKeyIn(List<String> searchKeys);
}
