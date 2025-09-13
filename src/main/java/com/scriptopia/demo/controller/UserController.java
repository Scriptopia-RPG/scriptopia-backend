package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.history.HistoryPageResponse;
import com.scriptopia.demo.dto.users.PiaItemDTO;
import com.scriptopia.demo.dto.users.UserAssetsResponse;
import com.scriptopia.demo.dto.users.UserSettingsDTO;
import com.scriptopia.demo.service.HistoryService;
import com.scriptopia.demo.service.UserCharacterImgService;
import com.scriptopia.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final HistoryService historyService;
    private final UserCharacterImgService userCharacterImgService;

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/items/game")
    public ResponseEntity<List<ItemDTO>> getGameItems(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        List<ItemDTO> response = userService.getGameItems(userId);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/my-page/history")
    public ResponseEntity<List<HistoryPageResponse>> getHistory(@RequestParam(required = false) UUID lastId,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return historyService.fetchMyHistory(userId, lastId, size);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/profile-images/url")
    public ResponseEntity<?> saveUserCharacterImg(Authentication authentication, @RequestParam("url") String url) {
        Long userId = Long.valueOf(authentication.getName());

        return userCharacterImgService.saveUserCharacterImg(userId, url);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/images")
    public ResponseEntity<?> getUserCharacterImgs(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return userCharacterImgService.getUserCharacterImg(userId);
    }

    /*
    등록할 수 있는 이미지 저장
    */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/save/img")
    public ResponseEntity<?> saveCharacterImg(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Long userId = Long.valueOf(authentication.getName());

        return userCharacterImgService.saveCharacterImg(userId, file);
    }
}
