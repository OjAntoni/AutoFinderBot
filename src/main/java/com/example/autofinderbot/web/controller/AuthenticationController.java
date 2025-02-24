package com.example.autofinderbot.web.controller;

import com.example.autofinderbot.web.security.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    AuthenticationManager authenticationManager;
    JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public JwtAuthenticationResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        String token = tokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(token);
    }

    @Data
    public static class LoginRequest {
        @NotEmpty
        private String username;
        @NotEmpty
        private String password;
    }

    @Data
    public static class JwtAuthenticationResponse {
        private final String accessToken;
        private final String tokenType = "Bearer";
    }
}

