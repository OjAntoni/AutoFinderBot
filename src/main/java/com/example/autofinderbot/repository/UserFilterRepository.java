package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.UserFilter;
import com.example.autofinderbot.domain.UserFilter.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Long> {
    UserFilter findByUser_Id(long userId);
    boolean existsByUser_IdAndState(long userId, State state);
    List<UserFilter> findAllByConfirmedIs(boolean confirmed);
    void deleteByUser_IdAndState(long userId, State state);
    UserFilter findByUser_IdAndState(long userId, State state);
}
