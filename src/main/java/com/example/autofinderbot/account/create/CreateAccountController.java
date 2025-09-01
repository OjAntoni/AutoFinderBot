package com.example.autofinderbot.account.create;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class CreateAccountController {
    CreateAccountService service;

    @PostMapping
    @PreAuthorize("hasRole(T(com.example.autofinderbot.account.Account.Role).ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public void createAccount(@RequestBody @Valid CreateAccountRequest createAccountRequest) {
        service.createAccount(createAccountRequest);
    }
}

