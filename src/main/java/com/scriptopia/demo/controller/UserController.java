package com.scriptopia.demo.controller;

import com.scriptopia.demo.domain.UserStatus;
import com.scriptopia.demo.dto.history.HistoryPageResponse;
import com.scriptopia.demo.dto.history.HistoryPageResponseDto;
import com.scriptopia.demo.dto.items.ItemDTO;
import com.scriptopia.demo.dto.users.*;
import com.scriptopia.demo.service.UserCharacterImgService;
import com.scriptopia.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
@Tag(name = "유저 관련 API", description = "유저 관련 API 입니다.")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserCharacterImgService userCharacterImgService;

    @Operation(summary = "보유 장비 아이템 조회")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/items/game")
    public ResponseEntity<List<ItemDTO>> getGameItems(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        List<ItemDTO> response = userService.getGameItems(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보유 피아 아이템 조회")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/items/pia")
    public ResponseEntity<List<PiaItemDTO>> getPiaItems(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        List<PiaItemDTO> response = userService.getPiaItems(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 옵션 조회")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/settings")
    public ResponseEntity<UserSettingsDTO> getUserSettings(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        UserSettingsDTO response = userService.getUserSettings(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 옵션 변경")
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

    @Operation(summary = "사용자 재화 조회")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/assets")
    public ResponseEntity<UserAssetsResponse> getUserAssets(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        UserAssetsResponse response = userService.getUserAssets(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 게임 기록 조회")
    @GetMapping("/games/histories")
    public ResponseEntity<HistoryPageResponseDto> getHistory(@RequestParam(required = false) UUID lastId,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(userService.fetchMyHistory(userId, lastId, size));
    }

    @Operation(summary = "프로필 이미지 변경")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/profile-images")
    public ResponseEntity<?> saveUserCharacterImg(Authentication authentication, @RequestBody UserImageRequest req) {
        Long userId = Long.valueOf(authentication.getName());

        return userCharacterImgService.saveUserCharacterImg(userId, req.getUrl());
    }

    @Operation(summary = "프로필 이미지 조회")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/profile-images")
    public ResponseEntity<?> getUserCharacterImgs(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return userCharacterImgService.getUserCharacterImg(userId);
    }

    @Operation(summary = "사용자 헤더 정보 조회")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<UserStatusResponse> getUserStatus(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(userService.getUserStatus(userId));
    }

}
