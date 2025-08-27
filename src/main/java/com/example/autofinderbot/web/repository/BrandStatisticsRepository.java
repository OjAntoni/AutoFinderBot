package com.example.autofinderbot.web.repository;

import com.example.autofinderbot.car.Car;
import com.example.autofinderbot.web.dto.statistics.DayCount;
import com.example.autofinderbot.web.dto.statistics.DayPrice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BrandStatisticsRepository extends CrudRepository<Car, Long> {

    @Query("""
        SELECT new com.example.autofinderbot.web.dto.statistics.DayPrice(
            cast(c.createdAt as date),
            AVG(c.price)
        )
        FROM Car c
        WHERE c.brand = :brand
          AND c.createdAt >= :startDate
        GROUP BY cast(c.createdAt as date)
        ORDER BY cast(c.createdAt as date)
        """)
    List<DayPrice> averagePriceByBrand(String brand, LocalDateTime startDate);

    @Query("""
        SELECT new com.example.autofinderbot.web.dto.statistics.DayCount(
            cast(c.createdAt as date),
            COUNT(c)
        )
        FROM Car c
        WHERE c.brand = :brand
          AND c.createdAt >= :startDate
        GROUP BY cast(c.createdAt as date)
        ORDER BY cast(c.createdAt as date)
        """)
    List<DayCount> offersCountByBrand(String brand, LocalDateTime startDate);
}
