package com.scriptopia.demo.controller;

import com.scriptopia.demo.service.UserCharacterImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/img")
@RequiredArgsConstructor
public class UserCharacterImgController {
    private final UserCharacterImgService userCharacterImgService;

    @PostMapping("/save")
    public ResponseEntity<?> saveUserCharacterImg(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Long userId = Long.valueOf(authentication.getName());

        return userCharacterImgService.saveCharacterImg(userId, file);
    }
}
