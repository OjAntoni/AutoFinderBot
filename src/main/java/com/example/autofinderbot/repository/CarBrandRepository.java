package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {
}
