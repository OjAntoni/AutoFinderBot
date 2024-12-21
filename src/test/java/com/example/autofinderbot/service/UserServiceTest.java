package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.exception.InvalidUrlException;
import com.example.autofinderbot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends BaseSpringBootTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    void save_PosTC() throws InvalidUrlException {
        User user = new User();
        user.setChatId(123L);
        user.setSearchUrl("https://example.com");


        User savedUser = userService.save(user);

        assertThat(savedUser)
                .extracting(User::getChatId, User::getSearchUrl)
                .containsExactly(123L, "https://example.com");

        assertThat(userRepository.findById(savedUser.getId()))
                .isPresent()
                .get()
                .extracting(User::getChatId, User::getSearchUrl)
                .containsExactly(123L, "https://example.com");
    }

    @Test
    void throwOnInvalidUrl() {
        User user = new User();
        user.setChatId(123L);
        user.setSearchUrl("invalid url");

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessage("Provided URL 'invalid url' is invalid.");
    }

    @Test
    void delete_PosTC() {
        userService.delete(3L);

        assertThat(userRepository.findById(3L))
                .isEmpty();
    }
}
