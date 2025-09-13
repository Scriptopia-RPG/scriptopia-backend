package com.scriptopia.demo.controller;

import com.scriptopia.demo.service.UserCharacterImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/me")
@RequiredArgsConstructor
public class UserCharacterImgController {
    private final UserCharacterImgService userCharacterImgService;

    /*
    등록할 수 있는 이미지 저장
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/save/img")
    public ResponseEntity<?> saveCharacterImg(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Long userId = Long.valueOf(authentication.getName());

        return userCharacterImgService.saveCharacterImg(userId, file);
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
}
