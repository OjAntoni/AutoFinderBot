package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.CarDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CarDetailRepository extends JpaRepository<CarDetail, Long> {
    List<CarDetail> findAllByCarIdIn(Collection<Long> ids);
}
