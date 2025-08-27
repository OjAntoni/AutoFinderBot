package com.example.autofinderbot.web.repository;

import com.example.autofinderbot.user.User;
import com.example.autofinderbot.web.dto.statistics.UserStatisticsResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserStatisticsRepository extends CrudRepository<User, Long> {
    @Query("""
    SELECT new com.example.autofinderbot.web.dto.statistics.UserStatisticsResponse(
        COUNT(u),
        COUNT(CASE WHEN u.lastActive >= :startOfToday THEN 1 END),
        COUNT(CASE WHEN u.lastActive >= :startOfThisWeek THEN 1 END),
        COUNT(uf),
        COUNT(CASE WHEN uf.active = true THEN 1 END)
    )
    FROM User u
    LEFT JOIN UserFilter uf ON uf.user = u
    """)
    UserStatisticsResponse getUserStatistics(LocalDateTime startOfToday, LocalDateTime startOfThisWeek);
}
