package com.example.autofinderbot.account;

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

@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {
    AccountService accountService;

    @PostMapping
    @PreAuthorize("hasRole(T(com.example.autofinderbot.account.Account.Role).ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public void createAccount(@RequestBody @Valid CreateAccountRequest createAccountRequest) {
        accountService.createAccount(createAccountRequest);
    }
}
