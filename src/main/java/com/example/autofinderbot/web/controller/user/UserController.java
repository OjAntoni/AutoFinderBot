package com.example.autofinderbot.web.controller.user;

import com.example.autofinderbot.user.User;
import com.example.autofinderbot.web.service.WebUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    WebUserService webUserService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<User> getUsers(Pageable pageable) {
        return webUserService.getAllUsers(pageable);
    }
}
