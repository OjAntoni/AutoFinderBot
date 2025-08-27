package com.example.autofinderbot.user;

import com.example.autofinderbot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByChatId(long chatId);
    boolean existsByChatId(long id);
}
