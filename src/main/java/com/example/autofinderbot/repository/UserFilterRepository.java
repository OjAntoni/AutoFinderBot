package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.UserFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Long> {
    UserFilter findByUser_Id(long userId);
    boolean existsByUser_Id(long userId);
    void deleteByUser_Id(long userId);
    List<UserFilter> findAllByConfirmedIs(boolean confirmed);
}
