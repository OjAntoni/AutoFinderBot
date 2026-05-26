package com.example.autofinderbot.account.login;

import com.example.autofinderbot.common.config.web.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
class LoginService {
    AuthenticationManager authenticationManager;
    JwtTokenProvider tokenProvider;

    JwtAuthenticationResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        JwtTokenProvider.JwtToken token = tokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(token.accessToken(), token.expiresAt());
    }
}
