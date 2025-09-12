package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.users.GetSettingsResponse;
import com.scriptopia.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/settings")
    public ResponseEntity<GetSettingsResponse> getUserSettings(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        GetSettingsResponse response = userService.getUserSettings(userId);
        return ResponseEntity.ok(response);
    }
}
