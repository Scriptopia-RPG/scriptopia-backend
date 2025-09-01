package com.scriptopia.demo.controller;

import com.scriptopia.demo.service.SharedGameFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SharedGameFavoriteService sharedGameFavoriteService;

    @PostMapping("/users/games/shared/{sharedGameId}/like")
    public ResponseEntity<?> likeSharedGame(@PathVariable Long sharedGameId, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameFavoriteService.saveFavorite(userId, sharedGameId);
    }
}
