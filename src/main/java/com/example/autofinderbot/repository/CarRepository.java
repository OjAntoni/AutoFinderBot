package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    boolean existsByUrl(String url);
    List<Car> findAllByCreatedAtBefore(LocalDateTime dateTime);

    @Query("""
        SELECT AVG(c.price)
        FROM Car c
        JOIN c.details modelDetail
        JOIN c.details yearDetail
        WHERE c.brand = :brand
          AND modelDetail.detail = 'Model'
          AND modelDetail.value = :model
          AND yearDetail.detail = 'Year'
          AND yearDetail.value = :year
          AND c.mileage BETWEEN :minMileage AND :maxMileage
    """)
    Double avgPriceForSimilar(
        @Param("brand") String brand,
        @Param("model") String model,
        @Param("year") String year,
        @Param("minMileage") long minMileage,
        @Param("maxMileage") long maxMileage
    );

    @Query("""
        SELECT DISTINCT c
        FROM Car c
        JOIN c.details modelDetail
        JOIN c.details yearDetail
        WHERE c.brand = :brand
          AND LOWER(modelDetail.detail) = 'model'
          AND modelDetail.value = :model
          AND LOWER(yearDetail.detail) = 'year'
          AND yearDetail.value = :year
          AND c.mileage BETWEEN :minMileage AND :maxMileage
          AND c.id <> :carId
    """)
    List<Car> findSimilarCars(
        @Param("brand") String brand,
        @Param("model") String model,
        @Param("year") String year,
        @Param("minMileage") long minMileage,
        @Param("maxMileage") long maxMileage,
        @Param("carId") long carId
    );
}
