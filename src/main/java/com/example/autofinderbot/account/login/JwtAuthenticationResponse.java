package com.example.autofinderbot.account.login;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
class JwtAuthenticationResponse {
    private final String accessToken;
    private final OffsetDateTime expiresAt;
    private final String tokenType = "Bearer";
}
