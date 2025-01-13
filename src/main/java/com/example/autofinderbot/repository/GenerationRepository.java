package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Generation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenerationRepository extends JpaRepository<Generation, Long> {
    List<Generation> findAllBySearchKeyIn(List<String> searchKeys);
}
