package com.example.autofinderbot.common.config.web;

import com.example.autofinderbot.common.util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private JwtTokenProvider createProvider(long expirationMs) {
        String secret = "jwtSecret".repeat(9); // length > 64 bytes
        return new JwtTokenProvider(secret, expirationMs, new DateTimeUtil());
    }

    @Test
    void generateAndParseToken_PosTC() {
        JwtTokenProvider provider = createProvider(1000);
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null);

        JwtTokenProvider.JwtToken token = provider.generateToken(auth);

        assertThat(provider.getUsernameFromJWT(token.accessToken())).isEqualTo("user");
        assertThat(provider.validateToken(token.accessToken())).isTrue();
        assertThat(token.expiresAt()).isAfter(OffsetDateTime.now(ZoneOffset.UTC));
    }

    @Test
    void validateTokenWhenTampered_NegTC() {
        JwtTokenProvider provider = createProvider(1000);
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null);
        JwtTokenProvider.JwtToken token = provider.generateToken(auth);

        String tampered = token.accessToken() + "a";

        assertThat(provider.validateToken(tampered)).isFalse();
    }

    @Test
    void validateTokenWhenExpired_NegTC() throws InterruptedException {
        JwtTokenProvider provider = createProvider(100);
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null);
        JwtTokenProvider.JwtToken token = provider.generateToken(auth);

        Thread.sleep(200);

        assertThat(provider.validateToken(token.accessToken())).isFalse();
    }

    @Test
    void constructWithShortSecret_NegTC() {
        String shortSecret = "short"; // length < 64 bytes
        assertThatThrownBy(() -> new JwtTokenProvider(shortSecret, 1000, new DateTimeUtil()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("64 bytes");
    }
}
