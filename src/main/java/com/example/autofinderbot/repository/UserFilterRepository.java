package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.UserFilter;
import com.example.autofinderbot.domain.UserFilter.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.autofinderbot.domain.UserFilter.State.NEW;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Long> {
    UserFilter findByUser_Id(long userId);
    boolean existsByUser_IdAndState(long userId, State state);
    List<UserFilter> findAllByConfirmedIsAndActive(boolean confirmed, boolean active);
    void deleteByUser_IdAndState(long userId, State state);
    UserFilter findByUser_IdAndState(long userId, State state);

    default UserFilter findActiveFilter(long userId) {
        return findByUser_IdAndStateAndConfirmedAndActive(userId, NEW, true, true);
    }

    default UserFilter findStoppedFilter(long userId) {
        return findByUser_IdAndStateAndConfirmedAndActive(userId, NEW, true, false);
    }

    UserFilter findByUser_IdAndStateAndConfirmedAndActive(long userId, State state, boolean confirmed, boolean active);
}
