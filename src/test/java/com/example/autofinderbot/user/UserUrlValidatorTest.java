package com.example.autofinderbot.user;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.autofinderbot.common.util.APIConstants.SEARCH_URL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserUrlValidatorTest extends BaseSpringBootTest {
    @Autowired
    UserUrlValidator userUrlValidator;

    @Test
    void throwOnEmptyUrl_NegTC() {
        String invalidUrl = "";

        assertThatThrownBy(() -> userUrlValidator.validate(invalidUrl))
                .isInstanceOf(InvalidSearchUrlException.class)
                .hasMessage("Url should be present.");
    }

    @Test
    void throwOnInvalidHostName_NegTC() {
        String invalidUrl = SEARCH_URL.replace("otomoto", "invalidHostName");

        assertThatThrownBy(() -> userUrlValidator.validate(invalidUrl))
                .isInstanceOf(InvalidSearchUrlException.class)
                .hasMessage("Url should start with 'https://www.otomoto.pl/'.");
    }

    @Test
    void throwOnTooLongUrl_NegTC() {
        String invalidUrl = SEARCH_URL + "x".repeat(2000);

        assertThatThrownBy(() -> userUrlValidator.validate(invalidUrl))
                .isInstanceOf(InvalidSearchUrlException.class)
                .hasMessage("Url is too long.");
    }
}