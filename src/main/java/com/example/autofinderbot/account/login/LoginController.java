package com.example.autofinderbot.account.login;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class LoginController {
    LoginService loginService;

    @PostMapping("/login")
    public JwtAuthenticationResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }
}
