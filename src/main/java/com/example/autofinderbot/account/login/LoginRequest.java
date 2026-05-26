package com.example.autofinderbot.account.login;

import jakarta.validation.constraints.NotEmpty;

record LoginRequest(@NotEmpty String username, @NotEmpty String password) {
}
