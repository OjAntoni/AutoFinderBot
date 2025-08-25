package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Car;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    boolean existsByUrl(String url);
    List<Car> findAllByCreatedAtBefore(LocalDateTime dateTime);

    @NotNull
    @Override
    @EntityGraph(attributePaths = {"seller", "details"})
    Page<Car> findAll(Specification<Car> spec, Pageable pageable);

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

    @Query("""
        SELECT c.id AS id,
               (
                   SELECT AVG(c2.price)
                   FROM Car c2
                   JOIN c2.details md2
                   JOIN c2.details yd2
                   WHERE c2.brand = c.brand
                     AND LOWER(md2.detail) = 'model'
                     AND md2.value = md.value
                     AND LOWER(yd2.detail) = 'year'
                     AND yd2.value = yd.value
                     AND c2.mileage BETWEEN c.mileage * (1.0 - :tolerance) AND c.mileage * (1.0 + :tolerance)
                     AND c2.id <> c.id
               ) AS avgPrice
        FROM Car c
        JOIN c.details md
        JOIN c.details yd
        WHERE c.id IN :ids
          AND LOWER(md.detail) = 'model'
          AND LOWER(yd.detail) = 'year'
    """)
    List<CarAveragePrice> avgPriceForSimilarBulk(
        @Param("ids") List<Long> ids,
        @Param("tolerance") double tolerance
    );
}
