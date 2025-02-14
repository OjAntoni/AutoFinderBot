package com.example.autofinderbot.web.controller.account;

import com.example.autofinderbot.web.dto.account.CreateAccountRequest;
import com.example.autofinderbot.web.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @PreAuthorize("hasRole(T(com.example.autofinderbot.web.domain.Account.Role).ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public void createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        accountService.createAccount(createAccountRequest);
    }
}
