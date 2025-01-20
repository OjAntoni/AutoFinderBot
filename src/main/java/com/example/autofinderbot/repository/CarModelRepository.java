package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    List<CarModel> findAllBySearchKeyIn(List<String> searchKeys);
}
