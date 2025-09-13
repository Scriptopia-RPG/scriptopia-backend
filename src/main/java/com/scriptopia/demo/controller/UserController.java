package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.users.PiaItemDTO;
import com.scriptopia.demo.dto.users.UserAssetsResponse;
import com.scriptopia.demo.dto.users.UserSettingsDTO;
import com.scriptopia.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/items/pia")
    public ResponseEntity<List<PiaItemDTO>> getPiaItems(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        List<PiaItemDTO> response = userService.getPiaItems(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/settings")
    public ResponseEntity<UserSettingsDTO> getUserSettings(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        UserSettingsDTO response = userService.getUserSettings(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PutMapping("/settings")
    public ResponseEntity<String> updateUserSettings(
            Authentication authentication,
            @RequestBody @Valid UserSettingsDTO request
    ) {
        String userId = authentication.getName();
        userService.updateUserSettings(userId,request);
        return ResponseEntity.ok("사용자 설정이 변경되었습니다.");
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/assets")
    public ResponseEntity<UserAssetsResponse> getUserAssets(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        UserAssetsResponse response = userService.getUserAssets(userId);
        return ResponseEntity.ok(response);
    }



}
