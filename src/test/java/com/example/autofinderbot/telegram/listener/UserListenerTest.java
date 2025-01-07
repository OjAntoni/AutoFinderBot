package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.repository.UserRepository;
import com.example.autofinderbot.telegram.exception.InvalidSearchUrlException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserListenerTest extends BaseTelegramListenerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    //TODO remove when listener logic will be replaced
    UserUrlValidator userUrlValidator;

    @Test
    void registerUser_PosTC() {
        Mockito.when(update.getMessage().getChatId()).thenReturn(12345L);

        strategyContext.executeStrategy("/register", update);

        assertThat(findByChatId(12345L))
                .isPresent()
                .get()
                .extracting(User::getSearchUrl)
                .isEqualTo(SEARCH_URL);
    }

    @Test
    void throwOnEmptyUrl_NegTC() {
        String invalidUrl = "";

        assertThatThrownBy(() -> userUrlValidator.validate(invalidUrl))
                .isInstanceOf(InvalidSearchUrlException.class)
                .hasMessage("Search url is invalid because url should be present.");
    }

    @Test
    void throwOnInvalidHostName_NegTC() {
        String invalidUrl = SEARCH_URL.replace("otomoto", "invalidHostName");

        assertThatThrownBy(() -> userUrlValidator.validate(invalidUrl))
                .isInstanceOf(InvalidSearchUrlException.class)
                .hasMessage("Search url is invalid because url should start with 'https://www.otomoto.pl/'.");
    }

    @Test
    void throwOnNotSortedCars_NegTC() {
        String invalidUrl = SEARCH_URL.replace("order%5D=created_at_first", "");

        assertThatThrownBy(() -> userUrlValidator.validate(invalidUrl))
                .isInstanceOf(InvalidSearchUrlException.class)
                .hasMessage("Search url is invalid because cars should be sorted by creation date ascending.");
    }

    @Test
    void throwOnTooLongUrl_NegTC() {
        String invalidUrl = SEARCH_URL + "x".repeat(2000);

        assertThatThrownBy(() -> userUrlValidator.validate(invalidUrl))
                .isInstanceOf(InvalidSearchUrlException.class)
                .hasMessage("Search url is invalid because url is too long.");
    }

    private Optional<User> findByChatId(long chatId){
        return userRepository.findAll().stream()
                .filter(u -> u.getChatId() == chatId)
                .findFirst();
    }
}