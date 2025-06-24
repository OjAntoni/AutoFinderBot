package com.example.autofinderbot.web.controller.user;

import com.example.autofinderbot.domain.UserHistory;
import com.example.autofinderbot.web.service.WebUserHistoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/history")
public class UserHistoryController {
    WebUserHistoryService webUserHistoryService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<UserHistory> getUserHistories(Pageable pageable) {
        return webUserHistoryService.getUserHistory(pageable);
    }
}
