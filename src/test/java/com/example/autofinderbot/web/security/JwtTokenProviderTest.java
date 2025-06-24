package com.example.autofinderbot.web.security;

import com.example.autofinderbot.shared.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider createProvider(long expirationMs) {
        String secret = "jwtSecret".repeat(9); // length > 64 bytes
        return new JwtTokenProvider(secret, expirationMs, new DateTimeUtil());
    }

    @Test
    void generateAndParseToken_PosTC() {
        JwtTokenProvider provider = createProvider(1000);
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null);

        String token = provider.generateToken(auth);

        assertThat(provider.getUsernameFromJWT(token)).isEqualTo("user");
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void validateTokenWhenTampered_NegTC() {
        JwtTokenProvider provider = createProvider(1000);
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null);
        String token = provider.generateToken(auth);

        String tampered = token + "a";

        assertThat(provider.validateToken(tampered)).isFalse();
    }

    @Test
    void validateTokenWhenExpired_NegTC() throws InterruptedException {
        JwtTokenProvider provider = createProvider(100);
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null);
        String token = provider.generateToken(auth);

        Thread.sleep(200);

        assertThat(provider.validateToken(token)).isFalse();
    }
}
